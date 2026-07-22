public class VolatileFlagDemo {
    private static volatile boolean running = true;

    public static void main(String[] args) throws InterruptedException {
        Thread worker = new Thread(() -> {
            long iterations = 0;
            while (running) {
                iterations++;
            }
            System.out.println("Worker observed stop after " + iterations + " iterations");
        }, "volatile-flag-worker");

        worker.start();
        Thread.sleep(100);
        running = false;
        worker.join();

        System.out.println("Main finished");
    }
}
