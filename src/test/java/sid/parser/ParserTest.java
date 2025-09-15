package sid.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import sid.exceptions.SidException;
import sid.models.TodoList;
import sid.stubs.StorageStub;
import sid.stubs.UiStub;

/**
 * Test cases for Parser class functionality.
 *
 * Tests created with assistance from Claude AI to ensure comprehensive coverage
 * of command parsing, execution, and UI interaction scenarios.
 */
public class ParserTest {
    private Parser parser;
    private TodoList tasks;
    private UiStub ui;
    private StorageStub storage;

    @BeforeEach
    public void setUp() {
        parser = new Parser();
        storage = new StorageStub();
        tasks = storage.load();
        ui = new UiStub();
    }

    @Test
    public void parseAndExecute_byeCommand_returnsFalseAndShowsGoodbye() throws SidException {
        boolean result = parser.parseAndExecute("bye", tasks, ui);

        assertFalse(result);
        assertTrue(ui.isGoodbyeCalled());
    }

    @Test
    public void parseAndExecute_emptyInput_returnsTrue() throws SidException {
        boolean result = parser.parseAndExecute("", tasks, ui);
        assertTrue(result);
    }

    @Test
    public void parseAndExecute_whitespaceOnlyInput_returnsTrue() throws SidException {
        boolean result = parser.parseAndExecute("   ", tasks, ui);
        assertTrue(result);
    }

    @Test
    public void parseAndExecute_nullInput_throwsException() {
        SidException exception = assertThrows(SidException.class, () -> {
            parser.parseAndExecute(null, tasks, ui);
        });
        assertEquals("No input provided.", exception.getMessage());
    }

    @Test
    public void parseAndExecute_unknownCommand_throwsException() {
        SidException exception = assertThrows(SidException.class, () -> {
            parser.parseAndExecute("unknown", tasks, ui);
        });
        assertEquals("Unknown command. Try: todo | deadline | event | list | mark <n> | unmark <n> | delete <n> | bye",
                exception.getMessage());
    }

    @Test
    public void parseAndExecute_todoCommand_success() throws SidException {
        boolean result = parser.parseAndExecute("todo test task", tasks, ui);

        assertTrue(result);
        assertEquals(1, tasks.getSize());
        assertEquals(1, storage.getSaveCalls());
        assertEquals(1, ui.getCapturedMessages().size());
        assertTrue(ui.getCapturedMessages().get(0).startsWith("ADDED:"));
    }

    @Test
    public void parseAndExecute_listCommand_success() throws SidException {
        // Add a task first
        parser.parseAndExecute("todo test task", tasks, ui);
        ui.reset();

        boolean result = parser.parseAndExecute("list", tasks, ui);

        assertTrue(result);
        assertEquals(1, ui.getCapturedMessages().size());
        assertEquals("LIST: 1 tasks", ui.getCapturedMessages().get(0));
    }

    @Test
    public void parseAndExecute_markCommand_success() throws SidException {
        // Add a task first
        parser.parseAndExecute("todo test task", tasks, ui);
        ui.reset();

        boolean result = parser.parseAndExecute("mark 1", tasks, ui);

        assertTrue(result);
        assertTrue(tasks.getTodo(1).isDone());
        assertEquals(1, ui.getCapturedMessages().size());
        assertTrue(ui.getCapturedMessages().get(0).startsWith("MARKED:"));
    }

    @Test
    public void parseAndExecute_caseInsensitiveCommands_success() throws SidException {
        boolean result1 = parser.parseAndExecute("TODO test task", tasks, ui);
        boolean result2 = parser.parseAndExecute("List", tasks, ui);

        assertTrue(result1);
        assertTrue(result2);
        assertEquals(1, tasks.getSize());
    }

    // Tests for JavaFX version of parseAndExecute
    @Test
    public void parseAndExecuteJavaFX_validTodoCommand_returnsSuccessMessage() {
        String result = parser.parseAndExecute("todo test task", tasks);

        assertTrue(result.contains("Successfully added"));
        assertEquals(1, tasks.getSize());
    }

    @Test
    public void parseAndExecuteJavaFX_emptyInput_returnsHelpMessage() {
        String result = parser.parseAndExecute("", tasks);

        assertEquals("Try: todo | deadline | event | list | mark <n> | unmark <n> | delete <n>", result);
    }

    @Test
    public void parseAndExecuteJavaFX_whitespaceInput_returnsHelpMessage() {
        String result = parser.parseAndExecute("   ", tasks);

        assertEquals("Try: todo | deadline | event | list | mark <n> | unmark <n> | delete <n>", result);
    }

    @Test
    public void parseAndExecuteJavaFX_unknownCommand_returnsErrorMessage() {
        String result = parser.parseAndExecute("unknown", tasks);

        assertEquals("Unknown command. Try: todo | deadline | event | list | mark <n> | unmark <n> | delete <n> | bye",
                result);
    }

    @Test
    public void parseAndExecuteJavaFX_invalidCommandArguments_returnsErrorMessage() {
        String result = parser.parseAndExecute("todo", tasks);

        assertEquals("Usage: todo <description>", result);
    }

    @Test
    public void parseAndExecute_commandWithExtraSpaces_handledCorrectly() throws SidException {
        boolean result = parser.parseAndExecute("  todo   test task with spaces  ", tasks, ui);

        assertTrue(result);
        assertEquals(1, tasks.getSize());
        assertEquals("test task with spaces", tasks.getTodo(1).getDescription());
    }

    @Test
    public void parseAndExecute_findCommand_emptyResults() throws SidException {
        parser.parseAndExecute("todo sample task", tasks, ui);
        ui.reset();

        boolean result = parser.parseAndExecute("find nonexistent", tasks, ui);

        assertTrue(result);
        assertEquals(1, ui.getCapturedMessages().size());
        assertEquals("ERROR: No tasks found.", ui.getCapturedMessages().get(0));
    }

    @Test
    public void parseAndExecute_findCommand_withResults() throws SidException {
        parser.parseAndExecute("todo find this task", tasks, ui);
        ui.reset();

        boolean result = parser.parseAndExecute("find this", tasks, ui);

        assertTrue(result);
        assertEquals(1, ui.getCapturedMessages().size());
        assertEquals("FOUND: 1 matching tasks", ui.getCapturedMessages().get(0));
    }

    @Test
    public void parseAndExecute_deleteCommand_success() throws SidException {
        parser.parseAndExecute("todo task to delete", tasks, ui);
        ui.reset();

        boolean result = parser.parseAndExecute("delete 1", tasks, ui);

        assertTrue(result);
        assertEquals(0, tasks.getSize());
        assertEquals(1, ui.getCapturedMessages().size());
        assertTrue(ui.getCapturedMessages().get(0).startsWith("DELETED:"));
    }
}
