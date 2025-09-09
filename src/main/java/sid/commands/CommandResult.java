package sid.commands;

import sid.models.ToDo;
import sid.models.TodoList;

/**
 * Result of executing a command.
 *
 * <p>Encapsulates the outcome of a command execution including whether the program
 * should continue running, a message for display, and any relevant task data.
 */
public class CommandResult {
    private final boolean shouldContinue;
    private final String message;
    private final ToDo task;
    private final int totalTasks;
    private final TodoList foundTasks;

    /**
     * Constructor for simple messages.
     *
     * @param shouldContinue Whether the program should continue running.
     * @param message The message to display.
     */
    public CommandResult(boolean shouldContinue, String message) {
        this(shouldContinue, message, null, 0, null);
    }

    /**
     * Constructor for task operations.
     *
     * @param shouldContinue Whether the program should continue running.
     * @param message The message to display.
     * @param task The task that was operated on.
     * @param totalTasks The total number of tasks after the operation.
     */
    public CommandResult(boolean shouldContinue, String message, ToDo task, int totalTasks) {
        this(shouldContinue, message, task, totalTasks, null);
    }

    /**
     * Constructor for find operations.
     *
     * @param shouldContinue Whether the program should continue running.
     * @param message The message to display.
     * @param foundTasks The list of tasks found by the search.
     */
    public CommandResult(boolean shouldContinue, String message, TodoList foundTasks) {
        this(shouldContinue, message, null, 0, foundTasks);
    }

    /**
     * Full constructor.
     *
     * @param shouldContinue Whether the program should continue running.
     * @param message The message to display.
     * @param task The task that was operated on (may be null).
     * @param totalTasks The total number of tasks after the operation.
     * @param foundTasks The list of tasks found by search (may be null).
     */
    public CommandResult(boolean shouldContinue, String message, ToDo task, int totalTasks, TodoList foundTasks) {
        this.shouldContinue = shouldContinue;
        this.message = message;
        this.task = task;
        this.totalTasks = totalTasks;
        this.foundTasks = foundTasks;
    }

    /**
     * Returns whether the program should continue running.
     *
     * @return true if the program should continue, false to terminate.
     */
    public boolean shouldContinue() {
        return shouldContinue;
    }

    /**
     * Returns the message to display.
     *
     * @return The display message.
     */
    public String getMessage() {
        return message;
    }

    /**
     * Returns the task that was operated on.
     *
     * @return The task, or null if not applicable.
     */
    public ToDo getTask() {
        return task;
    }

    /**
     * Returns the total number of tasks.
     *
     * @return The total task count.
     */
    public int getTotalTasks() {
        return totalTasks;
    }

    /**
     * Returns the list of tasks found by search.
     *
     * @return The found tasks, or null if not applicable.
     */
    public TodoList getFoundTasks() {
        return foundTasks;
    }
}
