package sid.models;

import sid.enums.TaskType;

/**
 * Represents an event task that spans a start and end time.
 *
 * <p>An {@code Event} is a specialized {@link ToDo} whose type is {@link TaskType#EVENT}
 * and which carries two additional labels: {@code startDate} and {@code endDate}. These
 * are free-form strings used for display and persistence (e.g., {@code "Aug 6th 2pm"}).
 */
public class Event extends ToDo {
    String startDate;
    String endDate;

    /**
     * Constructs an event task with the given description, start time, end time, and completion state.
     *
     * @param description Human-readable description of the event.
     * @param startDate   Start time label (e.g., {@code "Aug 6th 2pm"}).
     * @param dueDate     End time label (stored as {@code endDate}); the parameter name is kept for compatibility.
     * @param isDone      Whether the task is already marked as completed.
     */
    public Event(String description, String startDate, String dueDate, boolean isDone) {
        super(description, isDone);
        this.type = TaskType.EVENT;
        this.startDate = startDate;
        this.endDate = dueDate;
    }

    public String getStartDate() {
        return this.startDate;
    }

    public String getEndDate() {
        return this.endDate;
    }

    @Override
    public String toString() {
        return super.toString() + " (from: " + this.startDate + ", to: " + this.endDate + ")";
    }
}
