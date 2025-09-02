package sid.models;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

/**
 * Tests for the Event task model (Level 8 date/time formatting).
 */
class EventTest {

    @Test
    void constructor_withDateTimes_rendersWithTimes() {
        LocalDateTime start = LocalDateTime.of(2025, 8, 30, 18, 0);
        LocalDateTime end = LocalDateTime.of(2025, 8, 30, 20, 0);

        Event e = new Event("an event", start, end, false);

        assertEquals(start, e.getStartDate());
        assertEquals(end, e.getEndDate());
        assertTrue(e.toString().startsWith("[E]["), "Type tag should be [E]");
        assertEquals("[E][ ] an event (from: Aug 30 2025 18:00, to: Aug 30 2025 20:00)", e.toString());
    }

    @Test
    void constructor_midnightDates_rendersDateOnlyOnBothSides() {
        LocalDateTime start = LocalDateTime.of(2025, 9, 1, 0, 0);
        LocalDateTime end = LocalDateTime.of(2025, 9, 2, 0, 0);

        Event e = new Event("another event", start, end, false);

        assertEquals("[E][ ] another event (from: Sep 01 2025, to: Sep 02 2025)", e.toString());
    }

    @Test
    void constructor_mixedDateAndDateTime_formatsEachSideIndependently() {
        LocalDateTime start = LocalDateTime.of(2025, 12, 30, 0, 0); // date only
        LocalDateTime end = LocalDateTime.of(2025, 12, 30, 15, 45); // with time

        Event e = new Event("mixed event", start, end, false);

        assertEquals("[E][ ] mixed event (from: Dec 30 2025, to: Dec 30 2025 15:45)", e.toString());
    }

    @Test
    void markAndUnmark_toggleDoneStateInOutput() {
        LocalDateTime start = LocalDateTime.of(2025, 9, 1, 0, 0);
        LocalDateTime end = LocalDateTime.of(2025, 9, 2, 0, 0);

        Event e = new Event("trip", start, end, false);

        e.markTask();
        assertTrue(e.isDone());
        assertEquals("[E][X] trip (from: Sep 01 2025, to: Sep 02 2025)", e.toString());

        e.unmarkTask();
        assertFalse(e.isDone());
        assertEquals("[E][ ] trip (from: Sep 01 2025, to: Sep 02 2025)", e.toString());
    }
}
