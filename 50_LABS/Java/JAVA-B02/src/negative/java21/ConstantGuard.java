final class ConstantGuard {
    int run(Object value) {
        return switch (value) {
            case "x" when true -> 1;
            default -> 0;
        };
    }
}
