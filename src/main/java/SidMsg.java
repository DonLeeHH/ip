public enum SidMsg {
    HR("_".repeat(60)),
    GREETING("Hello! I'm Sid\nWhat can I do for you?"),
    GOODBYE("ByeByeBye");

    private final String text;
    SidMsg(String text) {
        this.text = text;
    }
    @Override
    public String toString() {
        return text;
    }
}
