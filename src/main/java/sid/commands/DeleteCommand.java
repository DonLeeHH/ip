package sid.commands;

import sid.exceptions.SidException;
import sid.messages.ResponseMessage;
import sid.models.ToDo;
import sid.models.TodoList;

/**
 * Command to delete a task.
 */
public class DeleteCommand implements Command {
    @Override
    public CommandResult execute(String arg, TodoList tasks) throws SidException {
        if (arg.isEmpty()) {
            throw new SidException(ResponseMessage.DELETE_USAGE_ERROR.getMessage());
        }
        int id = IndexParser.parseIndex(arg, ResponseMessage.DELETE_INVALID_NUMBER.getMessage());
        ToDo toRemove = tasks.getTodo(id);
        tasks.delete(id);
        return new CommandResult(true, ResponseMessage.DELETE_SUCCESS.getMessageWith(toRemove), toRemove,
                tasks.getSize());
    }
}
