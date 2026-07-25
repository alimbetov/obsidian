package lab.b04;

import java.util.ArrayList;
import java.util.List;

public final class ExceptionProof {

    private ExceptionProof() {
    }

    public static void main(String[] args) {
        provesCatchAndFinallyOrder();
        provesCausePreservation();
        provesReverseCloseOrder();
        provesSuppressedException();
        provesInitializerFailureClosesEarlierResource();
        System.out.println("JAVA-B04 runtime proof PASS");
    }

    private static void provesCatchAndFinallyOrder() {
        StringBuilder trace = new StringBuilder();
        try {
            trace.append("A");
            throw new IllegalStateException("failure");
        } catch (RuntimeException error) {
            trace.append("B");
        } finally {
            trace.append("C");
        }
        trace.append("D");
        requireEquals("ABCD", trace.toString(), "catch/finally order");
    }

    private static void provesCausePreservation() {
        IllegalArgumentException source = new IllegalArgumentException("source");
        RuntimeException wrapper = new RuntimeException("wrapper", source);
        require(wrapper.getCause() == source, "cause identity must be preserved");
        requireEquals("source", wrapper.getCause().getMessage(), "cause message");
    }

    private static void provesReverseCloseOrder() {
        List<String> trace = new ArrayList<>();
        try (TrackedResource first = new TrackedResource("first", trace, false);
             TrackedResource second = new TrackedResource("second", trace, false)) {
            trace.add("body");
        }
        requireEquals(
                List.of("open:first", "open:second", "body", "close:second", "close:first"),
                trace,
                "resources must close in reverse order"
        );
    }

    private static void provesSuppressedException() {
        Exception captured = null;
        try (TrackedResource resource = new TrackedResource("broken", new ArrayList<>(), true)) {
            throw new IllegalArgumentException("body");
        } catch (Exception error) {
            captured = error;
        }

        require(captured instanceof IllegalArgumentException, "body exception must remain primary");
        requireEquals("body", captured.getMessage(), "primary message");
        requireEquals(1, captured.getSuppressed().length, "suppressed count");
        requireEquals("close:broken", captured.getSuppressed()[0].getMessage(), "suppressed message");
    }

    private static void provesInitializerFailureClosesEarlierResource() {
        List<String> trace = new ArrayList<>();
        Exception captured = null;

        try (TrackedResource first = new TrackedResource("first", trace, false);
             TrackedResource ignored = failToOpen()) {
            trace.add("body");
        } catch (Exception error) {
            captured = error;
        }

        require(captured instanceof IllegalStateException, "initializer failure must propagate");
        requireEquals("open failed", captured.getMessage(), "initializer message");
        requireEquals(List.of("open:first", "close:first"), trace, "earlier resource cleanup");
    }

    private static TrackedResource failToOpen() {
        throw new IllegalStateException("open failed");
    }

    private static void require(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }

    private static void requireEquals(Object expected, Object actual, String message) {
        if (!expected.equals(actual)) {
            throw new AssertionError(message + ": expected=" + expected + ", actual=" + actual);
        }
    }

    private static final class TrackedResource implements AutoCloseable {
        private final String name;
        private final List<String> trace;
        private final boolean failOnClose;

        private TrackedResource(String name, List<String> trace, boolean failOnClose) {
            this.name = name;
            this.trace = trace;
            this.failOnClose = failOnClose;
            trace.add("open:" + name);
        }

        @Override
        public void close() {
            trace.add("close:" + name);
            if (failOnClose) {
                throw new IllegalStateException("close:" + name);
            }
        }
    }
}
