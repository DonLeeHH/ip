package sid.commands;

import sid.exceptions.SidException;
import sid.messages.ResponseMessage;
import sid.models.TodoList;

/**
 * Command to exit the application.
 */
public class ByeCommand implements Command {
    @Override
    public CommandResult execute(String arg, TodoList tasks) throws SidException {
        return new CommandResult(false, ResponseMessage.BYE_MESSAGE.getMessage());
    }
}
