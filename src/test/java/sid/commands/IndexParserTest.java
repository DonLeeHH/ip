package sid.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import sid.exceptions.SidException;

/**
 * Test cases for IndexParser utility class.
 *
 * Tests created with assistance from Claude AI to ensure robust parsing
 * of task indices with proper error handling and validation.
 */
public class IndexParserTest {

    @Test
    public void parseIndex_validPositiveInteger_success() throws SidException {
        assertEquals(1, IndexParser.parseIndex("1", "Invalid index"));
        assertEquals(5, IndexParser.parseIndex("5", "Invalid index"));
        assertEquals(100, IndexParser.parseIndex("100", "Invalid index"));
    }

    @Test
    public void parseIndex_validIntegerWithWhitespace_success() throws SidException {
        assertEquals(1, IndexParser.parseIndex(" 1 ", "Invalid index"));
        assertEquals(42, IndexParser.parseIndex("  42  ", "Invalid index"));
        assertEquals(7, IndexParser.parseIndex("\t7\n", "Invalid index"));
    }

    @Test
    public void parseIndex_nonNumericString_throwsException() {
        SidException exception = assertThrows(SidException.class, () -> {
            IndexParser.parseIndex("abc", "Custom error message");
        });
        assertEquals("Custom error message", exception.getMessage());
    }

    @Test
    public void parseIndex_emptyString_throwsException() {
        SidException exception = assertThrows(SidException.class, () -> {
            IndexParser.parseIndex("", "Empty input error");
        });
        assertEquals("Empty input error", exception.getMessage());
    }

    @Test
    public void parseIndex_whitespaceOnlyString_throwsException() {
        SidException exception = assertThrows(SidException.class, () -> {
            IndexParser.parseIndex("   ", "Whitespace error");
        });
        assertEquals("Whitespace error", exception.getMessage());
    }

    @Test
    public void parseIndex_zeroValue_throwsAssertionError() {
        // The method has an assertion that requires positive values
        assertThrows(AssertionError.class, () -> {
            IndexParser.parseIndex("0", "Zero error");
        });
    }

    @Test
    public void parseIndex_negativeValue_throwsAssertionError() {
        // The method has an assertion that requires positive values
        assertThrows(AssertionError.class, () -> {
            IndexParser.parseIndex("-1", "Negative error");
        });
        assertThrows(AssertionError.class, () -> {
            IndexParser.parseIndex("-42", "Negative error");
        });
    }

    @Test
    public void parseIndex_decimalNumber_throwsException() {
        SidException exception = assertThrows(SidException.class, () -> {
            IndexParser.parseIndex("1.5", "Decimal error");
        });
        assertEquals("Decimal error", exception.getMessage());
    }

    @Test
    public void parseIndex_numberWithLeadingZeros_success() throws SidException {
        assertEquals(1, IndexParser.parseIndex("01", "Leading zero error"));
        assertEquals(123, IndexParser.parseIndex("000123", "Leading zero error"));
    }

    @Test
    public void parseIndex_largeNumber_success() throws SidException {
        assertEquals(999999, IndexParser.parseIndex("999999", "Large number error"));
    }

    @Test
    public void parseIndex_numberOutOfIntegerRange_throwsException() {
        SidException exception = assertThrows(SidException.class, () -> {
            IndexParser.parseIndex("99999999999999999999", "Out of range error");
        });
        assertEquals("Out of range error", exception.getMessage());
    }

    @Test
    public void parseIndex_mixedContent_throwsException() {
        SidException exception = assertThrows(SidException.class, () -> {
            IndexParser.parseIndex("1abc", "Mixed content error");
        });
        assertEquals("Mixed content error", exception.getMessage());
    }

    @Test
    public void parseIndex_customErrorMessage_preservesMessage() {
        String customMessage = "Please provide a valid task number.";
        SidException exception = assertThrows(SidException.class, () -> {
            IndexParser.parseIndex("invalid", customMessage);
        });
        assertEquals(customMessage, exception.getMessage());
    }
}
