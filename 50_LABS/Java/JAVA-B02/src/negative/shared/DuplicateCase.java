final class DuplicateCase {
    void run(int value) {
        switch (value) {
            case 1 + 1 -> System.out.println("A");
            case 2 -> System.out.println("B");
            default -> System.out.println("C");
        }
    }
}
