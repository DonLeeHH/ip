package sid.models;

import sid.enums.TaskType;

/**
 * Represents a basic to-do task with a description and completion state.
 *
 * <p>The default task {@link #type} is {@link TaskType#TODO}. Instances can be marked
 * done/undone and are rendered in a Duke-style format, e.g.,
 * {@code [T][ ] read book} or {@code [T][X] read book}.
 */
public class ToDo {
    /** Categorical task type; defaults to {@link TaskType#TODO}. */
    protected TaskType type = TaskType.TODO;

    /** Human-readable description of the task. */
    private String description;

    /** Whether the task has been completed. */
    private boolean isDone;

    /**
     * Constructs a to-do task with the given description and completion flag.
     *
     * @param description Text describing what the task is about.
     * @param isDone      Whether the task is initially marked as completed.
     */
    public ToDo(String description, boolean isDone) {
        this.description = description;
        this.isDone = isDone;
    }

    /**
     * Marks this task as completed.
     */
    public void markTask() {
        this.isDone = true;
    }

    /**
     * Marks this task as not completed.
     */
    public void unmarkTask() {
        this.isDone = false;
    }

    public boolean isDone() {
        return this.isDone;
    }

    public String getDescription() {
        return this.description;
    }

    @Override
    public String toString() {
        return "[" + this.type + "][" + (this.isDone ? "X" : " ") + "] " + this.description;
    }
}
