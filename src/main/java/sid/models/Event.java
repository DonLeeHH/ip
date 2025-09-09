package sid.models;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import sid.enums.TaskType;

/**
 * Represents an event task that spans a start and end time.
 *
 * <p>An {@code Event} is a specialized {@link ToDo} whose type is {@link TaskType#EVENT}
 * and which carries two additional labels: {@code startDate} and {@code endDate}. These
 * are free-form strings used for display and persistence (e.g., {@code "Aug 6th 2pm"}).
 */
public class Event extends ToDo {
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("MMM dd yyyy");
    private static final DateTimeFormatter DATE_TIME_FMT = DateTimeFormatter.ofPattern("MMM dd yyyy HH:mm");

    private LocalDateTime startDate;
    private LocalDateTime endDate;

    /**
     * Constructs an event task.
     *
     * @param description Description of the event.
     * @param startDate   Start date/time.
     * @param endDate     End date/time.
     * @param isDone      Completion flag.
     */
    public Event(String description, LocalDateTime startDate, LocalDateTime endDate, boolean isDone) {
        super(description, isDone);
        assert startDate != null : "Start date cannot be null";
        assert endDate != null : "End date cannot be null";
        assert !startDate.isAfter(endDate) : "Start date must be before or equal to end date";
        this.type = TaskType.EVENT;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public LocalDateTime getStartDate() {
        return this.startDate;
    }

    public LocalDateTime getEndDate() {
        return this.endDate;
    }

    private static String format(LocalDateTime dt) {
        assert dt != null : "DateTime to format cannot be null";
        return (dt.getHour() == 0 && dt.getMinute() == 0)
                ? dt.toLocalDate().format(DATE_FMT)
                : dt.format(DATE_TIME_FMT);
    }

    @Override
    public String toString() {
        return super.toString() + " (from: " + format(this.startDate) + ", to: " + format(this.endDate) + ")";
    }
}
