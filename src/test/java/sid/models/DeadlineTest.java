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
 * Tests for the Deadline task model
 */
class DeadlineTest {

    @Test
    void constructor_withDateTime_rendersWithTime() throws SidException {
        LocalDateTime dt = LocalDateTime.now().plusDays(5).withHour(18).withMinute(0).withSecond(0).withNano(0);
        Deadline d = new Deadline("a deadline", dt, false);

        assertEquals(dt, d.getDueDate());
        assertTrue(d.toString().startsWith("[D]["), "Type tag should be [D]");
        assertTrue(d.toString().contains("a deadline"));
        assertTrue(d.toString().contains("18:00"));
    }

    @Test
    void constructor_midnight_rendersDateOnly() throws SidException {
        LocalDateTime midnight = LocalDateTime.now().plusDays(10).withHour(0).withMinute(0).withSecond(0).withNano(0);
        Deadline d = new Deadline("last deadline", midnight, false);

        assertEquals(midnight, d.getDueDate());
        assertTrue(d.toString().contains("last deadline"));
        assertTrue(d.toString().startsWith("[D][ ]"));
    }

    @Test
    void markAndUnmark_toggleDoneStateInOutput() throws SidException {
        LocalDateTime midnight = LocalDateTime.now().plusDays(15).withHour(0).withMinute(0).withSecond(0).withNano(0);
        Deadline d = new Deadline("read book", midnight, false);

        // mark
        d.markTask();
        assertTrue(d.isDone());
        assertTrue(d.toString().contains("[D][X] read book"));

        // unmark
        d.unmarkTask();
        assertFalse(d.isDone());
        assertTrue(d.toString().contains("[D][ ] read book"));
    }

    @Test
    void constructor_pastDate_throwsException() {
        LocalDateTime pastDate = LocalDateTime.now().minusDays(1);

        SidException exception = assertThrows(SidException.class, () -> {
            new Deadline("Submit assignment", pastDate, false);
        });

        assertEquals(ResponseMessage.DEADLINE_PAST_DATE.getMessage(), exception.getMessage());
    }

    @Test
    void constructor_futureDate_success() throws SidException {
        LocalDateTime futureDate = LocalDateTime.now().plusDays(1);

        Deadline deadline = new Deadline("Submit assignment", futureDate, false);

        assertEquals(futureDate, deadline.getDueDate());
        assertEquals("Submit assignment", deadline.getDescription());
        assertFalse(deadline.isDone());
    }
}
