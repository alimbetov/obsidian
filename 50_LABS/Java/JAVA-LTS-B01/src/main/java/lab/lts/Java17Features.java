package lab.lts;

public final class Java17Features {

    private Java17Features() {
    }

    sealed interface Shape permits Circle, Rectangle {
    }

    record Circle(double radius) implements Shape {
    }

    record Rectangle(double width, double height) implements Shape {
    }

    static double area(Shape shape) {
        if (shape instanceof Circle circle) {
            return Math.PI * circle.radius() * circle.radius();
        }
        if (shape instanceof Rectangle rectangle) {
            return rectangle.width() * rectangle.height();
        }
        throw new IllegalArgumentException("Unsupported shape: " + shape);
    }

    static int priority(String level) {
        return switch (level) {
            case "LOW" -> 1;
            case "MEDIUM" -> 2;
            case "HIGH" -> 3;
            default -> throw new IllegalArgumentException("Unknown level: " + level);
        };
    }

    public static void main(String[] args) {
        String document = """
                {
                  "runtime": "java17",
                  "features": ["records", "sealed", "text-blocks", "switch-expression"]
                }
                """;

        double rectangleArea = area(new Rectangle(4, 5));
        int priority = priority("HIGH");

        if (rectangleArea != 20.0 || priority != 3 || !document.contains("records")) {
            throw new IllegalStateException("Unexpected Java 17 feature result");
        }

        System.out.println("java17.area=" + rectangleArea);
        System.out.println("java17.priority=" + priority);
        System.out.println(document.strip());
    }
}
