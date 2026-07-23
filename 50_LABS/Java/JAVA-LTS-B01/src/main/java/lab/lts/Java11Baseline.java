package lab.lts;

import java.net.http.HttpClient;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public final class Java11Baseline {

    private Java11Baseline() {
    }

    public static void main(String[] args) {
        List<String> values = List.of("alpha", " ", "beta");

        List<String> nonBlank = values.stream()
                .filter(Predicate.not(String::isBlank))
                .map(value -> value.repeat(2))
                .collect(Collectors.toList());

        HttpClient client = HttpClient.newBuilder().build();

        if (!nonBlank.equals(List.of("alphaalpha", "betabeta"))) {
            throw new IllegalStateException("Unexpected Java 11 String/Stream result: " + nonBlank);
        }

        System.out.println("java11.http.version=" + client.version());
        System.out.println("java11.values=" + nonBlank);
    }
}
