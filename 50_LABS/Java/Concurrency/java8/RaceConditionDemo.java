import java.util.concurrent.CountDownLatch;

public class RaceConditionDemo {
    private static int counter;

    public static void main(String[] args) throws InterruptedException {
        int threads = 8;
        int incrementsPerThread = 250_000;
        CountDownLatch start = new CountDownLatch(1);
        CountDownLatch done = new CountDownLatch(threads);

        for (int i = 0; i < threads; i++) {
            Thread worker = new Thread(() -> {
                try {
                    start.await();
                    for (int j = 0; j < incrementsPerThread; j++) {
                        counter++;
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    done.countDown();
                }
            }, "counter-worker-" + i);
            worker.start();
        }

        start.countDown();
        done.await();

        int expected = threads * incrementsPerThread;
        System.out.println("Expected: " + expected);
        System.out.println("Actual:   " + counter);
        System.out.println("Lost:     " + (expected - counter));
    }
}
