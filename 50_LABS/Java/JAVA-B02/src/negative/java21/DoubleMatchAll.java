final class DoubleMatchAll {
    int run(Object value) {
        return switch (value) {
            case Object object -> 1;
            default -> 0;
        };
    }
}
