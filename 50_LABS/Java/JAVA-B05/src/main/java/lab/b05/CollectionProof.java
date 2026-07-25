package lab.b05;

import java.util.*;

public final class CollectionProof {
    private CollectionProof() {}

    public static void main(String[] args) {
        beginnerBridge();
        interfaceContracts();
        equalityAndMutableKeys();
        ordering();
        generics();
        immutableBoundaries();
        System.out.println("JAVA-B05 JDK17 semantic proofs PASS");
    }

    static void beginnerBridge() {
        var unique = new HashSet<>(List.of("A", "B", "A"));
        check(unique.size() == 2, "set uniqueness");
    }

    static void interfaceContracts() {
        Queue<Integer> q = new ArrayDeque<>();
        q.offer(10); q.offer(20);
        check(q.poll() == 10, "FIFO");
        Deque<Integer> stack = new ArrayDeque<>();
        stack.push(1); stack.push(2);
        check(stack.pop() == 2, "LIFO");
        Map<String, Integer> map = new HashMap<>();
        check(map.put("A", 1) == null, "first put");
        check(map.put("A", 2) == 1, "replacement returns previous");
    }

    static void equalityAndMutableKeys() {
        record Id(long value) {}
        Map<Id, String> map = new HashMap<>();
        map.put(new Id(7), "Aida");
        check("Aida".equals(map.get(new Id(7))), "record key equality");

        MutableKey key = new MutableKey(1);
        Map<MutableKey, String> broken = new HashMap<>();
        broken.put(key, "value");
        key.id = 2;
        check(broken.get(key) == null, "mutable key demonstrates lost lookup");
    }

    static void ordering() {
        Comparator<String> byLength = Comparator.comparingInt(String::length);
        Set<String> values = new TreeSet<>(byLength);
        values.add("aa"); values.add("bb"); values.add("c");
        check(values.size() == 2, "comparison zero defines sorted uniqueness");
    }

    static void generics() {
        List<Integer> ints = List.of(1, 2, 3);
        check(total(ints) == 6.0, "extends producer");
        List<Number> numbers = new ArrayList<>();
        addDefaults(numbers);
        check(numbers.equals(List.of(0, 1)), "super consumer");
        check("x".equals(first(List.of("x", "y"))), "generic method inference");
    }

    static double total(List<? extends Number> source) {
        double result = 0;
        for (Number n : source) result += n.doubleValue();
        return result;
    }

    static void addDefaults(List<? super Integer> target) {
        target.add(0); target.add(1);
    }

    static <T> T first(List<T> source) {
        return source.get(0);
    }

    static void immutableBoundaries() {
        List<String> source = new ArrayList<>(List.of("A", "B"));
        List<String> view = Collections.unmodifiableList(source);
        List<String> snapshot = List.copyOf(source);
        source.add("C");
        check(view.size() == 3, "view reflects source");
        check(snapshot.size() == 2, "copy is snapshot");
    }

    static void check(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
    }

    static final class MutableKey {
        int id;
        MutableKey(int id) { this.id = id; }
        @Override public boolean equals(Object o) {
            return o instanceof MutableKey other && id == other.id;
        }
        @Override public int hashCode() { return Integer.hashCode(id); }
    }
}
