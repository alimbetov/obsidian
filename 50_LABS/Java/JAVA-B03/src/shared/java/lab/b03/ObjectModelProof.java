package lab.b03;

public final class ObjectModelProof {
    private ObjectModelProof() {}

    interface Named {
        default String label() { return "named"; }
        private String prefix() { return "N:"; }
        default String decorated() { return prefix() + label(); }
        static String type() { return "interface"; }
    }

    static class Parent implements Named {
        static String kind = "parent-static";
        String value = "parent-field";
        @Override public String label() { return "parent"; }
        Number amount() { return 1; }
        static String who() { return "parent"; }
    }

    static final class Child extends Parent {
        static String kind = "child-static";
        String value = "child-field";
        @Override public String label() { return "child"; }
        @Override Integer amount() { return 2; }
        static String who() { return "child"; }
    }

    static String select(int value) { return "int"; }
    static String select(long value) { return "long"; }
    static String select(Integer value) { return "Integer"; }
    static String select(Object value) { return "Object"; }
    static String select(int... values) { return "varargs:" + values.length; }

    public static void main(String[] args) {
        Parent ref = new Child();
        require("child".equals(ref.label()), "override dispatch");
        require(ref.amount().intValue() == 2, "covariant return");
        require("parent-field".equals(ref.value), "field hiding is static by reference type");
        require("parent".equals(ref.who()), "static method hiding");
        require("N:child".equals(ref.decorated()), "interface private/default composition");
        require("int".equals(select(1)), "exact primitive overload");
        require("Integer".equals(select(Integer.valueOf(1))), "wrapper overload");
        require("varargs:0".equals(select()), "varargs fallback");
        require("interface".equals(Named.type()), "static interface method");
        System.out.println("JAVA-B03 object-model proof PASS");
    }

    private static void require(boolean condition, String detail) {
        if (!condition) throw new AssertionError(detail);
    }
}
