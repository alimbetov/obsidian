package lab.b03;

public final class InitializationProof {
    private InitializationProof() {}

    private static final StringBuilder TRACE = new StringBuilder();

    static class Base {
        static { TRACE.append("BS>"); }
        int baseField = mark("BF>", 1);
        { TRACE.append("BI>"); }
        Base() { TRACE.append("BC>"); }
    }

    static final class Child extends Base {
        static { TRACE.append("CS>"); }
        int childField = mark("CF>", 2);
        { TRACE.append("CI>"); }
        Child() { this(7); TRACE.append("C0>"); }
        Child(int value) { TRACE.append("C1:").append(value).append('>'); }
    }

    private static int mark(String token, int value) {
        TRACE.append(token);
        return value;
    }

    public static void main(String[] args) {
        new Child();
        String expected = "BS>CS>BF>BI>BC>CF>CI>C1:7>C0>";
        require(expected.equals(TRACE.toString()), TRACE.toString());
        System.out.println("JAVA-B03 initialization proof PASS");
    }

    private static void require(boolean condition, String detail) {
        if (!condition) throw new AssertionError(detail);
    }
}
