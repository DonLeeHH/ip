package sid.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import sid.exceptions.SidException;
import sid.models.ToDo;
import sid.models.TodoList;
import sid.stubs.StorageStub;

/**
 * Test cases for MarkCommand class.
 *
 * Tests created with assistance from Claude AI to ensure robust handling
 * of task marking functionality with proper validation and error handling.
 */
public class MarkCommandTest {
    private MarkCommand markCommand;
    private TodoList tasks;
    private StorageStub storage;

    @BeforeEach
    public void setUp() throws SidException {
        markCommand = new MarkCommand();
        storage = new StorageStub();
        tasks = storage.load();

        // Add some test tasks
        tasks.add(new ToDo("First task", false));
        tasks.add(new ToDo("Second task", false));
        tasks.add(new ToDo("Third task", true)); // Already marked
    }

    @Test
    public void execute_validTaskNumber_marksTaskAsDone() throws SidException {
        CommandResult result = markCommand.execute("1", tasks);

        assertTrue(result.shouldContinue());
        assertTrue(tasks.getTodo(1).isDone());
        assertEquals("First task", result.getTask().getDescription());
        assertTrue(result.getTask().isDone());
        assertEquals(3, result.getTotalTasks());
        assertTrue(result.getMessage().contains("Successfully marked task number 1"));
    }

    @Test
    public void execute_alreadyMarkedTask_marksAgain() throws SidException {
        CommandResult result = markCommand.execute("3", tasks);

        assertTrue(result.shouldContinue());
        assertTrue(tasks.getTodo(3).isDone());
        assertEquals("Third task", result.getTask().getDescription());
        assertTrue(result.getTask().isDone());
    }

    @Test
    public void execute_emptyArgument_throwsException() {
        SidException exception = assertThrows(SidException.class, () -> {
            markCommand.execute("", tasks);
        });
        assertEquals("Usage: mark <task-number>", exception.getMessage());
    }

    @Test
    public void execute_whitespaceOnlyArgument_throwsException() {
        SidException exception = assertThrows(SidException.class, () -> {
            markCommand.execute("   ", tasks);
        });
        assertEquals("Please provide a valid number after 'mark'.", exception.getMessage());
    }

    @Test
    public void execute_invalidNumber_throwsException() {
        SidException exception = assertThrows(SidException.class, () -> {
            markCommand.execute("abc", tasks);
        });
        assertEquals("Please provide a valid number after 'mark'.", exception.getMessage());
    }

    @Test
    public void execute_zeroTaskNumber_throwsException() {
        assertThrows(AssertionError.class, () -> {
            markCommand.execute("0", tasks);
        });
    }

    @Test
    public void execute_negativeTaskNumber_throwsException() {
        assertThrows(AssertionError.class, () -> {
            markCommand.execute("-1", tasks);
        });
    }

    @Test
    public void execute_taskNumberTooLarge_throwsException() {
        SidException exception = assertThrows(SidException.class, () -> {
            markCommand.execute("10", tasks);
        });
        assertEquals("Not a valid task number!", exception.getMessage());
    }

    @Test
    public void execute_validNumberWithWhitespace_success() throws SidException {
        CommandResult result = markCommand.execute("  2  ", tasks);

        assertTrue(result.shouldContinue());
        assertTrue(tasks.getTodo(2).isDone());
        assertEquals("Second task", result.getTask().getDescription());
    }

    @Test
    public void execute_decimalNumber_throwsException() {
        SidException exception = assertThrows(SidException.class, () -> {
            markCommand.execute("1.5", tasks);
        });
        assertEquals("Please provide a valid number after 'mark'.", exception.getMessage());
    }

    @Test
    public void execute_largeValidNumber_throwsException() {
        SidException exception = assertThrows(SidException.class, () -> {
            markCommand.execute("999", tasks);
        });
        assertEquals("Not a valid task number!", exception.getMessage());
    }

    @Test
    public void execute_validTaskOnEmptyList_throwsException() {
        TodoList emptyTasks = storage.load(); // Fresh empty list

        SidException exception = assertThrows(SidException.class, () -> {
            markCommand.execute("1", emptyTasks);
        });
        assertEquals("Not a valid task number!", exception.getMessage());
    }

    @Test
    public void execute_multipleMarksOnSameTask_success() throws SidException {
        CommandResult result1 = markCommand.execute("1", tasks);
        CommandResult result2 = markCommand.execute("1", tasks);

        assertTrue(tasks.getTodo(1).isDone());
        assertTrue(result1.getTask().isDone());
        assertTrue(result2.getTask().isDone());
        assertEquals("First task", result1.getTask().getDescription());
        assertEquals("First task", result2.getTask().getDescription());
    }
}
