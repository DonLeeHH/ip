package sid.models;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the Deadline task model
 */
class DeadlineTest {

    @Test
    void constructor_withDateTime_rendersWithTime() {
        LocalDateTime dt = LocalDateTime.of(2025, 8, 30, 18, 0);
        Deadline d = new Deadline("a deadline", dt, false);

        assertEquals(dt, d.getDueDate());
        assertTrue(d.toString().startsWith("[D]["), "Type tag should be [D]");
        assertEquals("[D][ ] a deadline (by: Aug 30 2025 18:00)", d.toString());
    }

    @Test
    void constructor_midnight_rendersDateOnly() {
        LocalDateTime midnight = LocalDateTime.of(2025, 12, 30, 0, 0);
        Deadline d = new Deadline("last deadline", midnight, false);

        assertEquals(midnight, d.getDueDate());
        assertEquals("[D][ ] last deadline (by: Dec 30 2025)", d.toString());
    }

    @Test
    void markAndUnmark_toggleDoneStateInOutput() {
        LocalDateTime midnight = LocalDateTime.of(2025, 12, 30, 0, 0);
        Deadline d = new Deadline("read book", midnight, false);

        // mark
        d.markTask();
        assertTrue(d.isDone());
        assertEquals("[D][X] read book (by: Dec 30 2025)", d.toString());

        // unmark
        d.unmarkTask();
        assertFalse(d.isDone());
        assertEquals("[D][ ] read book (by: Dec 30 2025)", d.toString());
    }
}
