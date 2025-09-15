package sid.commands;

import sid.exceptions.SidException;
import sid.messages.ResponseMessage;
import sid.models.TodoList;

/**
 * Command to list all tasks.
 */
public class ListCommand implements Command {
    @Override
    public CommandResult execute(String arg, TodoList tasks) throws SidException {
        if (tasks.isEmpty()) {
            return new CommandResult(true, ResponseMessage.LIST_EMPTY.getMessage());
        }
        return new CommandResult(true, ResponseMessage.LIST_WITH_TASKS.getMessageWith(tasks));
    }
}
