package sid.models;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import sid.enums.TaskType;

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
     */
    public Deadline(String description, LocalDateTime dueDate, boolean isDone) {
        super(description, isDone);
        this.dueDate = dueDate;
        this.type = TaskType.DEADLINE;
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
        return (dt.getHour() == 0 && dt.getMinute() == 0)
                ? dt.toLocalDate().format(DATE_FMT)
                : dt.format(DATE_TIME_FMT);
    }

    @Override
    public String toString() {
        return super.toString() + " (by: " + format(this.dueDate) + ")";
    }
}
