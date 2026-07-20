import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class VirtualThreadDemo {
    public static void main(String[] args) throws Exception {
        int taskCount = 10_000;
        Instant started = Instant.now();

        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            List<Future<Integer>> futures = new ArrayList<>(taskCount);

            for (int i = 0; i < taskCount; i++) {
                final int taskId = i;
                futures.add(executor.submit(() -> {
                    Thread.sleep(50);
                    return taskId;
                }));
            }

            long checksum = 0;
            for (Future<Integer> future : futures) {
                checksum += future.get();
            }

            System.out.println("Tasks:    " + taskCount);
            System.out.println("Checksum: " + checksum);
            System.out.println("Elapsed:  " + Duration.between(started, Instant.now()));
        }
    }
}
