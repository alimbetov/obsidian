package lab.b03;

public final class RecordPatternProof {
    private RecordPatternProof() {}

    record Point(int x, int y) {}
    record Box(Point point, String label) {}

    sealed interface Expr permits NumberExpr, AddExpr {}
    record NumberExpr(int value) implements Expr {}
    record AddExpr(Expr left, Expr right) implements Expr {}

    static String describe(Object value) {
        return switch (value) {
            case Box(Point(int x, int y), String label) when x == y -> label + ":diagonal:" + x;
            case Box(Point(int x, int y), String label) -> label + ":" + x + "," + y;
            case Point(int x, int y) -> "point:" + x + "," + y;
            case null -> "null";
            default -> "other";
        };
    }

    static int eval(Expr expr) {
        return switch (expr) {
            case NumberExpr(int value) -> value;
            case AddExpr(NumberExpr(int left), NumberExpr(int right)) -> left + right;
            case AddExpr(var left, var right) -> eval(left) + eval(right);
        };
    }

    public static void main(String[] args) {
        require("A:diagonal:2".equals(describe(new Box(new Point(2, 2), "A"))), "guarded nested pattern");
        require("B:2,3".equals(describe(new Box(new Point(2, 3), "B"))), "record deconstruction");
        require("null".equals(describe(null)), "null label");
        require(eval(new AddExpr(new NumberExpr(2), new AddExpr(new NumberExpr(3), new NumberExpr(4)))) == 9,
                "nested exhaustive record patterns");
        System.out.println("JAVA-B03 record-pattern proof PASS");
    }

    private static void require(boolean condition, String detail) {
        if (!condition) throw new AssertionError(detail);
    }
}
