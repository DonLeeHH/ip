package sid.commands;

import sid.exceptions.SidException;
import sid.models.ToDo;
import sid.models.TodoList;

/**
 * Command to create a new todo task.
 */
public class TodoCommand implements Command {
    @Override
    public CommandResult execute(String arg, TodoList tasks) throws SidException {
        if (arg.isEmpty()) {
            throw new SidException("Usage: todo <description>");
        }
        ToDo todo = new ToDo(arg, false);
        tasks.add(todo);
        return new CommandResult(true, "Successfully added\nTodo: " + todo.toString(), todo, tasks.getSize());
    }
}
