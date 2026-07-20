package lab;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.config.CustomScopeConfigurer;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.context.support.SimpleThreadScope;
import org.springframework.core.io.Resource;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public final class AdvancedCoreLab {

    private AdvancedCoreLab() {
    }

    public static void main(String[] args) throws Exception {
        runScopeFactoryLazyResourceAndMessageExperiments();
        runConstructorCycleExperiment();
        runParentChildContextExperiment();
    }

    private static void runScopeFactoryLazyResourceAndMessageExperiments()
            throws Exception {
        System.out.println("\n=== SCOPES, FACTORYBEAN, LAZY, RESOURCE, MESSAGE ===");

        PrototypeToken.resetCounters();
        LazyProbe.reset();

        try (AnnotationConfigApplicationContext context =
                     new AnnotationConfigApplicationContext(AdvancedCoreConfig.class)) {

            DirectPrototypeConsumer direct =
                    context.getBean(DirectPrototypeConsumer.class);
            ProviderPrototypeConsumer provider =
                    context.getBean(ProviderPrototypeConsumer.class);

            System.out.println("direct token #1 = " + direct.tokenId());
            System.out.println("direct token #2 = " + direct.tokenId());

            PrototypeToken firstFresh = provider.fresh();
            PrototypeToken secondFresh = provider.fresh();
            System.out.println("provider token #1 = " + firstFresh.id());
            System.out.println("provider token #2 = " + secondFresh.id());

            firstFresh.close();
            secondFresh.close();

            runThreadScopeExperiment(context);
            runFactoryBeanExperiment(context);

            System.out.println("lazy created before lookup = " + LazyProbe.createdCount());
            context.getBean(LazyProbe.class).touch();
            System.out.println("lazy created after lookup = " + LazyProbe.createdCount());

            Resource resource = context.getResource("classpath:core-b06.txt");
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(
                            resource.getInputStream(),
                            StandardCharsets.UTF_8
                    )
            )) {
                System.out.println("resource text = " + reader.readLine());
            }

            System.out.println(context.getMessage(
                    "greeting",
                    new Object[]{"Ruslan"},
                    Locale.ENGLISH
            ));
            System.out.println(context.getMessage(
                    "greeting",
                    new Object[]{"Руслан"},
                    new Locale("ru")
            ));
        }

        System.out.println("prototype created = " + PrototypeToken.createdCount());
        System.out.println("prototype explicitly closed = " + PrototypeToken.closedCount());
        System.out.println("captured prototype was not auto-destroyed by context");
    }

    private static void runThreadScopeExperiment(
            AnnotationConfigApplicationContext context
    ) throws InterruptedException {
        System.out.println("\n--- thread-scoped proxy ---");

        ThreadState proxy = context.getBean(ThreadState.class);
        String mainFirst = proxy.id();
        String mainSecond = proxy.id();

        AtomicReference<String> workerValues = new AtomicReference<String>();
        Thread worker = new Thread(() -> workerValues.set(
                proxy.id() + " / " + proxy.id()
        ), "scope-worker");
        worker.start();
        worker.join();

        System.out.println("proxy class = " + proxy.getClass().getName());
        System.out.println("main thread targets = " + mainFirst + " / " + mainSecond);
        System.out.println("worker thread targets = " + workerValues.get());
        System.out.println("same target inside one thread = " +
                mainFirst.equals(mainSecond));
        System.out.println("different target between threads = " +
                !workerValues.get().startsWith(mainFirst + " /"));
    }

    private static void runFactoryBeanExperiment(
            AnnotationConfigApplicationContext context
    ) {
        System.out.println("\n--- FactoryBean ---");

        Object productOne = context.getBean("demoProduct");
        Object productTwo = context.getBean("demoProduct");
        Object factory = context.getBean("&demoProduct");

        System.out.println("normal lookup type = " + productOne.getClass().getName());
        System.out.println("factory lookup type = " + factory.getClass().getName());
        System.out.println("singleton product identity = " +
                (productOne == productTwo));
    }

    private static void runConstructorCycleExperiment() {
        System.out.println("\n=== CONSTRUCTOR CYCLE ===");

        AnnotationConfigApplicationContext context =
                new AnnotationConfigApplicationContext();
        context.register(ConstructorCycleConfig.class);

        try {
            context.refresh();
            System.out.println("unexpected: cycle context started");
        } catch (BeansException expected) {
            System.out.println("expected startup failure = " +
                    rootCause(expected).getClass().getSimpleName());
        } finally {
            context.close();
        }
    }

    private static void runParentChildContextExperiment() {
        System.out.println("\n=== PARENT / CHILD CONTEXT ===");

        try (AnnotationConfigApplicationContext parent =
                     new AnnotationConfigApplicationContext(ParentConfig.class)) {

            AnnotationConfigApplicationContext child =
                    new AnnotationConfigApplicationContext();
            child.setParent(parent);
            child.register(ChildConfig.class);
            child.refresh();

            try {
                LabelService parentLabel =
                        parent.getBean("labelService", LabelService.class);
                LabelService childLabel =
                        child.getBean("labelService", LabelService.class);

                System.out.println("parent label = " + parentLabel.label());
                System.out.println("child label = " + childLabel.label());
                System.out.println("child sees parentOnly = " +
                        child.getBean("parentOnly", String.class));

                try {
                    parent.getBean("childOnly");
                    System.out.println("unexpected: parent saw child bean");
                } catch (NoSuchBeanDefinitionException expected) {
                    System.out.println("parent cannot see childOnly = true");
                }
            } finally {
                child.close();
            }
        }
    }

    private static Throwable rootCause(Throwable error) {
        Throwable current = error;
        while (current.getCause() != null) {
            current = current.getCause();
        }
        return current;
    }

    @Configuration
    static class AdvancedCoreConfig {

        @Bean
        static CustomScopeConfigurer customScopeConfigurer() {
            CustomScopeConfigurer configurer = new CustomScopeConfigurer();
            Map<String, Object> scopes = new HashMap<String, Object>();
            scopes.put("thread", new SimpleThreadScope());
            configurer.setScopes(scopes);
            return configurer;
        }

        @Bean
        @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
        PrototypeToken prototypeToken() {
            return new PrototypeToken();
        }

        @Bean
        DirectPrototypeConsumer directPrototypeConsumer(
                PrototypeToken token
        ) {
            return new DirectPrototypeConsumer(token);
        }

        @Bean
        ProviderPrototypeConsumer providerPrototypeConsumer(
                ObjectProvider<PrototypeToken> tokens
        ) {
            return new ProviderPrototypeConsumer(tokens);
        }

        @Bean
        @Scope(value = "thread", proxyMode = ScopedProxyMode.INTERFACES)
        ThreadState threadState() {
            return new ThreadStateImpl();
        }

        @Bean
        ThreadStateConsumer threadStateConsumer(ThreadState state) {
            return new ThreadStateConsumer(state);
        }

        @Bean
        DemoProductFactoryBean demoProduct() {
            return new DemoProductFactoryBean();
        }

        @Bean
        @Lazy
        LazyProbe lazyProbe() {
            return new LazyProbe();
        }

        @Bean(name = "messageSource")
        MessageSource messageSource() {
            ResourceBundleMessageSource source =
                    new ResourceBundleMessageSource();
            source.setBasename("messages");
            source.setDefaultEncoding("UTF-8");
            return source;
        }
    }

    static final class PrototypeToken implements AutoCloseable {
        private static final AtomicInteger CREATED = new AtomicInteger();
        private static final AtomicInteger CLOSED = new AtomicInteger();
        private final int id = CREATED.incrementAndGet();
        private boolean closed;

        int id() {
            return id;
        }

        @Override
        public void close() {
            if (!closed) {
                closed = true;
                CLOSED.incrementAndGet();
            }
        }

        static void resetCounters() {
            CREATED.set(0);
            CLOSED.set(0);
        }

        static int createdCount() {
            return CREATED.get();
        }

        static int closedCount() {
            return CLOSED.get();
        }
    }

    static final class DirectPrototypeConsumer {
        private final PrototypeToken token;

        DirectPrototypeConsumer(PrototypeToken token) {
            this.token = token;
        }

        int tokenId() {
            return token.id();
        }
    }

    static final class ProviderPrototypeConsumer {
        private final ObjectProvider<PrototypeToken> tokens;

        ProviderPrototypeConsumer(ObjectProvider<PrototypeToken> tokens) {
            this.tokens = tokens;
        }

        PrototypeToken fresh() {
            return tokens.getObject();
        }
    }

    interface ThreadState {
        String id();
    }

    static final class ThreadStateImpl implements ThreadState {
        private static final AtomicInteger IDS = new AtomicInteger();
        private final String id = Thread.currentThread().getName() +
                "-target-" + IDS.incrementAndGet();

        @Override
        public String id() {
            return id;
        }
    }

    static final class ThreadStateConsumer {
        private final ThreadState state;

        ThreadStateConsumer(ThreadState state) {
            this.state = state;
        }

        String current() {
            return state.id();
        }
    }

    static final class DemoProduct {
        private final int id;

        DemoProduct(int id) {
            this.id = id;
        }

        int id() {
            return id;
        }
    }

    static final class DemoProductFactoryBean
            implements FactoryBean<DemoProduct> {
        private final AtomicInteger ids = new AtomicInteger();
        private DemoProduct singleton;

        @Override
        public DemoProduct getObject() {
            if (singleton == null) {
                singleton = new DemoProduct(ids.incrementAndGet());
            }
            return singleton;
        }

        @Override
        public Class<?> getObjectType() {
            return DemoProduct.class;
        }

        @Override
        public boolean isSingleton() {
            return true;
        }
    }

    static final class LazyProbe {
        private static final AtomicInteger CREATED = new AtomicInteger();

        LazyProbe() {
            CREATED.incrementAndGet();
            System.out.println("LazyProbe constructor");
        }

        void touch() {
            System.out.println("LazyProbe touch");
        }

        static void reset() {
            CREATED.set(0);
        }

        static int createdCount() {
            return CREATED.get();
        }
    }

    @Configuration
    static class ConstructorCycleConfig {
        @Bean
        CycleA cycleA(CycleB b) {
            return new CycleA(b);
        }

        @Bean
        CycleB cycleB(CycleA a) {
            return new CycleB(a);
        }
    }

    static final class CycleA {
        CycleA(CycleB b) {
        }
    }

    static final class CycleB {
        CycleB(CycleA a) {
        }
    }

    interface LabelService {
        String label();
    }

    @Configuration
    static class ParentConfig {
        @Bean
        LabelService labelService() {
            return () -> "parent-label";
        }

        @Bean
        String parentOnly() {
            return "parent-visible-from-child";
        }
    }

    @Configuration
    static class ChildConfig {
        @Bean
        LabelService labelService() {
            return () -> "child-label";
        }

        @Bean
        String childOnly() {
            return "child-local";
        }
    }
}
