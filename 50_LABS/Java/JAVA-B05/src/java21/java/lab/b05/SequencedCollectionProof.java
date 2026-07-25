package lab.b05;

import java.util.*;

public final class SequencedCollectionProof {
    private SequencedCollectionProof() {}

    public static void main(String[] args) {
        SequencedCollection<String> values = new ArrayList<>();
        values.addFirst("B");
        values.addFirst("A");
        values.addLast("C");
        check(values.getFirst().equals("A"), "first");
        check(values.getLast().equals("C"), "last");

        SequencedCollection<String> reversed = values.reversed();
        check(reversed.getFirst().equals("C"), "reversed first");
        values.addLast("D");
        check(reversed.getFirst().equals("D"), "reversed is view");

        SequencedMap<Integer, String> map = new LinkedHashMap<>();
        map.put(1, "A");
        map.put(2, "B");
        check(map.firstEntry().getKey() == 1, "first entry");
        check(map.reversed().firstEntry().getKey() == 2, "reversed map view");

        System.out.println("JAVA-B05 JDK21 sequenced proofs PASS");
    }

    static void check(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
    }
}
