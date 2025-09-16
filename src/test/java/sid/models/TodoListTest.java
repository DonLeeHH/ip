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
        LocalDateTime dt = LocalDateTime.now().plusDays(30).withHour(18).withMinute(0).withSecond(0).withNano(0);
        TodoList list = new TodoList(List.of(
                new ToDo("alpha", false),
                new Deadline("return book", dt, false)
        ));

        // Search for the formatted date string that would actually be generated
        String expectedDateFormat = dt.format(java.time.format.DateTimeFormatter.ofPattern("MMM dd yyyy HH:mm"));
        TodoList results = list.findTodos(expectedDateFormat);
        assertNotNull(results);
        assertEquals(1, results.getSize());
        assertTrue(results.getTodo(1).toString().contains("(by: " + expectedDateFormat + ")"));
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

    // ---- Schedule Conflict Detection Tests ----

    @Test
    void add_allowsNonEventTasks_withoutConflictCheck() throws SidException {
        StorageStub storage = new StorageStub();
        TodoList list = new TodoList(new ArrayList<ToDo>(), storage);

        // Adding non-Event tasks should never trigger conflict detection
        list.add(new ToDo("task1", false));
        list.add(new Deadline("deadline1", LocalDateTime.now().plusDays(1), false));

        assertEquals(2, list.getSize());
        assertEquals(2, storage.getSaveCalls());
    }

    @Test
    void add_allowsNonOverlappingEvents() throws SidException {
        StorageStub storage = new StorageStub();
        TodoList list = new TodoList(new ArrayList<ToDo>(), storage);

        // Non-overlapping events should be added successfully
        Event event1 = new Event("Meeting A",
            LocalDateTime.now().plusDays(10).withHour(9).withMinute(0).withSecond(0).withNano(0),
            LocalDateTime.now().plusDays(10).withHour(10).withMinute(0).withSecond(0).withNano(0), false);
        Event event2 = new Event("Meeting B",
            LocalDateTime.now().plusDays(10).withHour(11).withMinute(0).withSecond(0).withNano(0),
            LocalDateTime.now().plusDays(10).withHour(12).withMinute(0).withSecond(0).withNano(0), false);

        list.add(event1);
        list.add(event2);

        assertEquals(2, list.getSize());
        assertEquals(2, storage.getSaveCalls());
    }

    @Test
    void add_throwsException_whenEventsOverlapCompletely() throws SidException {
        StorageStub storage = new StorageStub();
        TodoList list = new TodoList(new ArrayList<ToDo>(), storage);

        // Add first event
        Event event1 = new Event("Meeting A",
            LocalDateTime.now().plusDays(10).withHour(9).withMinute(0).withSecond(0).withNano(0),
            LocalDateTime.now().plusDays(10).withHour(11).withMinute(0).withSecond(0).withNano(0), false);
        list.add(event1);

        // Try to add completely overlapping event
        Event event2 = new Event("Meeting B",
            LocalDateTime.now().plusDays(10).withHour(9).withMinute(30).withSecond(0).withNano(0),
            LocalDateTime.now().plusDays(10).withHour(10).withMinute(30).withSecond(0).withNano(0), false);

        SidException exception = assertThrows(SidException.class, () -> list.add(event2));
        assertTrue(exception.getMessage().contains("Scheduling conflict detected!"));
        assertTrue(exception.getMessage().contains("Meeting A"));

        // First event should still be in list, second should not be added
        assertEquals(1, list.getSize());
    }

    @Test
    void add_throwsException_whenEventsOverlapPartially() throws SidException {
        StorageStub storage = new StorageStub();
        TodoList list = new TodoList(new ArrayList<ToDo>(), storage);

        // Add first event: 9:00-11:00
        Event event1 = new Event("Morning Meeting",
            LocalDateTime.now().plusDays(10).withHour(9).withMinute(0).withSecond(0).withNano(0),
            LocalDateTime.now().plusDays(10).withHour(11).withMinute(0).withSecond(0).withNano(0), false);
        list.add(event1);

        // Try to add partially overlapping event: 10:30-12:30
        Event event2 = new Event("Lunch Meeting",
            LocalDateTime.now().plusDays(10).withHour(10).withMinute(30).withSecond(0).withNano(0),
            LocalDateTime.now().plusDays(10).withHour(12).withMinute(30).withSecond(0).withNano(0), false);

        SidException exception = assertThrows(SidException.class, () -> list.add(event2));
        assertTrue(exception.getMessage().contains("Scheduling conflict detected!"));
        assertTrue(exception.getMessage().contains("Morning Meeting"));
    }

    @Test
    void add_throwsException_whenEventStartsExactlyWhenAnotherEnds() throws SidException {
        StorageStub storage = new StorageStub();
        TodoList list = new TodoList(new ArrayList<ToDo>(), storage);

        // Add first event: 9:00-10:00
        Event event1 = new Event("Meeting A",
            LocalDateTime.now().plusDays(10).withHour(9).withMinute(0).withSecond(0).withNano(0),
            LocalDateTime.now().plusDays(10).withHour(10).withMinute(0).withSecond(0).withNano(0), false);
        list.add(event1);

        // Try to add event that starts exactly when first ends: 10:00-11:00
        // This should NOT conflict (end time is exclusive)
        Event event2 = new Event("Meeting B",
            LocalDateTime.now().plusDays(10).withHour(10).withMinute(0).withSecond(0).withNano(0),
            LocalDateTime.now().plusDays(10).withHour(11).withMinute(0).withSecond(0).withNano(0), false);

        // This should succeed - events that touch at endpoints don't conflict
        list.add(event2);
        assertEquals(2, list.getSize());
    }

    @Test
    void add_throwsException_withMultipleConflictingEvents() throws SidException {
        StorageStub storage = new StorageStub();
        TodoList list = new TodoList(new ArrayList<ToDo>(), storage);

        // Add multiple existing events
        Event event1 = new Event("Meeting A",
            LocalDateTime.now().plusDays(10).withHour(9).withMinute(0).withSecond(0).withNano(0),
            LocalDateTime.now().plusDays(10).withHour(10).withMinute(30).withSecond(0).withNano(0), false);
        Event event2 = new Event("Meeting B",
            LocalDateTime.now().plusDays(10).withHour(11).withMinute(0).withSecond(0).withNano(0),
            LocalDateTime.now().plusDays(10).withHour(12).withMinute(0).withSecond(0).withNano(0), false);
        Event event3 = new Event("Meeting C",
            LocalDateTime.now().plusDays(10).withHour(14).withMinute(0).withSecond(0).withNano(0),
            LocalDateTime.now().plusDays(10).withHour(15).withMinute(0).withSecond(0).withNano(0), false);

        list.add(event1);
        list.add(event2);
        list.add(event3);

        // Try to add event that conflicts with multiple events: 10:00-14:30
        Event conflictingEvent = new Event("Long Meeting",
            LocalDateTime.now().plusDays(10).withHour(10).withMinute(0).withSecond(0).withNano(0),
            LocalDateTime.now().plusDays(10).withHour(14).withMinute(30).withSecond(0).withNano(0), false);

        SidException exception = assertThrows(SidException.class, () -> list.add(conflictingEvent));
        assertTrue(exception.getMessage().contains("Scheduling conflict detected!"));
        // Should mention both conflicting events
        assertTrue(exception.getMessage().contains("Meeting A"));
        assertTrue(exception.getMessage().contains("Meeting B"));
        assertTrue(exception.getMessage().contains("Meeting C"));
    }

    @Test
    void add_allowsEventsOnDifferentDays() throws SidException {
        StorageStub storage = new StorageStub();
        TodoList list = new TodoList(new ArrayList<ToDo>(), storage);

        // Events with same times but different days should not conflict
        Event event1 = new Event("Monday Meeting",
            LocalDateTime.now().plusDays(15).withHour(9).withMinute(0).withSecond(0).withNano(0),
            LocalDateTime.now().plusDays(15).withHour(10).withMinute(0).withSecond(0).withNano(0), false);
        Event event2 = new Event("Tuesday Meeting",
            LocalDateTime.now().plusDays(16).withHour(9).withMinute(0).withSecond(0).withNano(0),
            LocalDateTime.now().plusDays(16).withHour(10).withMinute(0).withSecond(0).withNano(0), false);

        list.add(event1);
        list.add(event2);

        assertEquals(2, list.getSize());
    }

    @Test
    void add_conflictDetection_ignoresNonEventTasks() throws SidException {
        StorageStub storage = new StorageStub();
        TodoList list = new TodoList(new ArrayList<ToDo>(), storage);

        // Add mixed task types
        list.add(new ToDo("Regular task", false));
        list.add(new Deadline("Deadline task",
                LocalDateTime.now().plusDays(10).withHour(10).withMinute(0).withSecond(0).withNano(0), false));

        // Add event that would overlap with deadline time - should not conflict
        Event event = new Event("Meeting",
            LocalDateTime.now().plusDays(10).withHour(9).withMinute(30).withSecond(0).withNano(0),
            LocalDateTime.now().plusDays(10).withHour(10).withMinute(30).withSecond(0).withNano(0), false);

        list.add(event); // Should succeed
        assertEquals(3, list.getSize());
    }
}
