package sid.models;

public class Deadline extends ToDo {
    private String dueDate;

    public Deadline(String description, String dueDate) {
        super(description);
        this.dueDate = dueDate;
        this.type = 'D';
    }

    @Override
    public String toString() {
        return super.toString() + " (by: " + this.dueDate + ")";
    }
}
