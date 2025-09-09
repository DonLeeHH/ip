package sid.commands;

import sid.exceptions.SidException;
import sid.models.TodoList;

/**
 * Command to find tasks matching a keyword.
 */
public class FindCommand implements Command {
    @Override
    public CommandResult execute(String arg, TodoList tasks) throws SidException {
        if (arg.isEmpty()) {
            throw new SidException("Usage: find <keyword>");
        }
        TodoList foundTodos = tasks.findTodos(arg);
        if (foundTodos.isEmpty()) {
            return new CommandResult(true, "No tasks found", foundTodos);
        } else {
            return new CommandResult(true, "Here are the tasks I found:\n" + foundTodos.toString(), foundTodos);
        }
    }
}
