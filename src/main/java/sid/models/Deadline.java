package sid.models;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import sid.enums.TaskType;
import sid.exceptions.SidException;
import sid.messages.ResponseMessage;

/**
 * Represents a task with a due date/time.
 *
 * <p>Stores a {@link LocalDateTime} and formats it for display as
 * {@code "MMM dd yyyy"} or {@code "MMM dd yyyy HH:mm"} when time is present.
 */
public class Deadline extends ToDo {
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("MMM dd yyyy");
    private static final DateTimeFormatter DATE_TIME_FMT = DateTimeFormatter.ofPattern("MMM dd yyyy HH:mm");

    private LocalDateTime dueDate;

    /**
     * Constructs a deadline task.
     *
     * @param description Description of the task.
     * @param dueDate     Due date/time.
     * @param isDone      Completion flag.
     * @throws SidException If the due date is in the past.
     */
    public Deadline(String description, LocalDateTime dueDate, boolean isDone) throws SidException {
        super(description, isDone);
        assert dueDate != null : "Due date cannot be null";
        checkNotPastDate(dueDate);
        this.dueDate = dueDate;
        this.type = TaskType.DEADLINE;
    }

    /**
     * Validates that the given date/time is not in the past.
     *
     * @param dateTime The date/time to validate.
     * @throws SidException If the date/time is in the past.
     */
    private static void checkNotPastDate(LocalDateTime dateTime) throws SidException {
        if (dateTime.isBefore(LocalDateTime.now())) {
            throw new SidException(ResponseMessage.DEADLINE_PAST_DATE.getMessage());
        }
    }

    /**
     * Returns the due date/time.
     *
     * @return Due date/time.
     */
    public LocalDateTime getDueDate() {
        return this.dueDate;
    }

    private static String format(LocalDateTime dt) {
        assert dt != null : "DateTime to format cannot be null";
        return (dt.getHour() == 0 && dt.getMinute() == 0)
                ? dt.toLocalDate().format(DATE_FMT)
                : dt.format(DATE_TIME_FMT);
    }

    @Override
    public String toString() {
        return super.toString() + " (by: " + format(this.dueDate) + ")";
    }
}
