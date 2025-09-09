package sid.commands;

import sid.exceptions.SidException;
import sid.models.ToDo;
import sid.models.TodoList;

/**
 * Command to unmark a task (mark as not done).
 */
public class UnmarkCommand implements Command {
    @Override
    public CommandResult execute(String arg, TodoList tasks) throws SidException {
        if (arg.isEmpty()) {
            throw new SidException("Usage: unmark <task-number>");
        }
        int id = IndexParser.parseIndex(arg, "Please provide a valid number after 'unmark'.");
        ToDo updated = tasks.unmarkDone(id);
        return new CommandResult(true, "Successfully unmarked task number " + id, updated, tasks.getSize());
    }
}
