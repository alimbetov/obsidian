package lab.lts;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public final class Java21Features {

    private Java21Features() {
    }

    sealed interface Shape permits Circle, Rectangle {
    }

    record Circle(double radius) implements Shape {
    }

    record Rectangle(double width, double height) implements Shape {
    }

    static double area(Shape shape) {
        return switch (shape) {
            case Circle(double radius) -> Math.PI * radius * radius;
            case Rectangle(double width, double height) -> width * height;
        };
    }

    public static void main(String[] args) throws Exception {
        List<String> versions = new ArrayList<>(List.of("11", "17", "21"));

        if (!versions.getFirst().equals("11") || !versions.getLast().equals("21")) {
            throw new IllegalStateException("Unexpected sequenced collection behavior");
        }

        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            Future<Double> result = executor.submit(() -> area(new Rectangle(6, 7)));
            if (result.get() != 42.0) {
                throw new IllegalStateException("Unexpected virtual-thread result");
            }
        }

        double circleArea = area(new Circle(2));
        if (circleArea <= 12.5 || circleArea >= 12.6) {
            throw new IllegalStateException("Unexpected record-pattern result: " + circleArea);
        }

        System.out.println("java21.first=" + versions.getFirst());
        System.out.println("java21.last=" + versions.getLast());
        System.out.println("java21.virtual-thread=true");
    }
}
