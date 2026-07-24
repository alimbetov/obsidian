class OverrideStaticInstance {
    static class Parent { static void run() {} }
    static class Child extends Parent { void run() {} }
}
