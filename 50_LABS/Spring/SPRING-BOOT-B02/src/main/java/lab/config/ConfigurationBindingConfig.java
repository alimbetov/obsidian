package lab.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties({ClientProperties.class, DeliveryProperties.class})
public class ConfigurationBindingConfig {

    @Bean
    public ConfigurationSnapshot configurationSnapshot(
            ClientProperties client,
            DeliveryProperties delivery
    ) {
        return new ConfigurationSnapshot(client, delivery);
    }
}
