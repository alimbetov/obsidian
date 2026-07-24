package lab.b03;

public final class RecordsSealedProof {
    private RecordsSealedProof() {}

    record Money(int amount, String currency) {
        Money {
            if (amount < 0) throw new IllegalArgumentException("amount");
            currency = currency.toUpperCase();
        }
        String display() { return amount + " " + currency; }
    }

    sealed interface Shape permits Circle, Rectangle {}
    record Circle(double radius) implements Shape {}
    record Rectangle(double width, double height) implements Shape {}

    enum Phase {
        NEW(0), RUNNING(1), DONE(2);
        private final int code;
        Phase(int code) { this.code = code; }
        int code() { return code; }
    }

    static double area(Shape shape) {
        if (shape instanceof Circle c) return Math.PI * c.radius() * c.radius();
        if (shape instanceof Rectangle r) return r.width() * r.height();
        throw new AssertionError(shape);
    }

    public static void main(String[] args) {
        Money a = new Money(10, "usd");
        Money b = new Money(10, "USD");
        require(a.equals(b), "record structural equality");
        require("10 USD".equals(a.display()), "compact constructor normalization");
        require(a.toString().contains("amount=10"), "record toString");
        require(Phase.RUNNING.code() == 1, "enum state");
        require(area(new Rectangle(3, 4)) == 12.0, "sealed hierarchy");
        System.out.println("JAVA-B03 records/sealed proof PASS");
    }

    private static void require(boolean condition, String detail) {
        if (!condition) throw new AssertionError(detail);
    }
}
