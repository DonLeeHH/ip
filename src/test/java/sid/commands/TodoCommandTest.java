package sid.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import sid.exceptions.SidException;
import sid.messages.ResponseMessage;
import sid.models.TodoList;
import sid.stubs.StorageStub;

/**
 * Test cases for TodoCommand class.
 *
 * Tests created with assistance from Claude AI to ensure comprehensive coverage
 * of todo task creation including edge cases and error handling.
 */
public class TodoCommandTest {
    private TodoCommand todoCommand;
    private TodoList tasks;
    private StorageStub storage;

    @BeforeEach
    public void setUp() {
        todoCommand = new TodoCommand();
        storage = new StorageStub();
        tasks = storage.load();
    }

    @Test
    public void execute_validDescription_createsTask() throws SidException {
        CommandResult result = todoCommand.execute("Buy groceries", tasks);

        assertTrue(result.shouldContinue());
        assertEquals(1, tasks.getSize());
        assertEquals("Buy groceries", tasks.getTodo(1).getDescription());
        assertFalse(tasks.getTodo(1).isDone());
        assertEquals("Buy groceries", result.getTask().getDescription());
        assertEquals(1, result.getTotalTasks());
        assertTrue(result.getMessage().contains(ResponseMessage.TODO_SUCCESS.getMessage()));
    }

    @Test
    public void execute_emptyDescription_throwsException() {
        SidException exception = assertThrows(SidException.class, () -> {
            todoCommand.execute("", tasks);
        });
        assertEquals(ResponseMessage.TODO_USAGE_ERROR.getMessage(), exception.getMessage());
        assertEquals(0, tasks.getSize());
    }

    @Test
    public void execute_whitespaceOnlyDescription_throwsException() {
        assertThrows(AssertionError.class, () -> {
            todoCommand.execute("   ", tasks);
        });
        assertEquals(0, tasks.getSize());
    }

    @Test
    public void execute_longDescription_success() throws SidException {
        String longDescription = "This is a very long task description that contains many words "
                + "and should still be handled correctly by the todo command without any issues";

        CommandResult result = todoCommand.execute(longDescription, tasks);

        assertTrue(result.shouldContinue());
        assertEquals(1, tasks.getSize());
        assertEquals(longDescription, tasks.getTodo(1).getDescription());
        assertEquals(longDescription, result.getTask().getDescription());
    }

    @Test
    public void execute_specialCharacters_success() throws SidException {
        String specialDescription = "Buy @#$% groceries & prepare for 100% test coverage!";

        CommandResult result = todoCommand.execute(specialDescription, tasks);

        assertTrue(result.shouldContinue());
        assertEquals(1, tasks.getSize());
        assertEquals(specialDescription, tasks.getTodo(1).getDescription());
    }

    @Test
    public void execute_multipleTasksSequentially_success() throws SidException {
        CommandResult result1 = todoCommand.execute("First task", tasks);
        CommandResult result2 = todoCommand.execute("Second task", tasks);
        CommandResult result3 = todoCommand.execute("Third task", tasks);

        assertEquals(3, tasks.getSize());
        assertEquals(1, result1.getTotalTasks());
        assertEquals(2, result2.getTotalTasks());
        assertEquals(3, result3.getTotalTasks());

        assertEquals("First task", tasks.getTodo(1).getDescription());
        assertEquals("Second task", tasks.getTodo(2).getDescription());
        assertEquals("Third task", tasks.getTodo(3).getDescription());
    }

    @Test
    public void execute_unicodeCharacters_success() throws SidException {
        String unicodeDescription = "学习中文 and eat 🍕 for dinner";

        CommandResult result = todoCommand.execute(unicodeDescription, tasks);

        assertTrue(result.shouldContinue());
        assertEquals(1, tasks.getSize());
        assertEquals(unicodeDescription, tasks.getTodo(1).getDescription());
    }

    @Test
    public void execute_numbersInDescription_success() throws SidException {
        String numericDescription = "Complete 42 exercises by 2025-12-31";

        CommandResult result = todoCommand.execute(numericDescription, tasks);

        assertTrue(result.shouldContinue());
        assertEquals(1, tasks.getSize());
        assertEquals(numericDescription, tasks.getTodo(1).getDescription());
    }

    @Test
    public void execute_descriptionWithNewlines_success() throws SidException {
        String descriptionWithNewlines = "Line 1\nLine 2\nLine 3";

        CommandResult result = todoCommand.execute(descriptionWithNewlines, tasks);

        assertTrue(result.shouldContinue());
        assertEquals(1, tasks.getSize());
        assertEquals(descriptionWithNewlines, tasks.getTodo(1).getDescription());
    }

    @Test
    public void execute_resultMessageFormat_correctFormat() throws SidException {
        CommandResult result = todoCommand.execute("Test task", tasks);

        assertTrue(result.getMessage().startsWith(ResponseMessage.TODO_SUCCESS.getMessage()));
        assertTrue(result.getMessage().contains("[T][ ] Test task"));
    }
}
