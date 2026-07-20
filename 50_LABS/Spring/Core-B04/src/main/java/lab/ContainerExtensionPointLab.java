package lab;

import org.springframework.beans.BeansException;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.DestructionAwareBeanPostProcessor;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessor;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.PriorityOrdered;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class ContainerExtensionPointLab {

    private ContainerExtensionPointLab() {
    }

    public static void main(String[] args) {
        Timeline.record("main: create context without refresh");

        AnnotationConfigApplicationContext context =
                new AnnotationConfigApplicationContext();

        context.register(LabConfiguration.class);

        context.getBeanFactory().addBeanPostProcessor(
                new ProgrammaticTracingPostProcessor("programmatic-1")
        );
        context.getBeanFactory().addBeanPostProcessor(
                new ProgrammaticTracingPostProcessor("programmatic-2")
        );

        Timeline.record("main: refresh context");
        context.refresh();

        ConfigurableClient client = context.getBean(ConfigurableClient.class);
        WorkService service = context.getBean(WorkService.class);
        DynamicPlugin plugin = context.getBean(DynamicPlugin.class);

        Timeline.record("main: client timeout = " + client.getTimeoutMs());
        Timeline.record("main: service runtime class = " + service.getClass().getName());
        Timeline.record("main: service is JDK proxy = " + Proxy.isProxyClass(service.getClass()));
        Timeline.record("main: dynamic plugin = " + plugin.name());

        service.execute("invoice-42");

        Timeline.record("main: close context");
        context.close();

        Timeline.print();
    }

    @Configuration
    static class LabConfiguration {

        @Bean
        static PluginRegistryPostProcessor pluginRegistryPostProcessor() {
            return new PluginRegistryPostProcessor();
        }

        @Bean
        static TimeoutMetadataPostProcessor timeoutMetadataPostProcessor() {
            return new TimeoutMetadataPostProcessor();
        }

        @Bean
        static InstantiationTracingPostProcessor instantiationTracingPostProcessor() {
            return new InstantiationTracingPostProcessor();
        }

        @Bean
        static AuditProxyPostProcessor auditProxyPostProcessor() {
            return new AuditProxyPostProcessor();
        }

        @Bean
        static LifecycleCleanupPostProcessor lifecycleCleanupPostProcessor() {
            return new LifecycleCleanupPostProcessor();
        }

        @Bean
        ConfigurableClient configurableClient() {
            Timeline.record("configurableClient: factory method");
            return new ConfigurableClient();
        }

        @Bean
        WorkService workService() {
            Timeline.record("workService: factory method / raw target");
            return new AuditedWorkService();
        }
    }

    static final class PluginRegistryPostProcessor
            implements BeanDefinitionRegistryPostProcessor, PriorityOrdered {

        @Override
        public void postProcessBeanDefinitionRegistry(
                BeanDefinitionRegistry registry
        ) throws BeansException {
            Timeline.record("BDRPP: register dynamicPlugin definition");

            BeanDefinition definition = BeanDefinitionBuilder
                    .genericBeanDefinition(DynamicPlugin.class)
                    .addConstructorArgValue("registry-plugin")
                    .getBeanDefinition();

            registry.registerBeanDefinition("dynamicPlugin", definition);
        }

        @Override
        public void postProcessBeanFactory(
                ConfigurableListableBeanFactory beanFactory
        ) throws BeansException {
            Timeline.record("BDRPP: inherited postProcessBeanFactory");
        }

        @Override
        public int getOrder() {
            return Ordered.HIGHEST_PRECEDENCE;
        }
    }

    static final class TimeoutMetadataPostProcessor
            implements BeanFactoryPostProcessor, Ordered {

        @Override
        public void postProcessBeanFactory(
                ConfigurableListableBeanFactory beanFactory
        ) throws BeansException {
            Timeline.record("BFPP: modify configurableClient BeanDefinition");

            BeanDefinition definition =
                    beanFactory.getBeanDefinition("configurableClient");

            definition.getPropertyValues().add("timeoutMs", 2500);
        }

        @Override
        public int getOrder() {
            return 100;
        }
    }

    static final class ProgrammaticTracingPostProcessor
            implements BeanPostProcessor, PriorityOrdered {

        private final String id;

        ProgrammaticTracingPostProcessor(String id) {
            this.id = id;
        }

        @Override
        public Object postProcessBeforeInitialization(
                Object bean,
                String beanName
        ) throws BeansException {
            if (isApplicationBean(beanName)) {
                Timeline.record(id + ": beforeInit " + beanName);
            }
            return bean;
        }

        @Override
        public Object postProcessAfterInitialization(
                Object bean,
                String beanName
        ) throws BeansException {
            if (isApplicationBean(beanName)) {
                Timeline.record(id + ": afterInit " + beanName);
            }
            return bean;
        }

        @Override
        public int getOrder() {
            return Ordered.LOWEST_PRECEDENCE;
        }
    }

    static final class InstantiationTracingPostProcessor
            implements InstantiationAwareBeanPostProcessor, PriorityOrdered {

        @Override
        public Object postProcessBeforeInstantiation(
                Class<?> beanClass,
                String beanName
        ) throws BeansException {
            if (isApplicationBean(beanName)) {
                Timeline.record("IABPP: beforeInstantiation " + beanName);
            }
            return null;
        }

        @Override
        public boolean postProcessAfterInstantiation(
                Object bean,
                String beanName
        ) throws BeansException {
            if (isApplicationBean(beanName)) {
                Timeline.record("IABPP: afterInstantiation " + beanName);
            }
            return true;
        }

        @Override
        public PropertyValues postProcessProperties(
                PropertyValues pvs,
                Object bean,
                String beanName
        ) throws BeansException {
            if (isApplicationBean(beanName)) {
                Timeline.record("IABPP: postProcessProperties " + beanName);
            }
            return pvs;
        }

        @Override
        public int getOrder() {
            return 0;
        }
    }

    static final class AuditProxyPostProcessor
            implements BeanPostProcessor, Ordered {

        @Override
        public Object postProcessAfterInitialization(
                Object bean,
                String beanName
        ) throws BeansException {
            Class<?> targetClass = bean.getClass();
            if (!targetClass.isAnnotationPresent(Audited.class)) {
                return bean;
            }

            Timeline.record("AuditBPP: create JDK proxy for " + beanName);

            return Proxy.newProxyInstance(
                    targetClass.getClassLoader(),
                    targetClass.getInterfaces(),
                    (proxy, method, args) -> invokeAudited(beanName, bean, method, args)
            );
        }

        private Object invokeAudited(
                String beanName,
                Object target,
                Method method,
                Object[] args
        ) throws Throwable {
            Timeline.record("audit proxy: before " + beanName + "." + method.getName());
            try {
                Object result = method.invoke(target, args);
                Timeline.record("audit proxy: after " + beanName + "." + method.getName());
                return result;
            } catch (InvocationTargetException error) {
                throw error.getTargetException();
            }
        }

        @Override
        public int getOrder() {
            return 200;
        }
    }

    static final class LifecycleCleanupPostProcessor
            implements DestructionAwareBeanPostProcessor, Ordered {

        @Override
        public void postProcessBeforeDestruction(
                Object bean,
                String beanName
        ) throws BeansException {
            if (requiresDestruction(bean)) {
                Timeline.record("DestructionBPP: beforeDestruction " + beanName);
            }
        }

        @Override
        public boolean requiresDestruction(Object bean) {
            return bean instanceof ConfigurableClient || bean instanceof DynamicPlugin;
        }

        @Override
        public int getOrder() {
            return 300;
        }
    }

    static final class ConfigurableClient {
        private int timeoutMs;

        ConfigurableClient() {
            Timeline.record("configurableClient: constructor");
        }

        public void setTimeoutMs(int timeoutMs) {
            Timeline.record("configurableClient: setTimeoutMs(" + timeoutMs + ")");
            this.timeoutMs = timeoutMs;
        }

        int getTimeoutMs() {
            return timeoutMs;
        }
    }

    interface WorkService {
        void execute(String requestId);
    }

    @Audited
    static final class AuditedWorkService implements WorkService {

        AuditedWorkService() {
            Timeline.record("workService: constructor");
        }

        @Override
        public void execute(String requestId) {
            Timeline.record("workService target: execute " + requestId);
        }
    }

    static final class DynamicPlugin {
        private final String name;

        DynamicPlugin(String name) {
            this.name = name;
            Timeline.record("dynamicPlugin: constructor " + name);
        }

        String name() {
            return name;
        }
    }

    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.RUNTIME)
    @interface Audited {
    }

    private static boolean isApplicationBean(String beanName) {
        return "configurableClient".equals(beanName)
                || "workService".equals(beanName)
                || "dynamicPlugin".equals(beanName);
    }

    static final class Timeline {
        private static final List<String> EVENTS =
                Collections.synchronizedList(new ArrayList<String>());

        private Timeline() {
        }

        static void record(String event) {
            EVENTS.add(event);
        }

        static void print() {
            System.out.println();
            System.out.println("=== CONTAINER EXTENSION POINT TIMELINE ===");
            int index = 1;
            for (String event : EVENTS) {
                System.out.printf("%02d. %s%n", index++, event);
            }
        }
    }
}
