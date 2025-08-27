package sid.exceptions;

/**
 * Application-specific checked exception for the Sid chatbot.
 *
 * <p>Upon construction, the message is immediately printed using
 * This constructor therefore has a side effect (console output).
 */
public class SidException extends Exception {

    /**
     * Constructs a {@code SidException} with the specified detail message.
     *
     *
     * @param message Detail message describing the error.
     */
    public SidException(String message) {
        super(message);
    }
}
