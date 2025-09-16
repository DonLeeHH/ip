package sid.models;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import sid.exceptions.SidException;
import sid.messages.ResponseMessage;

/**
 * Tests for the Event task model (Level 8 date/time formatting).
 */
class EventTest {

    @Test
    void constructor_withDateTimes_rendersWithTimes() throws SidException {
        LocalDateTime start = LocalDateTime.now().plusDays(5).withHour(18).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime end = start.plusHours(2);

        Event e = new Event("an event", start, end, false);

        assertEquals(start, e.getStartDate());
        assertEquals(end, e.getEndDate());
        assertTrue(e.toString().startsWith("[E]["), "Type tag should be [E]");
        assertTrue(e.toString().contains("an event"));
        assertTrue(e.toString().contains("18:00"));
        assertTrue(e.toString().contains("20:00"));
    }

    @Test
    void constructor_midnightDates_rendersDateOnlyOnBothSides() throws SidException {
        LocalDateTime start = LocalDateTime.now().plusDays(10).withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime end = start.plusDays(1);

        Event e = new Event("another event", start, end, false);

        assertTrue(e.toString().contains("another event"));
        assertTrue(e.toString().startsWith("[E][ ]"));
        // Should render date only (no time) for midnight
    }

    @Test
    void constructor_mixedDateAndDateTime_formatsEachSideIndependently() throws SidException {
        LocalDateTime start = LocalDateTime.now().plusDays(15).withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime end = start.withHour(15).withMinute(45);

        Event e = new Event("mixed event", start, end, false);

        assertTrue(e.toString().contains("mixed event"));
        assertTrue(e.toString().contains("15:45"));
        assertTrue(e.toString().startsWith("[E][ ]"));
    }

    @Test
    void markAndUnmark_toggleDoneStateInOutput() throws SidException {
        LocalDateTime start = LocalDateTime.now().plusDays(20).withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime end = start.plusDays(1);

        Event e = new Event("trip", start, end, false);

        e.markTask();
        assertTrue(e.isDone());
        assertTrue(e.toString().contains("[E][X] trip"));

        e.unmarkTask();
        assertFalse(e.isDone());
        assertTrue(e.toString().contains("[E][ ] trip"));
    }

    @Test
    void constructor_pastStartDate_throwsException() {
        LocalDateTime pastStart = LocalDateTime.now().minusDays(1);
        LocalDateTime futureEnd = LocalDateTime.now().plusDays(1);

        SidException exception = assertThrows(SidException.class, () -> {
            new Event("Meeting", pastStart, futureEnd, false);
        });

        assertEquals(ResponseMessage.EVENT_PAST_DATE.getMessage(), exception.getMessage());
    }

    @Test
    void constructor_endBeforeStart_throwsException() {
        LocalDateTime futureStart = LocalDateTime.now().plusDays(2);
        LocalDateTime futureEnd = LocalDateTime.now().plusDays(1);

        SidException exception = assertThrows(SidException.class, () -> {
            new Event("Meeting", futureStart, futureEnd, false);
        });

        assertEquals(ResponseMessage.EVENT_INVALID_TIME_ORDER.getMessage(), exception.getMessage());
    }

    @Test
    void constructor_futureDate_success() throws SidException {
        LocalDateTime futureStart = LocalDateTime.now().plusDays(1);
        LocalDateTime futureEnd = LocalDateTime.now().plusDays(2);

        Event event = new Event("Meeting", futureStart, futureEnd, false);

        assertEquals(futureStart, event.getStartDate());
        assertEquals(futureEnd, event.getEndDate());
        assertEquals("Meeting", event.getDescription());
        assertFalse(event.isDone());
    }
}
