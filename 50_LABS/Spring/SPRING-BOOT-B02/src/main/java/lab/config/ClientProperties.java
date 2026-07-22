package lab.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.convert.DataSizeUnit;
import org.springframework.boot.convert.DurationUnit;
import org.springframework.util.unit.DataSize;
import org.springframework.util.unit.DataUnit;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.net.URI;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Validated
@ConfigurationProperties(prefix = "client")
public class ClientProperties {

    @NotNull
    private URI endpoint;

    @DurationUnit(ChronoUnit.SECONDS)
    private Duration timeout = Duration.ofSeconds(5);

    @DataSizeUnit(DataUnit.MEGABYTES)
    private DataSize maxPayload = DataSize.ofMegabytes(1);

    @Valid
    private Retry retry = new Retry();

    private List<URI> servers = new ArrayList<>();

    private Map<String, String> headers = new LinkedHashMap<>();

    public URI getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(URI endpoint) {
        this.endpoint = endpoint;
    }

    public Duration getTimeout() {
        return timeout;
    }

    public void setTimeout(Duration timeout) {
        this.timeout = timeout;
    }

    public DataSize getMaxPayload() {
        return maxPayload;
    }

    public void setMaxPayload(DataSize maxPayload) {
        this.maxPayload = maxPayload;
    }

    public Retry getRetry() {
        return retry;
    }

    public void setRetry(Retry retry) {
        this.retry = retry;
    }

    public List<URI> getServers() {
        return servers;
    }

    public void setServers(List<URI> servers) {
        this.servers = servers;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public static class Retry {

        @Min(1)
        private int maxAttempts = 3;

        @DurationUnit(ChronoUnit.MILLIS)
        private Duration backoff = Duration.ofMillis(200);

        public int getMaxAttempts() {
            return maxAttempts;
        }

        public void setMaxAttempts(int maxAttempts) {
            this.maxAttempts = maxAttempts;
        }

        public Duration getBackoff() {
            return backoff;
        }

        public void setBackoff(Duration backoff) {
            this.backoff = backoff;
        }
    }
}
