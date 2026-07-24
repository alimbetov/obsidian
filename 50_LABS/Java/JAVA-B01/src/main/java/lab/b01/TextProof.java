package lab.b01;

public final class TextProof {

    private TextProof() {
    }

    public static void main(String[] args) {
        String literal = "java";
        String compileTime = "ja" + "va";
        check(literal == compileTime, "compile-time concatenation must use pooled literal");

        String part = "ja";
        String runtime = part + "va";
        check(literal != runtime, "runtime concatenation must not be the pooled literal reference");
        check(literal.equals(runtime), "runtime concatenation must preserve content");

        String immutable = "java";
        immutable.toUpperCase();
        check("java".equals(immutable), "String operation must not mutate original value");

        String csv = "a,b,";
        check(csv.split(",").length == 2, "default split must discard trailing empty value");
        check(csv.split(",", -1).length == 3, "negative split limit must retain trailing empty value");

        String textBlock = """
                A
                  B
                """;
        check("A\n  B\n".equals(textBlock),
                "text-block incidental indentation and final newline must match");

        String joined = """
                one\
                two
                """;
        check("onetwo\n".equals(joined), "line continuation must suppress one newline");

        StringBuilder first = new StringBuilder("ab");
        StringBuilder alias = first;
        alias.reverse();
        check("ba".contentEquals(first), "builder mutation must be visible through aliases");
        check(first == alias, "builder assignment must copy the reference");

        StringBuilder sameTextA = new StringBuilder("java");
        StringBuilder sameTextB = new StringBuilder("java");
        check(!sameTextA.equals(sameTextB), "StringBuilder equals must remain identity-based");
        check(sameTextA.toString().equals(sameTextB.toString()),
                "converted String values must compare by content");

        System.out.println("JAVA-B01 TextProof PASS");
    }

    private static void check(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }
}
