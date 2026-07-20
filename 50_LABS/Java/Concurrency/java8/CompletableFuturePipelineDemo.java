import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class CompletableFuturePipelineDemo {
    public static void main(String[] args) {
        ExecutorService ioExecutor = Executors.newFixedThreadPool(4);

        try {
            CompletableFuture<Customer> customerFuture =
                    CompletableFuture.supplyAsync(() -> loadCustomer(42), ioExecutor);

            CompletableFuture<Integer> limitFuture =
                    customerFuture.thenCompose(customer -> loadLimitAsync(customer, ioExecutor));

            CompletableFuture<List<String>> offersFuture =
                    customerFuture.thenCompose(customer -> loadOffersAsync(customer, ioExecutor));

            CompletableFuture<String> responseFuture =
                    limitFuture.thenCombine(
                            offersFuture,
                            (limit, offers) -> "limit=" + limit + ", offers=" + offers
                    ).exceptionally(error -> "fallback because: " + error.getMessage());

            System.out.println(responseFuture.join());
        } finally {
            ioExecutor.shutdown();
            try {
                if (!ioExecutor.awaitTermination(5, TimeUnit.SECONDS)) {
                    ioExecutor.shutdownNow();
                }
            } catch (InterruptedException e) {
                ioExecutor.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
    }

    private static Customer loadCustomer(int id) {
        sleep(100);
        return new Customer(id, "Amina");
    }

    private static CompletableFuture<Integer> loadLimitAsync(
            Customer customer,
            ExecutorService executor
    ) {
        return CompletableFuture.supplyAsync(() -> {
            sleep(120);
            return customer.id * 1_000;
        }, executor);
    }

    private static CompletableFuture<List<String>> loadOffersAsync(
            Customer customer,
            ExecutorService executor
    ) {
        return CompletableFuture.supplyAsync(() -> {
            sleep(80);
            return Arrays.asList("CARD", "CASH_LOAN");
        }, executor);
    }

    private static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Interrupted", e);
        }
    }

    private static final class Customer {
        private final int id;
        private final String name;

        private Customer(int id, String name) {
            this.id = id;
            this.name = name;
        }

        @Override
        public String toString() {
            return "Customer{id=" + id + ", name='" + name + "'}";
        }
    }
}
