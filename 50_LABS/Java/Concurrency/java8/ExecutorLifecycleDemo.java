import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class ExecutorLifecycleDemo {
    public static void main(String[] args) {
        ExecutorService executor = Executors.newFixedThreadPool(3);
        List<Future<Integer>> futures = new ArrayList<Future<Integer>>();

        try {
            for (int value = 1; value <= 5; value++) {
                final int input = value;
                Callable<Integer> task = () -> {
                    if (input == 4) {
                        throw new IllegalStateException("Demonstration failure for " + input);
                    }
                    return input * input;
                };
                futures.add(executor.submit(task));
            }

            for (Future<Integer> future : futures) {
                try {
                    System.out.println("Result: " + future.get());
                } catch (ExecutionException e) {
                    System.out.println("Task failed: " + e.getCause());
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        } finally {
            shutdownGracefully(executor);
        }
    }

    private static void shutdownGracefully(ExecutorService executor) {
        executor.shutdown();
        try {
            if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                executor.shutdownNow();
                if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                    System.err.println("Executor did not terminate");
                }
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
