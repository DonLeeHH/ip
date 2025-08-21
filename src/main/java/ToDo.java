public class ToDo {
    private String description;
    private boolean isDone;
    protected char type = 'T';

    public ToDo(String description) {
        this.description = description;
        this.isDone = false;
    }

    public void markTask() {
        this.isDone = true;
    }

    public void unmarkTask() {
        this.isDone = false;
    }

    @Override
    public String toString() {
        return "[" + this.type + "][" + (isDone ? "X" : " ") + "] " + description;
    }
}
