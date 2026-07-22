package lab.config;

import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Import;
import org.springframework.util.unit.DataSize;

import java.net.URI;
import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

class ConfigurationBindingTest {

    private final ApplicationContextRunner contextRunner =
            new ApplicationContextRunner()
                    .withUserConfiguration(ConfigurationBindingConfig.class)
                    .withPropertyValues(
                            "client.endpoint=https://api.example.test",
                            "delivery.endpoint=https://delivery.example.test",
                            "delivery.batch-size=10"
                    );

    @Test
    void bindsRelaxedNamesAndTypedUnits() {
        contextRunner
                .withPropertyValues(
                        "client.timeout=750ms",
                        "client.max-payload=4MB",
                        "client.retry.max_attempts=5",
                        "client.retry.backoff=2s"
                )
                .run(context -> {
                    assertThat(context).hasNotFailed();
                    ClientProperties properties = context.getBean(ClientProperties.class);
                    assertThat(properties.getEndpoint())
                            .isEqualTo(URI.create("https://api.example.test"));
                    assertThat(properties.getTimeout()).isEqualTo(Duration.ofMillis(750));
                    assertThat(properties.getMaxPayload()).isEqualTo(DataSize.ofMegabytes(4));
                    assertThat(properties.getRetry().getMaxAttempts()).isEqualTo(5);
                    assertThat(properties.getRetry().getBackoff()).isEqualTo(Duration.ofSeconds(2));
                });
    }

    @Test
    void bindsNestedCollectionsAndMaps() {
        contextRunner
                .withPropertyValues(
                        "client.servers[0]=https://a.example.test",
                        "client.servers[1]=https://b.example.test",
                        "client.headers[X-Tenant]=retail"
                )
                .run(context -> {
                    ClientProperties properties = context.getBean(ClientProperties.class);
                    assertThat(properties.getServers()).containsExactly(
                            URI.create("https://a.example.test"),
                            URI.create("https://b.example.test")
                    );
                    assertThat(properties.getHeaders()).containsEntry("X-Tenant", "retail");
                });
    }

    @Test
    void validatesMissingRequiredEndpoint() {
        new ApplicationContextRunner()
                .withUserConfiguration(ConfigurationBindingConfig.class)
                .withPropertyValues(
                        "delivery.endpoint=https://delivery.example.test",
                        "delivery.batch-size=10"
                )
                .run(context -> {
                    assertThat(context).hasFailed();
                    assertThat(context.getStartupFailure()).hasMessageContaining("client");
                });
    }

    @Test
    void validatesNestedRetryConstraint() {
        contextRunner
                .withPropertyValues("client.retry.max-attempts=0")
                .run(context -> {
                    assertThat(context).hasFailed();
                    assertThat(context.getStartupFailure()).hasMessageContaining("client");
                });
    }

    @Test
    void bindsBoot25ConstructorProperties() {
        contextRunner.run(context -> {
            DeliveryProperties properties = context.getBean(DeliveryProperties.class);
            assertThat(properties.getEndpoint())
                    .isEqualTo(URI.create("https://delivery.example.test"));
            assertThat(properties.getBatchSize()).isEqualTo(10);
        });
    }

    @Test
    void programmaticBinderUsesThePreparedEnvironment() {
        contextRunner
                .withPropertyValues("client.timeout=9s")
                .run(context -> {
                    ClientProperties properties = Binder.get(context.getEnvironment())
                            .bind("client", Bindable.of(ClientProperties.class))
                            .orElseThrow(IllegalStateException::new);
                    assertThat(properties.getTimeout()).isEqualTo(Duration.ofSeconds(9));
                });
    }

    @Test
    void configDataLoadsImportAndProfileSpecificOverride() {
        SpringApplication application = new SpringApplication(B02TestApplication.class);
        application.setWebApplicationType(WebApplicationType.NONE);
        application.setLogStartupInfo(false);

        try (ConfigurableApplicationContext context = application.run(
                "--spring.config.name=application-b02",
                "--spring.profiles.active=prod",
                "--spring.main.banner-mode=off"
        )) {
            ClientProperties properties = context.getBean(ClientProperties.class);
            assertThat(properties.getEndpoint())
                    .isEqualTo(URI.create("https://prod.example.test"));
            assertThat(properties.getTimeout()).isEqualTo(Duration.ofSeconds(1));
            assertThat(properties.getHeaders()).containsEntry("Imported", "yes");
            assertThat(context.getEnvironment().getActiveProfiles()).containsExactly("prod");
        }
    }

    @SpringBootConfiguration
    @Import(ConfigurationBindingConfig.class)
    static class B02TestApplication {
    }
}
