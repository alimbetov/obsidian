package lab.b06;

import java.util.IntSummaryStatistics;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public final class LambdaStreamProof {
    public static void main(String[] args) {
        functionalInterfaceAndCapture();
        lazinessAndShortCircuit();
        transformAndFlatten();
        reductionAndCollectors();
        primitiveStreams();
        singleUseBoundary();
        System.out.println("JAVA-B06 semantic proof PASS");
    }

    private static void functionalInterfaceAndCapture() {
        int minimum = 4;
        Predicate<String> accepted = value -> value.length() >= minimum;
        require(accepted.test("stream"), "lambda target typing/capture");
        require(!accepted.test("api"), "predicate false branch");
    }

    private static void lazinessAndShortCircuit() {
        int[] visited = {0};
        int first = IntStream.rangeClosed(1, 10)
                .peek(value -> visited[0]++)
                .filter(value -> value % 2 == 0)
                .findFirst()
                .orElseThrow();
        require(first == 2 && visited[0] == 2, "lazy vertical traversal and short circuit");
    }

    private static void transformAndFlatten() {
        List<List<String>> groups = List.of(List.of("beta", "alpha"), List.of("beta", "gamma"));
        List<String> result = groups.stream()
                .flatMap(List::stream)
                .filter(value -> value.length() >= 5)
                .distinct()
                .sorted()
                .toList();
        require(result.equals(List.of("alpha", "gamma")), "flatMap/filter/distinct/sorted");
    }

    private static void reductionAndCollectors() {
        int sum = List.of(1, 2, 3, 4).stream().reduce(0, Integer::sum);
        require(sum == 10, "associative reduction");

        Map<Integer, Long> counts = Stream.of("a", "bb", "c", "dd", "eee")
                .collect(Collectors.groupingBy(String::length, Collectors.counting()));
        require(counts.equals(Map.of(1, 2L, 2, 2L, 3, 1L)), "grouping downstream collector");
    }

    private static void primitiveStreams() {
        IntSummaryStatistics stats = IntStream.rangeClosed(1, 4).summaryStatistics();
        require(stats.getCount() == 4 && stats.getSum() == 10 && stats.getMax() == 4,
                "primitive stream statistics");
        require(IntStream.empty().average().isEmpty(), "empty average optional");
    }

    private static void singleUseBoundary() {
        Stream<Integer> stream = Stream.of(1, 2, 3);
        require(stream.count() == 3, "first terminal operation");
        try {
            stream.findFirst();
            throw new AssertionError("consumed stream must fail");
        } catch (IllegalStateException expected) {
            // expected proof boundary
        }
    }

    private static void require(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
    }
}
