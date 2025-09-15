package sid.commands;

import sid.exceptions.SidException;
import sid.messages.ResponseMessage;
import sid.models.ToDo;
import sid.models.TodoList;

/**
 * Command to unmark a task (mark as not done).
 */
public class UnmarkCommand implements Command {
    @Override
    public CommandResult execute(String arg, TodoList tasks) throws SidException {
        if (arg.isEmpty()) {
            throw new SidException(ResponseMessage.UNMARK_USAGE_ERROR.getMessage());
        }
        int id = IndexParser.parseIndex(arg, ResponseMessage.UNMARK_INVALID_NUMBER.getMessage());
        ToDo updated = tasks.unmarkDone(id);
        return new CommandResult(true, ResponseMessage.UNMARK_SUCCESS.getMessageWith(updated), updated,
                tasks.getSize());
    }
}
