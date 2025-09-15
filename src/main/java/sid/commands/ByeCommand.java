package sid.commands;

import sid.exceptions.SidException;
import sid.models.TodoList;

/**
 * Command to exit the application.
 */
public class ByeCommand implements Command {
    @Override
    public CommandResult execute(String arg, TodoList tasks) throws SidException {
        return new CommandResult(false, "Byebye! See you next time!");
    }
}
