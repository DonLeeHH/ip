package sid.models;

import sid.enums.TaskType;

/**
 * Represents a task with a due date that must be completed by a specified time.
 *
 * <p>A {@code Deadline} is a specialized {@link ToDo} whose type is {@link TaskType#DEADLINE}
 * and which carries an additional {@code dueDate} string used for display and persistence.
 */
public class Deadline extends ToDo {
    private String dueDate;

    /**
     * Constructs a deadline task with the given description, due date, and completion state.
     *
     * @param description Human-readable description of the task.
     * @param dueDate Due date/time label (free-form text such as {@code "June 6th"}).
     * @param isDone Whether the task is already marked as completed.
     */
    public Deadline(String description, String dueDate, boolean isDone) {
        super(description, isDone);
        this.dueDate = dueDate;
        this.type = TaskType.DEADLINE;
    }

    /**
     * Returns the due date label of this deadline task.
     *
     * @return Due date/time as a string.
     */
    public String getDueDate() {
        return this.dueDate;
    }

    @Override
    public String toString() {
        return super.toString() + " (by: " + this.dueDate + ")";
    }
}
