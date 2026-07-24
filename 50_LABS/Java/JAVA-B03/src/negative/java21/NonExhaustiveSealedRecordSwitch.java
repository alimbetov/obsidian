sealed interface Expr permits Num, Add {}
record Num(int value) implements Expr {}
record Add(Expr left, Expr right) implements Expr {}
class NonExhaustiveSealedRecordSwitch {
    static int eval(Expr expr) {
        return switch (expr) { case Num(int value) -> value; };
    }
}
