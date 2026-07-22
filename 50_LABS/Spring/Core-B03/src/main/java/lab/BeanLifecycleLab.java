package lab;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.DestructionAwareBeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class BeanLifecycleLab {

    private BeanLifecycleLab() {
    }

    public static void main(String[] args) {
        Timeline.record("main: before context creation");

        try (AnnotationConfigApplicationContext context =
                     new AnnotationConfigApplicationContext(LabConfiguration.class)) {

            Timeline.record("main: context refreshed");

            LifecycleProbe probe = context.getBean(LifecycleProbe.class);
            BusinessService businessService = context.getBean(BusinessService.class);

            Timeline.record(
                    "main: businessService runtime class = " +
                            businessService.getClass().getName()
            );
            Timeline.record(
                    "main: businessService is JDK proxy = " +
                            Proxy.isProxyClass(businessService.getClass())
            );

            probe.work();
            businessService.execute("invoice-42");

            Timeline.record("main: leaving context scope");
        }

        Timeline.record("main: context closed");
        Timeline.print();
    }

    @Configuration
    static class LabConfiguration {

        @Bean
        static LifecycleTracingPostProcessor lifecycleTracingPostProcessor() {
            return new LifecycleTracingPostProcessor();
        }

        @Bean
        Dependency dependency() {
            Timeline.record("dependency: factory method");
            return new Dependency("database-client");
        }

        @Bean(initMethod = "customInit", destroyMethod = "customDestroy")
        LifecycleProbe lifecycleProbe(Dependency dependency) {
            Timeline.record("lifecycleProbe: factory method");
            return new LifecycleProbe(dependency);
        }

        @Bean
        BusinessService businessService() {
            Timeline.record("businessService: factory method");
            return new BusinessServiceImpl();
        }
    }

    static final class Dependency {
        private final String name;

        Dependency(String name) {
            this.name = name;
            Timeline.record("dependency: constructor");
        }

        String name() {
            return name;
        }
    }

    static final class LifecycleProbe implements
            BeanNameAware,
            BeanFactoryAware,
            ApplicationContextAware,
            InitializingBean,
            DisposableBean,
            SmartInitializingSingleton {

        private final Dependency dependency;
        private String beanName;

        LifecycleProbe(Dependency dependency) {
            this.dependency = dependency;
            Timeline.record("lifecycleProbe: constructor / raw instance created");
        }

        @Override
        public void setBeanName(String name) {
            this.beanName = name;
            Timeline.record("lifecycleProbe: BeanNameAware.setBeanName(" + name + ")");
        }

        @Override
        public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
            Timeline.record("lifecycleProbe: BeanFactoryAware.setBeanFactory");
        }

        @Override
        public void setApplicationContext(ApplicationContext applicationContext)
                throws BeansException {
            Timeline.record("lifecycleProbe: ApplicationContextAware.setApplicationContext");
        }

        @PostConstruct
        void postConstruct() {
            Timeline.record(
                    "lifecycleProbe: @PostConstruct, dependency=" + dependency.name()
            );
        }

        @Override
        public void afterPropertiesSet() {
            Timeline.record("lifecycleProbe: InitializingBean.afterPropertiesSet");
        }

        void customInit() {
            Timeline.record("lifecycleProbe: custom init-method");
        }

        @Override
        public void afterSingletonsInstantiated() {
            Timeline.record(
                    "lifecycleProbe: SmartInitializingSingleton.afterSingletonsInstantiated"
            );
        }

        void work() {
            Timeline.record("lifecycleProbe: business method on " + beanName);
        }

        @PreDestroy
        void preDestroy() {
            Timeline.record("lifecycleProbe: @PreDestroy");
        }

        @Override
        public void destroy() {
            Timeline.record("lifecycleProbe: DisposableBean.destroy");
        }

        void customDestroy() {
            Timeline.record("lifecycleProbe: custom destroy-method");
        }
    }

    interface BusinessService {
        String execute(String command);
    }

    static final class BusinessServiceImpl implements BusinessService {

        BusinessServiceImpl() {
            Timeline.record("businessService: constructor / raw target created");
        }

        @PostConstruct
        void initializeTarget() {
            Timeline.record("businessService: @PostConstruct on raw target");
        }

        @Override
        public String execute(String command) {
            Timeline.record("businessService target: execute(" + command + ")");
            return "processed:" + command;
        }
    }

    static final class LifecycleTracingPostProcessor implements
            BeanPostProcessor,
            DestructionAwareBeanPostProcessor,
            Ordered {

        @Override
        public int getOrder() {
            return Ordered.LOWEST_PRECEDENCE;
        }

        @Override
        public Object postProcessBeforeInitialization(Object bean, String beanName)
                throws BeansException {
            if (isObserved(beanName)) {
                Timeline.record(
                        beanName + ": custom BPP postProcessBeforeInitialization"
                );
            }
            return bean;
        }

        @Override
        public Object postProcessAfterInitialization(Object bean, String beanName)
                throws BeansException {
            if (isObserved(beanName)) {
                Timeline.record(
                        beanName + ": custom BPP postProcessAfterInitialization"
                );
            }

            if ("businessService".equals(beanName) && bean instanceof BusinessService) {
                Timeline.record("businessService: custom BPP creates JDK proxy");
                return createBusinessProxy((BusinessService) bean);
            }

            return bean;
        }

        @Override
        public void postProcessBeforeDestruction(Object bean, String beanName)
                throws BeansException {
            if (isObserved(beanName)) {
                Timeline.record(
                        beanName + ": DestructionAwareBPP.postProcessBeforeDestruction"
                );
            }
        }

        @Override
        public boolean requiresDestruction(Object bean) {
            return bean instanceof LifecycleProbe || bean instanceof BusinessService;
        }

        private static boolean isObserved(String beanName) {
            return "lifecycleProbe".equals(beanName) ||
                    "businessService".equals(beanName);
        }

        private static BusinessService createBusinessProxy(BusinessService target) {
            InvocationHandler handler = new InvocationHandler() {
                @Override
                public Object invoke(Object proxy, Method method, Object[] args)
                        throws Throwable {
                    if (method.getDeclaringClass() == Object.class) {
                        return method.invoke(target, args);
                    }

                    Timeline.record("businessService proxy: before " + method.getName());
                    try {
                        return method.invoke(target, args);
                    } finally {
                        Timeline.record("businessService proxy: after " + method.getName());
                    }
                }
            };

            return (BusinessService) Proxy.newProxyInstance(
                    BusinessService.class.getClassLoader(),
                    new Class<?>[]{BusinessService.class},
                    handler
            );
        }
    }

    static final class Timeline {
        private static final List<String> EVENTS =
                Collections.synchronizedList(new ArrayList<String>());

        private Timeline() {
        }

        static void record(String event) {
            EVENTS.add(event);
            System.out.println(event);
        }

        static void print() {
            System.out.println();
            System.out.println("=== ORDERED TIMELINE ===");
            synchronized (EVENTS) {
                for (int index = 0; index < EVENTS.size(); index++) {
                    System.out.printf("%02d. %s%n", index + 1, EVENTS.get(index));
                }
            }
        }
    }
}
