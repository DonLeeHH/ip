package sid.commands;

import sid.exceptions.SidException;
import sid.messages.ResponseMessage;
import sid.models.TodoList;

/**
 * Command to find tasks matching a keyword.
 */
public class FindCommand implements Command {
    @Override
    public CommandResult execute(String arg, TodoList tasks) throws SidException {
        if (arg.isEmpty()) {
            throw new SidException(ResponseMessage.FIND_USAGE_ERROR.getMessage());
        }
        TodoList foundTodos = tasks.findTodos(arg);
        if (foundTodos.isEmpty()) {
            return new CommandResult(true, ResponseMessage.FIND_NO_RESULTS.getMessage(), foundTodos);
        } else {
            return new CommandResult(true, ResponseMessage.FIND_SUCCESS.getMessageWith(foundTodos), foundTodos);
        }
    }
}
