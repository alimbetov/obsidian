final class UnreachableWhile {
    void run() {
        while (false) {
            System.out.println("never");
        }
    }
}
