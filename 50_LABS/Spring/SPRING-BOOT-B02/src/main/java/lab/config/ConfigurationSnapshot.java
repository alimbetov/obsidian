package lab.config;

public final class ConfigurationSnapshot {

    private final ClientProperties client;
    private final DeliveryProperties delivery;

    public ConfigurationSnapshot(ClientProperties client, DeliveryProperties delivery) {
        this.client = client;
        this.delivery = delivery;
    }

    public ClientProperties getClient() {
        return client;
    }

    public DeliveryProperties getDelivery() {
        return delivery;
    }
}
