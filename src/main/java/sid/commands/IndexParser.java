package sid.commands;

import sid.exceptions.SidException;

/**
 * Utility class for parsing task indices from strings.
 */
public class IndexParser {

    /**
     * Parses a string to an integer index.
     *
     * @param s The string to parse.
     * @param errorMsg The error message to use if parsing fails.
     * @return The parsed integer.
     * @throws SidException If the string cannot be parsed or is not positive.
     */
    public static int parseIndex(String s, String errorMsg) throws SidException {
        assert s != null : "String to parse cannot be null";
        assert errorMsg != null : "Error message cannot be null";
        try {
            int result = Integer.parseInt(s.trim());
            assert result > 0 : "Parsed task ID must be positive (1-based indexing)";
            return result;
        } catch (NumberFormatException e) {
            throw new SidException(errorMsg);
        }
    }
}
