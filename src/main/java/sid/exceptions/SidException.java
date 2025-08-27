package sid.exceptions;

import sid.Sid;

/**
 * Application-specific checked exception for the Sid chatbot.
 *
 * <p>Upon construction, the message is immediately printed using
 * {@link Sid#SpecialPrint(String)} to provide user-visible feedback.
 * This constructor therefore has a side effect (console output).
 */
public class SidException extends Exception {

    /**
     * Constructs a {@code SidException} with the specified detail message.
     *
     * <p>The message is also printed via {@link Sid#SpecialPrint(String)}.
     *
     * @param message Detail message describing the error.
     */
    public SidException(String message) {
        super(message);
    }
}
