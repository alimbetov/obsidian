record Point(int x, int y) {}
class RecordPatternJava17 {
    static int sum(Object value) {
        return value instanceof Point(int x, int y) ? x + y : 0;
    }
}
