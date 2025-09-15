package sid.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import sid.exceptions.SidException;
import sid.messages.ResponseMessage;
import sid.models.ToDo;
import sid.models.TodoList;
import sid.stubs.StorageStub;

/**
 * Test cases for DeleteCommand class.
 *
 * Tests created with assistance from Claude AI to ensure comprehensive coverage
 * of task deletion functionality with proper validation and error handling.
 */
public class DeleteCommandTest {
    private DeleteCommand deleteCommand;
    private TodoList tasks;
    private StorageStub storage;

    @BeforeEach
    public void setUp() throws SidException {
        deleteCommand = new DeleteCommand();
        storage = new StorageStub();
        tasks = storage.load();

        // Add some test tasks
        tasks.add(new ToDo("First task", false));
        tasks.add(new ToDo("Second task", true));
        tasks.add(new ToDo("Third task", false));
    }

    @Test
    public void execute_validTaskNumber_deletesTask() throws SidException {
        CommandResult result = deleteCommand.execute("2", tasks);

        assertTrue(result.shouldContinue());
        assertEquals(2, tasks.getSize());
        assertEquals("Second task", result.getTask().getDescription());
        assertTrue(result.getTask().isDone());
        assertEquals(2, result.getTotalTasks());
        assertTrue(result.getMessage().contains(ResponseMessage.DELETE_SUCCESS.getMessage()));
        assertTrue(result.getMessage().contains("Second task"));

        // Verify remaining tasks shifted correctly
        assertEquals("First task", tasks.getTodo(1).getDescription());
        assertEquals("Third task", tasks.getTodo(2).getDescription());
    }

    @Test
    public void execute_firstTask_deletesCorrectly() throws SidException {
        CommandResult result = deleteCommand.execute("1", tasks);

        assertEquals(2, tasks.getSize());
        assertEquals("First task", result.getTask().getDescription());
        assertEquals("Second task", tasks.getTodo(1).getDescription());
        assertEquals("Third task", tasks.getTodo(2).getDescription());
    }

    @Test
    public void execute_lastTask_deletesCorrectly() throws SidException {
        CommandResult result = deleteCommand.execute("3", tasks);

        assertEquals(2, tasks.getSize());
        assertEquals("Third task", result.getTask().getDescription());
        assertEquals("First task", tasks.getTodo(1).getDescription());
        assertEquals("Second task", tasks.getTodo(2).getDescription());
    }

    @Test
    public void execute_emptyArgument_throwsException() {
        SidException exception = assertThrows(SidException.class, () -> {
            deleteCommand.execute("", tasks);
        });
        assertEquals(ResponseMessage.DELETE_USAGE_ERROR.getMessage(), exception.getMessage());
        assertEquals(3, tasks.getSize()); // List unchanged
    }

    @Test
    public void execute_whitespaceOnlyArgument_throwsException() {
        SidException exception = assertThrows(SidException.class, () -> {
            deleteCommand.execute("   ", tasks);
        });
        assertEquals(ResponseMessage.DELETE_INVALID_NUMBER.getMessage(), exception.getMessage());
        assertEquals(3, tasks.getSize()); // List unchanged
    }

    @Test
    public void execute_invalidNumber_throwsException() {
        SidException exception = assertThrows(SidException.class, () -> {
            deleteCommand.execute("abc", tasks);
        });
        assertEquals(ResponseMessage.DELETE_INVALID_NUMBER.getMessage(), exception.getMessage());
        assertEquals(3, tasks.getSize()); // List unchanged
    }

    @Test
    public void execute_zeroTaskNumber_throwsException() {
        assertThrows(AssertionError.class, () -> {
            deleteCommand.execute("0", tasks);
        });
        assertEquals(3, tasks.getSize()); // List unchanged
    }

    @Test
    public void execute_negativeTaskNumber_throwsException() {
        assertThrows(AssertionError.class, () -> {
            deleteCommand.execute("-1", tasks);
        });
        assertEquals(3, tasks.getSize()); // List unchanged
    }

    @Test
    public void execute_taskNumberTooLarge_throwsException() {
        SidException exception = assertThrows(SidException.class, () -> {
            deleteCommand.execute("10", tasks);
        });
        assertEquals(ResponseMessage.INVALID_TASK_NUMBER.getMessage(), exception.getMessage());
        assertEquals(3, tasks.getSize()); // List unchanged
    }

    @Test
    public void execute_validNumberWithWhitespace_success() throws SidException {
        CommandResult result = deleteCommand.execute("  1  ", tasks);

        assertEquals(2, tasks.getSize());
        assertEquals("First task", result.getTask().getDescription());
    }

    @Test
    public void execute_deleteAllTasksSequentially_success() throws SidException {
        // Delete from the end to avoid index shifting issues
        deleteCommand.execute("3", tasks);
        assertEquals(2, tasks.getSize());

        deleteCommand.execute("2", tasks);
        assertEquals(1, tasks.getSize());

        CommandResult result = deleteCommand.execute("1", tasks);
        assertEquals(0, tasks.getSize());
        assertEquals(0, result.getTotalTasks());
    }

    @Test
    public void execute_deleteFromEmptyList_throwsException() {
        TodoList emptyTasks = storage.load(); // Fresh empty list

        SidException exception = assertThrows(SidException.class, () -> {
            deleteCommand.execute("1", emptyTasks);
        });
        assertEquals(ResponseMessage.INVALID_TASK_NUMBER.getMessage(), exception.getMessage());
    }

    @Test
    public void execute_deleteSingleItemList_success() throws SidException {
        TodoList singleTask = storage.load();
        singleTask.add(new ToDo("Only task", false));

        CommandResult result = deleteCommand.execute("1", singleTask);

        assertEquals(0, singleTask.getSize());
        assertEquals("Only task", result.getTask().getDescription());
        assertEquals(0, result.getTotalTasks());
    }

    @Test
    public void execute_decimalNumber_throwsException() {
        SidException exception = assertThrows(SidException.class, () -> {
            deleteCommand.execute("1.5", tasks);
        });
        assertEquals(ResponseMessage.DELETE_INVALID_NUMBER.getMessage(), exception.getMessage());
        assertEquals(3, tasks.getSize()); // List unchanged
    }
}
