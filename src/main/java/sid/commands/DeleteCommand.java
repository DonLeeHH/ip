package sid.commands;

import sid.exceptions.SidException;
import sid.models.ToDo;
import sid.models.TodoList;

/**
 * Command to delete a task.
 */
public class DeleteCommand implements Command {
    @Override
    public CommandResult execute(String arg, TodoList tasks) throws SidException {
        if (arg.isEmpty()) {
            throw new SidException("What do you want me to delete?\nUsage: delete <task-number>");
        }
        int id = IndexParser.parseIndex(arg, "Please provide a valid number after 'delete'.");
        ToDo toRemove = tasks.getTodo(id);
        tasks.delete(id);
        return new CommandResult(true, "Deleted your task:\n" + toRemove.toString(), toRemove, tasks.getSize());
    }
}
