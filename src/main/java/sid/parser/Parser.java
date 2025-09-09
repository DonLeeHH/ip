package sid.parser;

import java.util.Map;

import sid.commands.ByeCommand;
import sid.commands.Command;
import sid.commands.CommandResult;
import sid.commands.DeadlineCommand;
import sid.commands.DeleteCommand;
import sid.commands.EventCommand;
import sid.commands.FindCommand;
import sid.commands.ListCommand;
import sid.commands.MarkCommand;
import sid.commands.TodoCommand;
import sid.commands.UnmarkCommand;
import sid.exceptions.SidException;
import sid.models.TodoList;
import sid.ui.Ui;

/**
 * Parses user commands and executes them against the task list.
 *
 * <p>Level 8: Dates/times are parsed into {@link LocalDateTime}. Accepted inpu
 * formats include {@code yyyy-MM-dd[ HHmm]} and {@code d/M/yyyy[ HHmm]}; ISO
 * {@code yyyy-MM-dd'T'HH:mm} is also supported. Date-only inputs default to
 * {@code 00:00}.
 */
public class Parser {
    /** Split limit for command parsing: command + arguments. */
    private static final int COMMAND_SPLIT_LIMIT = 2;
    private final Map<String, Command> commands = Map.of(
        "list", new ListCommand(),
        "todo", new TodoCommand(),
        "deadline", new DeadlineCommand(),
        "event", new EventCommand(),
        "mark", new MarkCommand(),
        "unmark", new UnmarkCommand(),
        "delete", new DeleteCommand(),
        "find", new FindCommand(),
        "bye", new ByeCommand()
    );

    /**
     * Parses and executes a single command line.
     *
     * <p>Returns {@code false} when the session should end (on {@code bye});
     * returns {@code true} otherwise.
     *
     * @param input Raw user input.
     * @param tasks Task list to operate on.
     * @param ui    UI for user-visible output.
     * @return {@code false} to terminate; {@code true} to continue.
     * @throws SidException If the command/arguments are invalid.
     */
    public boolean parseAndExecute(String input, TodoList tasks, Ui ui) throws SidException {
        if (input == null) {
            throw new SidException("No input provided.");
        }

        String line = input.trim();
        if (line.isEmpty()) {
            return true; // ignore empty lines
        }

        String[] parts = line.split("\\s+", COMMAND_SPLIT_LIMIT);
        String cmd = parts[0].toLowerCase();
        String arg = (parts.length > 1) ? parts[1].trim() : "";

        // Handle special case for bye command that needs UI interaction
        if (cmd.equals("bye")) {
            ui.showGoodbye();
            return false;
        }

        // Execute the command and display results
        CommandResult result = executeCommand(cmd, arg, tasks);
        displayCommandResult(cmd, result, tasks, ui);

        return result.shouldContinue();
    }


    /**
     * Parses and executes the command input from JavaFX GUI.
     *
     * <p>Returns response string
     *
     * @param input Raw user input.
     * @param tasks Task list to operate on.
     */
    public String parseAndExecute(String input, TodoList tasks) {
        String line = input.trim();
        if (line.isEmpty()) {
            return "Try: todo | deadline | event | list | mark <n> | unmark <n> | delete <n>";
        }

        String[] parts = line.split("\\s+", COMMAND_SPLIT_LIMIT);
        String cmd = parts[0].toLowerCase();
        String arg = (parts.length > 1) ? parts[1].trim() : "";

        try {
            CommandResult result = executeCommand(cmd, arg, tasks);
            return result.getMessage();
        } catch (SidException error) {
            return error.getMessage();
        }
    }

    // ---- Command execution methods ----

    /**
     * Executes a command and returns the result.
     *
     * @param cmd The command to execute.
     * @param arg The argument for the command.
     * @param tasks The task list to operate on.
     * @return The result of command execution.
     * @throws SidException If the command is invalid or execution fails.
     */
    private CommandResult executeCommand(String cmd, String arg, TodoList tasks) throws SidException {
        assert cmd != null : "Command cannot be null";
        assert arg != null : "Argument cannot be null";
        assert tasks != null : "TodoList cannot be null";

        Command command = commands.get(cmd);
        if (command == null) {
            throw new SidException(
                    "Unknown command. Try: todo | deadline | event | list | mark <n> | unmark <n> | delete <n> | bye"
            );
        }

        return command.execute(arg, tasks);
    }

    /**
     * Displays the command result through the UI based on command type.
     *
     * @param cmd The command that was executed.
     * @param result The result of command execution.
     * @param tasks The task list (needed for displaying list).
     * @param ui The UI for output.
     */
    private void displayCommandResult(String cmd, CommandResult result, TodoList tasks, Ui ui) {
        assert cmd != null : "Command cannot be null";
        assert result != null : "CommandResult cannot be null";
        assert tasks != null : "TodoList cannot be null";
        assert ui != null : "UI cannot be null";

        switch (cmd) {
        case "list":
            ui.showList(tasks);
            break;

        case "todo":
        case "deadline":
        case "event":
            ui.showTaskAdded(result.getTask(), result.getTotalTasks());
            break;

        case "mark":
            ui.showTaskMarked(result.getTask());
            break;

        case "unmark":
            ui.showTaskUnmarked(result.getTask());
            break;

        case "delete":
            ui.showTaskDeleted(result.getTask(), result.getTotalTasks());
            break;

        case "find":
            if (result.getFoundTasks().isEmpty()) {
                ui.showError("No tasks found.");
            } else {
                ui.showFind(result.getFoundTasks());
            }
            break;

        default:
            // Should never reach here as executeCommand handles unknown commands
            break;
        }
    }
}
