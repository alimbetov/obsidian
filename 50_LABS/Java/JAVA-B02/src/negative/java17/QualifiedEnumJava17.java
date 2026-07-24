final class QualifiedEnumJava17 {
    enum Color { RED, BLUE }

    int run(Color color) {
        return switch (color) {
            case Color.RED -> 1;
            case Color.BLUE -> 2;
        };
    }
}
