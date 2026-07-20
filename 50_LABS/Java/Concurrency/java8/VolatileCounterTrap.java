import java.util.concurrent.CountDownLatch;

public class VolatileCounterTrap {
    private static volatile int counter;

    public static void main(String[] args) throws InterruptedException {
        int threads = 8;
        int incrementsPerThread = 250_000;
        CountDownLatch start = new CountDownLatch(1);
        CountDownLatch done = new CountDownLatch(threads);

        for (int i = 0; i < threads; i++) {
            new Thread(() -> {
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
            }, "volatile-counter-worker-" + i).start();
        }

        start.countDown();
        done.await();

        int expected = threads * incrementsPerThread;
        System.out.println("Expected: " + expected);
        System.out.println("Actual:   " + counter);
        System.out.println("volatile made writes visible, but increment was still not atomic");
    }
}
