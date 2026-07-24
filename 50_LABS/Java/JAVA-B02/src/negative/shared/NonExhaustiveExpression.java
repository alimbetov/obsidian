final class NonExhaustiveExpression {
    int run(int value) {
        return switch (value) {
            case 1 -> 10;
            case 2 -> 20;
        };
    }
}
