package lab.acme;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.FilteredClassLoader;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;

class AcmeAutoConfigurationTest {

    private final ApplicationContextRunner contextRunner =
            new ApplicationContextRunner()
                    .withConfiguration(
                            AutoConfigurations.of(AcmeAutoConfiguration.class)
                    );

    @Test
    void createsDefaultClientWhenConditionsMatch() {
        contextRunner.run(context -> {
            assertThat(context).hasSingleBean(AcmeClient.class);
            assertThat(context.getBean(AcmeClient.class).endpoint())
                    .isEqualTo("https://default.example.test");
        });
    }

    @Test
    void bindsEndpointFromEnvironment() {
        contextRunner
                .withPropertyValues("acme.endpoint=https://configured.example.test")
                .run(context -> {
                    assertThat(context).hasSingleBean(AcmeClient.class);
                    assertThat(context.getBean(AcmeClient.class).endpoint())
                            .isEqualTo("https://configured.example.test");
                });
    }

    @Test
    void backsOffWhenFeatureIsDisabled() {
        contextRunner
                .withPropertyValues("acme.enabled=false")
                .run(context -> assertThat(context)
                        .doesNotHaveBean(AcmeClient.class));
    }

    @Test
    void backsOffWhenUserProvidesClient() {
        AcmeClient custom = new AcmeClient("https://custom.example.test");

        contextRunner
                .withBean(AcmeClient.class, () -> custom)
                .run(context -> {
                    assertThat(context).hasSingleBean(AcmeClient.class);
                    assertThat(context.getBean(AcmeClient.class))
                            .isSameAs(custom);
                });
    }

    @Test
    void backsOffWhenOptionalLibraryClassIsMissing() {
        contextRunner
                .withClassLoader(new FilteredClassLoader(AcmeLibraryMarker.class))
                .run(context -> assertThat(context)
                        .doesNotHaveBean(AcmeClient.class));
    }

    @Test
    void registersAutoConfigurationInBoot2SpringFactories() throws IOException {
        Properties factories = new Properties();

        try (InputStream input = getClass().getClassLoader()
                .getResourceAsStream("META-INF/spring.factories")) {
            assertThat(input).isNotNull();
            factories.load(input);
        }

        assertThat(factories.getProperty(
                "org.springframework.boot.autoconfigure.EnableAutoConfiguration"
        )).contains("lab.acme.AcmeAutoConfiguration");
    }
}
