final class NonExhaustivePatternStatement {
    void run(Object value) {
        switch (value) {
            case String text -> System.out.println(text);
        }
    }
}
