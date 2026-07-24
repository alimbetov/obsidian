package lab.b01;

public final class ValuesProof {

    private ValuesProof() {
    }

    public static void main(String[] args) {
        byte overflow = 120;
        overflow += 10;
        check(overflow == -126, "compound assignment must narrow with byte overflow");

        char letter = 'A';
        check(letter + 1 == 66, "char must promote to int");

        check(7 / 3 == 2, "positive integer division must truncate toward zero");
        check(-7 / 3 == -2, "negative integer division must truncate toward zero");
        check(-7 % 3 == -1, "remainder must keep dividend sign");

        double nan = 0.0 / 0.0;
        check(nan != nan, "NaN must be unequal to itself");
        check(Double.isNaN(nan), "Double.isNaN must identify NaN");

        Integer cachedA = 127;
        Integer cachedB = 127;
        check(cachedA == cachedB, "required Integer cache range must preserve identity");

        Integer largerA = 128;
        Integer largerB = 128;
        check(largerA.equals(largerB), "wrapper equals must compare value");

        boolean unboxingFailed = false;
        try {
            Integer nullable = null;
            int ignored = nullable;
            System.out.println(ignored);
        } catch (NullPointerException expected) {
            unboxingFailed = true;
        }
        check(unboxingFailed, "unboxing null must throw NullPointerException");

        check(Integer.parseInt("101", 2) == 5, "radix parsing must use supplied base");
        check(Math.abs(Integer.MIN_VALUE) == Integer.MIN_VALUE,
                "Integer.MIN_VALUE has no positive int counterpart");

        boolean exactOverflowDetected = false;
        try {
            Math.addExact(Integer.MAX_VALUE, 1);
        } catch (ArithmeticException expected) {
            exactOverflowDetected = true;
        }
        check(exactOverflowDetected, "Math.addExact must detect overflow");

        System.out.println("JAVA-B01 ValuesProof PASS");
    }

    private static void check(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }
}
