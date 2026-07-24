class FinalMethodOverride {
    static class Parent { final void run() {} }
    static class Child extends Parent { void run() {} }
}
