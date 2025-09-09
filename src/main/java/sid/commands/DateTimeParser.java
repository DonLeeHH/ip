package sid.commands;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import sid.exceptions.SidException;

/**
 * Utility class for parsing date and time strings into LocalDateTime objects.
 */
public class DateTimeParser {

    /**
     * Tries several patterns; if only a date is present, time defaults to 00:00.
     *
     * @param text The date/time string to parse.
     * @return Parsed LocalDateTime.
     * @throws SidException If the text cannot be parsed.
     */
    public static LocalDateTime parseFlexibleDateTime(String text) throws SidException {
        // Try date + time patterns first
        LocalDateTime dateTime = tryParseDateTime(text,
                DateTimeFormatter.ofPattern("yyyy-MM-dd HHmm"),
                DateTimeFormatter.ofPattern("d/M/yyyy HHmm"),
                DateTimeFormatter.ISO_LOCAL_DATE_TIME // e.g., 2019-12-02T18:00
        );
        if (dateTime != null) {
            return dateTime;
        }

        // Try date only patterns -> midnight
        LocalDateTime dateOnly = tryParseDateOnly(text,
                DateTimeFormatter.ofPattern("yyyy-MM-dd"),
                DateTimeFormatter.ofPattern("d/M/yyyy")
        );
        if (dateOnly != null) {
            return dateOnly;
        }

        throw new SidException(
                "Could not parse date/time: " + text
                        + "\nTry: 2025-12-02 1800, 2025-12-02, 2/12/2025 1800, or 2/12/2025"
        );
    }

    /**
     * Tries to parse text as LocalDateTime using the given formatters.
     *
     * @param text the text to parse
     * @param formatters formatters to try in order
     * @return parsed LocalDateTime or null if none work
     */
    private static LocalDateTime tryParseDateTime(String text, DateTimeFormatter... formatters) {
        for (DateTimeFormatter formatter : formatters) {
            try {
                return LocalDateTime.parse(text, formatter);
            } catch (DateTimeParseException ignore) { /* try next */ }
        }
        return null;
    }

    /**
     * Tries to parse text as LocalDate and converts to midnight LocalDateTime.
     *
     * @param text the text to parse
     * @param formatters formatters to try in order
     * @return parsed LocalDateTime at midnight or null if none work
     */
    private static LocalDateTime tryParseDateOnly(String text, DateTimeFormatter... formatters) {
        for (DateTimeFormatter formatter : formatters) {
            try {
                LocalDate date = LocalDate.parse(text, formatter);
                return LocalDateTime.of(date, LocalTime.MIDNIGHT);
            } catch (DateTimeParseException ignore) { /* try next */ }
        }
        return null;
    }
}
