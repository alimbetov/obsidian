package lab.b06;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public final class ParallelReductionProof {
    public static void main(String[] args) {
        long sequential = IntStream.rangeClosed(1, 100_000).asLongStream().sum();
        long parallel = IntStream.rangeClosed(1, 100_000).parallel().asLongStream().sum();
        require(sequential == parallel, "associative sum must be stable");

        Map<Integer, Long> grouped = IntStream.range(0, 10_000)
                .parallel()
                .boxed()
                .collect(Collectors.groupingByConcurrent(value -> value % 10, Collectors.counting()));
        require(grouped.size() == 10, "ten partitions expected");
        require(grouped.values().stream().allMatch(count -> count == 1_000L), "balanced concurrent grouping");

        List<Integer> immutableResult = IntStream.range(0, 1_000)
                .parallel()
                .map(value -> value * 2)
                .boxed()
                .toList();
        require(immutableResult.size() == 1_000, "collector must preserve every value");
        require(immutableResult.get(999) == 1_998, "ordered source retains encounter order in toList");

        System.out.println("JAVA-B06 parallel proof PASS");
    }

    private static void require(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
    }
}
