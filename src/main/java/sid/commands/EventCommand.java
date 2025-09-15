package sid.commands;

import java.time.LocalDateTime;

import sid.exceptions.SidException;
import sid.messages.ResponseMessage;
import sid.models.Event;
import sid.models.TodoList;

/**
 * Command to create a new event task.
 */
public class EventCommand implements Command {
    /** Required number of parts after splitting by "/from" or "/to": description and time. */
    private static final int REQUIRED_EVENT_PARTS = 2;
    @Override
    public CommandResult execute(String arg, TodoList tasks) throws SidException {
        if (arg.isEmpty()) {
            throw new SidException(ResponseMessage.EVENT_USAGE_ERROR.getMessage());
        }
        String[] a = arg.split("(?i)\\s*/from\\s+", REQUIRED_EVENT_PARTS);
        if (a.length < REQUIRED_EVENT_PARTS || a[0].isBlank()) {
            throw new SidException(ResponseMessage.EVENT_USAGE_ERROR.getMessage());
        }
        String desc = a[0].trim();
        String[] b = a[1].split("(?i)\\s*/to\\s+", REQUIRED_EVENT_PARTS);
        if (b.length < REQUIRED_EVENT_PARTS || b[0].isBlank() || b[1].isBlank()) {
            throw new SidException(ResponseMessage.EVENT_USAGE_ERROR.getMessage());
        }
        LocalDateTime start = DateTimeParser.parseFlexibleDateTime(b[0].trim());
        LocalDateTime end = DateTimeParser.parseFlexibleDateTime(b[1].trim());
        if (end.isBefore(start)) {
            throw new SidException(ResponseMessage.EVENT_INVALID_TIME_ORDER.getMessage());
        }
        assert !end.isBefore(start) : "Event end date constraint validated";
        Event e = new Event(desc, start, end, false);
        tasks.add(e);
        return new CommandResult(true, ResponseMessage.EVENT_SUCCESS.getMessageWith(e), e, tasks.getSize());
    }
}
