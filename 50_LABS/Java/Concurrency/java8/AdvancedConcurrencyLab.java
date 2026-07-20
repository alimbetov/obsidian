import java.util.Iterator;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.LongAdder;

public class AdvancedConcurrencyLab {
    public static void main(String[] args) throws Exception {
        String mode = args.length == 0 ? "all" : args[0];
        if ("atomic".equals(mode) || "all".equals(mode)) atomicCounter();
        if ("cas".equals(mode) || "all".equals(mode)) casStateMachine();
        if ("adder".equals(mode) || "all".equals(mode)) longAdderStatistics();
        if ("deadlock".equals(mode) || "all".equals(mode)) deadlockObservation();
        if ("ordered".equals(mode) || "all".equals(mode)) orderedTransfer();
        if ("map".equals(mode) || "all".equals(mode)) concurrentMapCompoundAction();
        if ("cow".equals(mode) || "all".equals(mode)) copyOnWriteSnapshot();
        if ("queue".equals(mode) || "all".equals(mode)) blockingQueueBackpressure();
    }

    private static void atomicCounter() throws InterruptedException {
        AtomicInteger counter = new AtomicInteger();
        int threads = 8;
        int increments = 100_000;
        CountDownLatch start = new CountDownLatch(1);
        CountDownLatch done = new CountDownLatch(threads);
        for (int i = 0; i < threads; i++) {
            new Thread(() -> {
                try {
                    start.await();
                    for (int j = 0; j < increments; j++) counter.incrementAndGet();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    done.countDown();
                }
            }).start();
        }
        start.countDown();
        done.await();
        System.out.println("atomic expected=" + threads * increments + ", actual=" + counter.get());
    }

    private static void casStateMachine() {
        AtomicReference<State> state = new AtomicReference<State>(State.NEW);
        System.out.println("CAS NEW->RUNNING: " + state.compareAndSet(State.NEW, State.RUNNING));
        System.out.println("CAS NEW->RUNNING again: " + state.compareAndSet(State.NEW, State.RUNNING));
        System.out.println("CAS RUNNING->STOPPED: " + state.compareAndSet(State.RUNNING, State.STOPPED));
    }

    private static void longAdderStatistics() throws InterruptedException {
        LongAdder adder = new LongAdder();
        int threads = 8;
        CountDownLatch done = new CountDownLatch(threads);
        for (int i = 0; i < threads; i++) {
            new Thread(() -> {
                for (int j = 0; j < 200_000; j++) adder.increment();
                done.countDown();
            }).start();
        }
        done.await();
        System.out.println("LongAdder statistics=" + adder.sum());
    }

    private static void deadlockObservation() throws InterruptedException {
        Object left = new Object();
        Object right = new Object();
        Thread first = new Thread(() -> lockInOrder(left, right), "left-then-right");
        Thread second = new Thread(() -> lockInOrder(right, left), "right-then-left");
        first.setDaemon(true);
        second.setDaemon(true);
        first.start();
        second.start();
        Thread.sleep(300);
        System.out.println("deadlock states=" + first.getState() + ", " + second.getState());
    }

    private static void lockInOrder(Object first, Object second) {
        synchronized (first) {
            sleep(80);
            synchronized (second) {
                System.out.println("both locks acquired");
            }
        }
    }

    private static void orderedTransfer() throws InterruptedException {
        Account a = new Account(1, 1_000);
        Account b = new Account(2, 1_000);
        Thread t1 = new Thread(() -> repeatTransfer(a, b));
        Thread t2 = new Thread(() -> repeatTransfer(b, a));
        t1.start();
        t2.start();
        t1.join();
        t2.join();
        System.out.println("ordered total=" + (a.balance + b.balance));
    }

    private static void repeatTransfer(Account from, Account to) {
        for (int i = 0; i < 10_000; i++) transfer(from, to, 1);
    }

    private static void transfer(Account from, Account to, int amount) {
        Account first = from.id < to.id ? from : to;
        Account second = from.id < to.id ? to : from;
        synchronized (first) {
            synchronized (second) {
                if (from.balance >= amount) {
                    from.balance -= amount;
                    to.balance += amount;
                }
            }
        }
    }

    private static void concurrentMapCompoundAction() throws InterruptedException {
        ConcurrentHashMap<String, String> cache = new ConcurrentHashMap<String, String>();
        AtomicInteger loads = new AtomicInteger();
        int threads = 20;
        CountDownLatch start = new CountDownLatch(1);
        CountDownLatch done = new CountDownLatch(threads);
        for (int i = 0; i < threads; i++) {
            new Thread(() -> {
                try {
                    start.await();
                    cache.computeIfAbsent("customer-42", key -> {
                        loads.incrementAndGet();
                        return "value-for-" + key;
                    });
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    done.countDown();
                }
            }).start();
        }
        start.countDown();
        done.await();
        System.out.println("mapping function calls=" + loads.get());
    }

    private static void copyOnWriteSnapshot() {
        CopyOnWriteArrayList<String> list = new CopyOnWriteArrayList<String>();
        list.add("A");
        list.add("B");
        Iterator<String> snapshot = list.iterator();
        list.add("C");
        StringBuilder seen = new StringBuilder();
        while (snapshot.hasNext()) seen.append(snapshot.next());
        System.out.println("snapshot=" + seen + ", current=" + list);
    }

    private static void blockingQueueBackpressure() throws InterruptedException {
        BlockingQueue<String> queue = new ArrayBlockingQueue<String>(2);
        Thread consumer = new Thread(() -> {
            try {
                for (int i = 0; i < 5; i++) {
                    String value = queue.take();
                    sleep(80);
                    System.out.println("consumed=" + value + ", depth=" + queue.size());
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        consumer.start();
        for (int i = 1; i <= 5; i++) {
            queue.put("job-" + i);
            System.out.println("produced=job-" + i + ", depth=" + queue.size());
        }
        consumer.join();
    }

    private static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private enum State { NEW, RUNNING, STOPPED }

    private static final class Account {
        private final long id;
        private int balance;
        private Account(long id, int balance) {
            this.id = id;
            this.balance = balance;
        }
    }
}
