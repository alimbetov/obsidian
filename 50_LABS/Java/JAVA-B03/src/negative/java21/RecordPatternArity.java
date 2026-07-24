record Point(int x, int y) {}
class RecordPatternArity {
    static int read(Object value) {
        return switch (value) { case Point(int x) -> x; default -> 0; };
    }
}
