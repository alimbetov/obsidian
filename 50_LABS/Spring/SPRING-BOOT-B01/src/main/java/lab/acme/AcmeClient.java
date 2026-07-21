package lab.acme;

import java.util.Objects;

public final class AcmeClient {

    private final String endpoint;

    public AcmeClient(String endpoint) {
        this.endpoint = Objects.requireNonNull(endpoint, "endpoint");
    }

    public String endpoint() {
        return endpoint;
    }
}
