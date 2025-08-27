package sid.enums;

/**
 * Defines predefined system messages used by Sid for formatted console output.
 *
 * <p>Each enum constant carries a display string that is printed to the console.
 * Typical uses include a horizontal rule, a greeting on startup, and a farewell on exit.
 */
public enum SidMsg {
    /** 60-character horizontal rule used to frame output. */
    HR("_".repeat(60)),

    /** Greeting shown at startup. */
    GREETING("Hello! I'm Sid\nWhat can I do for you?"),

    /** Farewell shown on exit. */
    GOODBYE("ByeByeBye");

    /** Display text associated with this message. */
    private final String text;

    SidMsg(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }
}
