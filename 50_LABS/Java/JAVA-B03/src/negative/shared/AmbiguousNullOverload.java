class AmbiguousNullOverload {
    static void call(String value) {}
    static void call(Integer value) {}
    public static void main(String[] args) { call(null); }
}
