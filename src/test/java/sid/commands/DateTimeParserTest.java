package sid.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import sid.exceptions.SidException;

/**
 * Test cases for DateTimeParser utility class.
 *
 * Tests created with assistance from Claude AI to ensure comprehensive coverage
 * of date/time parsing functionality including edge cases and error handling.
 */
public class DateTimeParserTest {

    @Test
    public void parseFlexibleDateTime_validDateTime_success() throws SidException {
        // Test yyyy-MM-dd HHmm format
        LocalDateTime result1 = DateTimeParser.parseFlexibleDateTime("2025-12-02 1800");
        assertEquals(LocalDateTime.of(2025, 12, 2, 18, 0), result1);

        // Test d/M/yyyy HHmm format
        LocalDateTime result2 = DateTimeParser.parseFlexibleDateTime("2/12/2025 1800");
        assertEquals(LocalDateTime.of(2025, 12, 2, 18, 0), result2);

        // Test ISO format
        LocalDateTime result3 = DateTimeParser.parseFlexibleDateTime("2025-12-02T18:00");
        assertEquals(LocalDateTime.of(2025, 12, 2, 18, 0), result3);
    }

    @Test
    public void parseFlexibleDateTime_validDateOnly_defaultsToMidnight() throws SidException {
        // Test yyyy-MM-dd format
        LocalDateTime result1 = DateTimeParser.parseFlexibleDateTime("2025-12-02");
        assertEquals(LocalDateTime.of(2025, 12, 2, 0, 0), result1);

        // Test d/M/yyyy format
        LocalDateTime result2 = DateTimeParser.parseFlexibleDateTime("2/12/2025");
        assertEquals(LocalDateTime.of(2025, 12, 2, 0, 0), result2);
    }

    @Test
    public void parseFlexibleDateTime_edgeCases_success() throws SidException {
        // Test single digit day/month
        LocalDateTime result1 = DateTimeParser.parseFlexibleDateTime("1/1/2025");
        assertEquals(LocalDateTime.of(2025, 1, 1, 0, 0), result1);

        // Test with time
        LocalDateTime result2 = DateTimeParser.parseFlexibleDateTime("1/1/2025 0000");
        assertEquals(LocalDateTime.of(2025, 1, 1, 0, 0), result2);

        // Test end of year
        LocalDateTime result3 = DateTimeParser.parseFlexibleDateTime("31/12/2025 2359");
        assertEquals(LocalDateTime.of(2025, 12, 31, 23, 59), result3);
    }

    @Test
    public void parseFlexibleDateTime_invalidFormat_throwsException() {
        // Test completely invalid format
        SidException exception1 = assertThrows(SidException.class, () -> {
            DateTimeParser.parseFlexibleDateTime("not-a-date");
        });
        assertEquals("Could not parse date/time: not-a-date\n"
                + "Try: 2025-12-02 1800, 2025-12-02, 2/12/2025 1800, or 2/12/2025",
                exception1.getMessage());

        // Test invalid date
        SidException exception2 = assertThrows(SidException.class, () -> {
            DateTimeParser.parseFlexibleDateTime("32/12/2025");
        });
        assertEquals("Could not parse date/time: 32/12/2025\n"
                + "Try: 2025-12-02 1800, 2025-12-02, 2/12/2025 1800, or 2/12/2025",
                exception2.getMessage());

        // Test invalid time
        SidException exception3 = assertThrows(SidException.class, () -> {
            DateTimeParser.parseFlexibleDateTime("2025-12-02 2500");
        });
        assertEquals("Could not parse date/time: 2025-12-02 2500\n"
                + "Try: 2025-12-02 1800, 2025-12-02, 2/12/2025 1800, or 2/12/2025",
                exception3.getMessage());
    }

    @Test
    public void parseFlexibleDateTime_emptyInput_throwsException() {
        SidException exception = assertThrows(SidException.class, () -> {
            DateTimeParser.parseFlexibleDateTime("");
        });
        assertEquals("Could not parse date/time: \n"
                + "Try: 2025-12-02 1800, 2025-12-02, 2/12/2025 1800, or 2/12/2025",
                exception.getMessage());
    }

    @Test
    public void parseFlexibleDateTime_leapYear_success() throws SidException {
        // Test leap year date
        LocalDateTime result = DateTimeParser.parseFlexibleDateTime("29/2/2024");
        assertEquals(LocalDateTime.of(2024, 2, 29, 0, 0), result);
    }

    @Test
    public void parseFlexibleDateTime_invalidDateFormat_throwsException() {
        // Test completely invalid date format
        SidException exception = assertThrows(SidException.class, () -> {
            DateTimeParser.parseFlexibleDateTime("32/13/2023");
        });
        assertEquals("Could not parse date/time: 32/13/2023\n"
                + "Try: 2025-12-02 1800, 2025-12-02, 2/12/2025 1800, or 2/12/2025",
                exception.getMessage());
    }
}
