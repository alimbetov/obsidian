final class DominatedPattern {
    int run(Object value) {
        return switch (value) {
            case CharSequence sequence -> 1;
            case String text -> 2;
            default -> 0;
        };
    }
}
