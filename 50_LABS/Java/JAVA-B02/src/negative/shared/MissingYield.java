final class MissingYield {
    int run(int value) {
        return switch (value) {
            case 1 -> {
                int result = 10;
            }
            default -> 0;
        };
    }
}
