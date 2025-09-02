package sid.models;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import sid.exceptions.SidException;
import sid.stubs.StorageStub;

/**
 * Tests for TodoList behavior (mutations, indexing, and findTodos).
 */
class TodoListTest {

    @Test
    void add_marksAndUnmarks_triggerSaves() throws SidException {
        StorageStub storage = new StorageStub();
        List<ToDo> seed = new ArrayList<>();
        TodoList list = new TodoList(seed, storage);

        // add
        list.add(new ToDo("alpha", false));
        assertEquals(1, list.getSize());
        assertEquals(1, storage.getSaveCalls());
        assertTrue(storage.snapshots.get(0).contains("[T][ ] alpha"));

        // add second, then mark #2
        list.add(new ToDo("beta", false));
        assertEquals(2, list.getSize());
        assertEquals(2, storage.getSaveCalls());

        ToDo marked = list.markDone(2); // 1-based index
        assertTrue(marked.isDone());
        assertEquals(3, storage.getSaveCalls());
        assertTrue(storage.snapshots.get(2).contains("[T][X] beta"));

        // unmark #2
        ToDo unmarked = list.unmarkDone(2);
        assertFalse(unmarked.isDone());
        assertEquals(4, storage.getSaveCalls());
        assertTrue(storage.snapshots.get(3).contains("[T][ ] beta"));

        // delete #1
        list.delete(1);
        assertEquals(1, list.getSize());
        assertEquals(5, storage.getSaveCalls());
        assertTrue(storage.snapshots.get(4).contains("[T][ ] beta"));
    }

    @Test
    void getTodo_usesOneBasedIndex_andBoundsCheck() throws SidException {
        StorageStub storage = new StorageStub();
        TodoList list = new TodoList(new ArrayList<ToDo>(), storage);
        list.add(new ToDo("first", false));
        list.add(new ToDo("second", false));

        // one-based access
        assertEquals("[T][ ] first", list.getTodo(1).toString());
        assertEquals("[T][ ] second", list.getTodo(2).toString());

        // out of range throws
        assertThrows(SidException.class, () -> list.getTodo(0));
        assertThrows(SidException.class, () -> list.getTodo(3));
    }

    @Test
    void findTodos_substringCaseInsensitive_andLiteralSpecialChars() throws Exception {
        // No storage needed; this constructor does not save.
        TodoList list = new TodoList(List.of(
                new ToDo("Read book", false),
                new ToDo("read email", false),
                new ToDo("Call (Alice)", false)
        ));

        // substring, case-insensitive: "read" matches "Read book" and "read email"
        TodoList results1 = list.findTodos("read");
        assertNotNull(results1);
        assertEquals(2, results1.getSize());

        // literal special char "(" should match "Call (Alice)"
        TodoList results2 = list.findTodos("(");
        assertNotNull(results2);
        assertEquals(1, results2.getSize());
        assertEquals("[T][ ] Call (Alice)",
                results2.getTodo(1).toString()); // one-based indexing
    }

    @Test
    void findTodos_matchesAgainstToString_forDateBasedTasks() throws Exception {
        // Include a Deadline so the formatted date appears in toString()
        LocalDateTime dt = LocalDateTime.of(2025, 8, 30, 18, 0);
        TodoList list = new TodoList(List.of(
                new ToDo("alpha", false),
                new Deadline("return book", dt, false) // toString contains "Aug 30 2025 18:00"
        ));

        TodoList results = list.findTodos("Aug 30 2025 18:00");
        assertNotNull(results);
        assertEquals(1, results.getSize());
        assertTrue(results.getTodo(1).toString().contains("(by: Aug 30 2025 18:00)"));
    }

    @Test
    void findTodos_returnsEmptyList_onNullOrEmpty() {
        TodoList list = new TodoList(List.of(
                new ToDo("alpha", false),
                new ToDo("beta", false)
        ));

        TodoList r1 = list.findTodos(null);
        TodoList r2 = list.findTodos("   ");
        TodoList r3 = list.findTodos("zzz-not-present");

        assertNotNull(r1);
        assertNotNull(r2);
        assertNotNull(r3);

        assertEquals(0, r1.getSize());
        assertEquals(0, r2.getSize());
        assertEquals(0, r3.getSize());
    }

    @Test
    void toString_listsTasksOnePerLine_withOneBasedNumbers() {
        TodoList list = new TodoList(List.of(
                new ToDo("alpha", false),
                new ToDo("beta", true)
        ));
        String out = list.toString();
        assertTrue(out.contains("1. [T][ ] alpha"));
        assertTrue(out.contains("2. [T][X] beta"));
        // exactly one newline between lines, none at end
        assertTrue(out.split("\n").length >= 2);
        assertFalse(out.endsWith("\n"));
    }
}
