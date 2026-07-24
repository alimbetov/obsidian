record CompactConstructorReturn(int value) {
    CompactConstructorReturn { if (value < 0) return; }
}
