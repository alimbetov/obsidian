record Name(String value) {}
class DominatedRecordPattern {
    static int read(Name value) {
        return switch (value) {
            case Name(var x) -> 1;
            case Name(String x) -> 2;
        };
    }
}
