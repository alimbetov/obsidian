package lab.b02;

public final class PatternSwitchProof {

    sealed interface Token permits Word, NumberToken {
    }

    record Word(String text) implements Token {
    }

    record NumberToken(int value) implements Token {
    }

    enum Signal {
        RED, GREEN
    }

    private PatternSwitchProof() {
    }

    public static void main(String[] args) {
        check(describe("abc").equals("text:3"), "String pattern must bind the selected value");
        check(describe(" ").equals("blank"), "guarded String pattern must run before fallback");
        check(describe(7).equals("int:7"), "Integer pattern must be selected");
        check(describe(null).equals("null"), "explicit null label must handle null");
        check(describe(2.5).equals("other"), "default must handle unmatched non-null values");

        check(tokenValue(new Word("java")) == 4, "sealed Word case must be exhaustive");
        check(tokenValue(new NumberToken(7)) == 7, "sealed NumberToken case must be exhaustive");

        check(longReference(null) == -1, "null Long reference must select null label");
        check(longReference(42L) == 42, "Long reference selector must support a Long pattern");

        check(signalCode(Signal.RED) == 10, "qualified enum constant RED must compile on Java 21");
        check(signalCode(Signal.GREEN) == 20, "qualified enum constant GREEN must compile on Java 21");

        check(rest("value").equals("text"), "String must select its specific case");
        check(rest(42).equals("rest"), "unmatched non-null must select case null, default");
        check(rest(null).equals("rest"), "null must select case null, default");

        System.out.println("JAVA-B02 PatternSwitchProof PASS");
    }

    static String describe(Object value) {
        return switch (value) {
            case null -> "null";
            case String s when s.isBlank() -> "blank";
            case String s -> "text:" + s.length();
            case Integer i -> "int:" + i;
            default -> "other";
        };
    }

    static int tokenValue(Token token) {
        return switch (token) {
            case Word word -> word.text().length();
            case NumberToken number -> number.value();
        };
    }

    static int longReference(Long value) {
        return switch (value) {
            case null -> -1;
            case Long number -> number.intValue();
        };
    }

    static int signalCode(Signal signal) {
        return switch (signal) {
            case Signal.RED -> 10;
            case Signal.GREEN -> 20;
        };
    }

    static String rest(Object value) {
        return switch (value) {
            case String s -> "text";
            case null, default -> "rest";
        };
    }

    private static void check(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }
}
