final class ContinueToBlock {
    void run() {
        block: {
            continue block;
        }
    }
}
