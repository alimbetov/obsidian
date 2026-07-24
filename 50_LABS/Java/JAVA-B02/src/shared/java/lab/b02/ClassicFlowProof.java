package lab.b02;

public final class ClassicFlowProof {

    enum State {
        NEW, RUNNING, DONE
    }

    private ClassicFlowProof() {
    }

    public static void main(String[] args) {
        check(danglingElse() == 2, "else must bind to nearest unmatched if");
        check(forContinueTrace().equals("023"), "continue must execute for-update before rechecking");
        check(labeledBreakCount() == 4, "labeled break must exit both loops");
        check(enhancedForReassignment().equals("123"), "enhanced-for reassignment must not update array");
        check(instanceofPattern("java").equals("JAVA"), "instanceof pattern must bind in flow-proven scope");
        check(classicFallThrough(2).equals("BC"), "colon switch must fall through");
        check(switchExpression(2) == 22, "yield must produce block-arm value");
        check(enumCode(State.RUNNING) == 1, "exhaustive enum switch expression must select RUNNING");
        check(legacyStatementNoMatch(99).equals("none"), "legacy switch statement may be non-exhaustive");
        System.out.println("JAVA-B02 ClassicFlowProof PASS");
    }

    static int danglingElse() {
        int x = 0;
        if (true)
            if (false)
                x = 1;
            else
                x = 2;
        return x;
    }

    static String forContinueTrace() {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < 4; i++) {
            if (i == 1) {
                continue;
            }
            result.append(i);
        }
        return result.toString();
    }

    static int labeledBreakCount() {
        int count = 0;
        outer:
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (i == 1 && j == 1) {
                    break outer;
                }
                count++;
            }
        }
        return count;
    }

    static String enhancedForReassignment() {
        int[] values = {1, 2, 3};
        for (int value : values) {
            value *= 10;
        }
        StringBuilder result = new StringBuilder();
        for (int value : values) {
            result.append(value);
        }
        return result.toString();
    }

    static String instanceofPattern(Object value) {
        if (value instanceof String text && text.length() > 3) {
            return text.toUpperCase();
        }
        return "other";
    }

    static String classicFallThrough(int n) {
        StringBuilder result = new StringBuilder();
        switch (n) {
            case 1:
                result.append("A");
            case 2:
                result.append("B");
            default:
                result.append("C");
        }
        return result.toString();
    }

    static int switchExpression(int n) {
        return switch (n) {
            case 1 -> 10;
            case 2 -> {
                int base = 20;
                yield base + 2;
            }
            default -> 0;
        };
    }

    static int enumCode(State state) {
        return switch (state) {
            case NEW -> 0;
            case RUNNING -> 1;
            case DONE -> 2;
        };
    }

    static String legacyStatementNoMatch(int n) {
        String result = "none";
        switch (n) {
            case 1:
                result = "one";
                break;
            case 2:
                result = "two";
                break;
        }
        return result;
    }

    private static void check(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }
}
