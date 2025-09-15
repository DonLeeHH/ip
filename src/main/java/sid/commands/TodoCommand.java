package sid.commands;

import sid.exceptions.SidException;
import sid.messages.ResponseMessage;
import sid.models.ToDo;
import sid.models.TodoList;

/**
 * Command to create a new todo task.
 */
public class TodoCommand implements Command {
    @Override
    public CommandResult execute(String arg, TodoList tasks) throws SidException {
        if (arg.isEmpty()) {
            throw new SidException(ResponseMessage.TODO_USAGE_ERROR.getMessage());
        }
        ToDo todo = new ToDo(arg, false);
        tasks.add(todo);
        return new CommandResult(true, ResponseMessage.TODO_SUCCESS.getMessageWith(todo), todo, tasks.getSize());
    }
}
