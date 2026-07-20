import java.util.concurrent.CountDownLatch;

public class SynchronizedCounterDemo {
    private static final Counter COUNTER = new Counter();

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
                        COUNTER.increment();
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    done.countDown();
                }
            }, "safe-counter-worker-" + i);
            worker.start();
        }

        start.countDown();
        done.await();

        int expected = threads * incrementsPerThread;
        System.out.println("Expected: " + expected);
        System.out.println("Actual:   " + COUNTER.value());
    }

    private static final class Counter {
        private int value;

        synchronized void increment() {
            value++;
        }

        synchronized int value() {
            return value;
        }
    }
}
