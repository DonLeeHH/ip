package sid.commands;

import sid.exceptions.SidException;
import sid.models.TodoList;

/**
 * Interface for all commands that can be executed by the application.
 *
 * <p>Each command is responsible for validating its arguments and executing
 * the appropriate business logic on the given TodoList.
 */
public interface Command {
    /**
     * Executes the command with the given arguments.
     *
     * @param arg The argument string for the command (may be empty).
     * @param tasks The TodoList to operate on.
     * @return The result of command execution.
     * @throws SidException If the command arguments are invalid or execution fails.
     */
    CommandResult execute(String arg, TodoList tasks) throws SidException;
}
