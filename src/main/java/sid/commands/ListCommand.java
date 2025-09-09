package sid.commands;

import sid.exceptions.SidException;
import sid.models.TodoList;

/**
 * Command to list all tasks.
 */
public class ListCommand implements Command {
    @Override
    public CommandResult execute(String arg, TodoList tasks) throws SidException {
        if (tasks.isEmpty()) {
            return new CommandResult(true, "You currently have no tasks!");
        }
        return new CommandResult(true, "Here are your tasks:\n" + tasks.toString());
    }
}
