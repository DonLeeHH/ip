package sid.commands;

import sid.exceptions.SidException;
import sid.models.ToDo;
import sid.models.TodoList;

/**
 * Command to mark a task as done.
 */
public class MarkCommand implements Command {
    @Override
    public CommandResult execute(String arg, TodoList tasks) throws SidException {
        if (arg.isEmpty()) {
            throw new SidException("Usage: mark <task-number>");
        }
        int id = IndexParser.parseIndex(arg, "Please provide a valid number after 'mark'.");
        ToDo updated = tasks.markDone(id);
        return new CommandResult(true, "Successfully marked task number " + id, updated, tasks.getSize());
    }
}
