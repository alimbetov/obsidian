package lab.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.net.URI;

@Validated
@ConstructorBinding
@ConfigurationProperties(prefix = "delivery")
public class DeliveryProperties {

    @NotNull
    private final URI endpoint;

    @Min(1)
    private final int batchSize;

    public DeliveryProperties(URI endpoint, int batchSize) {
        this.endpoint = endpoint;
        this.batchSize = batchSize;
    }

    public URI getEndpoint() {
        return endpoint;
    }

    public int getBatchSize() {
        return batchSize;
    }
}
