package sid.enums;

/**
 * Enumerates supported task types and their one-letter codes used in storage.
 *
 * <p>Codes:
 * <ul>
 *   <li>{@code T} – To-do</li>
 *   <li>{@code D} – Deadline</li>
 *   <li>{@code E} – Event</li>
 * </ul>
 */
public enum TaskType {
    TODO("T"),
    DEADLINE("D"),
    EVENT("E");

    /** One-letter code used in save files. */
    private final String code;

    TaskType(String code) {
        this.code = code;
    }

    /**
     * Parses a one-letter code into a {@link TaskType}.
     *
     * <p>Accepted values are {@code "T"}, {@code "D"}, and {@code "E"}.
     *
     * @param code One-letter task type code.
     * @return Matching {@link TaskType}.
     * @throws IllegalArgumentException If {@code code} is null or unrecognized.
     */
    public static TaskType fromCode(String code) {
        if (code == null) {
            throw new IllegalArgumentException("code is null");
        }
        switch (code) {
        case "T": return TODO;
        case "D": return DEADLINE;
        case "E": return EVENT;
        default: throw new IllegalArgumentException("Unknown task type code: " + code);
        }
    }

    @Override
    public String toString() {
        return code;
    }
}
