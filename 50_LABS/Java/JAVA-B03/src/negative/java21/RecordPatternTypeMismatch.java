record Name(String value) {}
class RecordPatternTypeMismatch {
    static int read(Object value) {
        return switch (value) { case Name(Integer x) -> x; default -> 0; };
    }
}
