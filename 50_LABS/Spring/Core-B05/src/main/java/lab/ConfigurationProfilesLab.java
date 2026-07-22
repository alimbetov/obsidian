package lab;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.PropertySource;

import java.util.Collections;
import java.util.concurrent.atomic.AtomicInteger;

public final class ConfigurationProfilesLab {

    private ConfigurationProfilesLab() {
    }

    public static void main(String[] args) {
        runFullConfigurationExperiment();
        runLiteConfigurationExperiment();
        runProfileAndPropertyExperiment("dev");
        runProfileAndPropertyExperiment("prod");
    }

    private static void runFullConfigurationExperiment() {
        System.out.println("\n=== FULL CONFIGURATION ===");

        try (AnnotationConfigApplicationContext context =
                     new AnnotationConfigApplicationContext(FullConfig.class)) {

            Repository managedRepository = context.getBean(Repository.class);
            OrderService orderService = context.getBean(OrderService.class);

            System.out.println("managed repository id = " + managedRepository.id());
            System.out.println("service repository id = " + orderService.repository().id());
            System.out.println("same instance = " +
                    (managedRepository == orderService.repository()));
        }
    }

    private static void runLiteConfigurationExperiment() {
        System.out.println("\n=== PROXY BEAN METHODS FALSE ===");

        try (AnnotationConfigApplicationContext context =
                     new AnnotationConfigApplicationContext(LiteConfig.class)) {

            Repository managedRepository = context.getBean(Repository.class);
            OrderService orderService = context.getBean(OrderService.class);

            System.out.println("managed repository id = " + managedRepository.id());
            System.out.println("service repository id = " + orderService.repository().id());
            System.out.println("same instance = " +
                    (managedRepository == orderService.repository()));
        }
    }

    private static void runProfileAndPropertyExperiment(String profile) {
        System.out.println("\n=== PROFILE AND PROPERTY: " + profile + " ===");

        AnnotationConfigApplicationContext context =
                new AnnotationConfigApplicationContext();

        ConfigurableEnvironment environment = context.getEnvironment();
        environment.setActiveProfiles(profile);
        environment.getPropertySources().addFirst(
                new MapPropertySource(
                        "runtimeOverride",
                        Collections.<String, Object>singletonMap(
                                "client.timeout-ms",
                                "2500"
                        )
                )
        );

        context.register(RootConfig.class);
        context.refresh();

        try {
            Transport transport = context.getBean(Transport.class);
            ClientSettings settings = context.getBean(ClientSettings.class);

            System.out.println("active profile = " + profile);
            System.out.println("transport = " + transport.name());
            System.out.println("client name = " + settings.name());
            System.out.println("client timeout = " + settings.timeoutMs());
            System.out.println("client endpoint = " + settings.endpoint());

            printSourcesContaining(
                    environment,
                    "client.timeout-ms"
            );
        } finally {
            context.close();
        }
    }

    private static void printSourcesContaining(
            ConfigurableEnvironment environment,
            String key
    ) {
        System.out.println("sources containing " + key + ":");

        for (PropertySource<?> source : environment.getPropertySources()) {
            Object value = source.getProperty(key);
            if (value != null) {
                System.out.println(
                        "  " + source.getName() + " -> " + value
                );
            }
        }
    }

    @Configuration
    static class FullConfig {

        @Bean
        Repository repository() {
            return new Repository();
        }

        @Bean
        OrderService orderService() {
            return new OrderService(repository());
        }
    }

    @Configuration(proxyBeanMethods = false)
    static class LiteConfig {

        @Bean
        Repository repository() {
            return new Repository();
        }

        @Bean
        OrderService orderService() {
            return new OrderService(repository());
        }
    }

    @Configuration
    @Import({
            DevTransportConfig.class,
            ProdTransportConfig.class,
            PropertyConfig.class
    })
    static class RootConfig {
    }

    @Configuration(proxyBeanMethods = false)
    @Profile("dev")
    static class DevTransportConfig {

        @Bean
        Transport transport() {
            return new NamedTransport("logging-transport");
        }
    }

    @Configuration(proxyBeanMethods = false)
    @Profile("prod")
    static class ProdTransportConfig {

        @Bean
        Transport transport() {
            return new NamedTransport("remote-transport");
        }
    }

    @Configuration(proxyBeanMethods = false)
    @org.springframework.context.annotation.PropertySource(
            "classpath:core-b05.properties"
    )
    static class PropertyConfig {

        @Bean
        static PropertySourcesPlaceholderConfigurer placeholders() {
            return new PropertySourcesPlaceholderConfigurer();
        }

        @Bean
        ClientSettings clientSettings(
                @Value("${client.name}") String name,
                @Value("${client.timeout-ms:1000}") int timeoutMs,
                @Value("${client.endpoint}") String endpoint
        ) {
            return new ClientSettings(name, timeoutMs, endpoint);
        }
    }

    interface Transport {
        String name();
    }

    static final class NamedTransport implements Transport {
        private final String name;

        NamedTransport(String name) {
            this.name = name;
        }

        @Override
        public String name() {
            return name;
        }
    }

    static final class Repository {
        private static final AtomicInteger IDS = new AtomicInteger();
        private final int id = IDS.incrementAndGet();

        int id() {
            return id;
        }
    }

    static final class OrderService {
        private final Repository repository;

        OrderService(Repository repository) {
            this.repository = repository;
        }

        Repository repository() {
            return repository;
        }
    }

    static final class ClientSettings {
        private final String name;
        private final int timeoutMs;
        private final String endpoint;

        ClientSettings(String name, int timeoutMs, String endpoint) {
            if (name == null || name.trim().isEmpty()) {
                throw new IllegalArgumentException("name is required");
            }
            if (timeoutMs <= 0) {
                throw new IllegalArgumentException(
                        "timeoutMs must be positive"
                );
            }
            if (endpoint == null || endpoint.trim().isEmpty()) {
                throw new IllegalArgumentException("endpoint is required");
            }

            this.name = name;
            this.timeoutMs = timeoutMs;
            this.endpoint = endpoint;
        }

        String name() {
            return name;
        }

        int timeoutMs() {
            return timeoutMs;
        }

        String endpoint() {
            return endpoint;
        }
    }
}
