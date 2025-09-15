package sid.commands;

import sid.exceptions.SidException;
import sid.messages.ResponseMessage;
import sid.models.ToDo;
import sid.models.TodoList;

/**
 * Command to mark a task as done.
 */
public class MarkCommand implements Command {
    @Override
    public CommandResult execute(String arg, TodoList tasks) throws SidException {
        if (arg.isEmpty()) {
            throw new SidException(ResponseMessage.MARK_USAGE_ERROR.getMessage());
        }
        int id = IndexParser.parseIndex(arg, ResponseMessage.MARK_INVALID_NUMBER.getMessage());
        ToDo updated = tasks.markDone(id);
        return new CommandResult(true, ResponseMessage.MARK_SUCCESS.getMessageWith(updated), updated, tasks.getSize());
    }
}
