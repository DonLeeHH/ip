package sid.commands;

import java.time.LocalDateTime;

import sid.exceptions.SidException;
import sid.models.Deadline;
import sid.models.TodoList;

/**
 * Command to create a new deadline task.
 */
public class DeadlineCommand implements Command {
    /** Required number of parts after splitting by "/by": description and deadline. */
    private static final int REQUIRED_DEADLINE_PARTS = 2;
    @Override
    public CommandResult execute(String arg, TodoList tasks) throws SidException {
        if (arg.isEmpty()) {
            throw new SidException("Usage: deadline <description> /by <yyyy-MM-dd HHmm>");
        }
        String[] seg = arg.split("\\s*/by\\s+", REQUIRED_DEADLINE_PARTS);
        if (seg.length < REQUIRED_DEADLINE_PARTS || seg[0].isBlank() || seg[1].isBlank()) {
            throw new SidException("Usage: deadline <description> /by <yyyy-MM-dd HHmm>");
        }
        String desc = seg[0].trim();
        LocalDateTime when = DateTimeParser.parseFlexibleDateTime(seg[1].trim());
        Deadline d = new Deadline(desc, when, false);
        tasks.add(d);
        return new CommandResult(true, "Successfully added\nDeadline: " + d.toString(), d, tasks.getSize());
    }
}
