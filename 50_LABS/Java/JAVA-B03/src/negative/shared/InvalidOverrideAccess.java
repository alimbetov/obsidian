class InvalidOverrideAccess {
    static class Parent { protected void run() {} }
    static class Child extends Parent { private void run() {} }
}
