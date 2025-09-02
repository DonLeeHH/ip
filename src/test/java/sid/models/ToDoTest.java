package sid.models;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;


class ToDoTest {

    @Test
    void constructor_withDoneTrue_rendersChecked() {
        ToDo t = new ToDo("Read book", true);
        assertEquals("[T][X] Read book", t.toString());
    }

    @Test
    void getters_reflectStateAndDescription() {
        ToDo t = new ToDo("Alpha", false);
        assertFalse(t.isDone(), "isDone should reflect initial state");
        assertEquals("Alpha", t.getDescription(), "description should be returned as-is");
    }

    @Test
    void mark_isIdempotent() {
        ToDo t = new ToDo("Task", false);
        t.markTask();
        t.markTask(); // call again
        assertTrue(t.isDone());
        assertEquals("[T][X] Task", t.toString());
    }

    @Test
    void unmark_isIdempotent() {
        ToDo t = new ToDo("Task", true);
        t.unmarkTask();
        t.unmarkTask(); // call again
        assertFalse(t.isDone());
        assertEquals("[T][ ] Task", t.toString());
    }

    @Test
    void toString_preservesWhitespaceInDescription() {
        ToDo t = new ToDo("  spaced  ", false);
        // Class does not trim; ensure output preserves spaces
        assertEquals("[T][ ]   spaced  ", t.toString());
    }

    @Test
    void toString_alwaysUsesTypeT() {
        ToDo t = new ToDo("X", false);
        assertTrue(t.toString().startsWith("[T]["), "Type tag should be [T]");
    }
}
