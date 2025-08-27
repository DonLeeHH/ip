package sid.models;

public class Event extends ToDo {
    String startDate;
    String endDate;
    public Event(String description, String startDate, String dueDate) {
        super(description);
        this.type = 'E';
        this.startDate = startDate;
        this.endDate = dueDate;
    }

    @Override
    public String toString() {
        return super.toString() + " (from: " + this.startDate + ", to: " + this.endDate + ")";
    }
}
