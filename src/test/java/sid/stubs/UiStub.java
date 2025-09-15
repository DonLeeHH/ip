package sid.stubs;

import java.util.ArrayList;
import java.util.List;

import sid.models.ToDo;
import sid.models.TodoList;
import sid.ui.Ui;

/**
 * Test double for UI that captures output calls for verification.
 *
 * Created with assistance from Claude AI to provide comprehensive
 * UI testing capabilities without actual user interaction.
 */
public class UiStub extends Ui {
    private final List<String> capturedMessages = new ArrayList<>();
    private boolean goodbyeCalled = false;

    /**
     * Returns all captured messages in order.
     * @return list of captured messages
     */
    public List<String> getCapturedMessages() {
        return new ArrayList<>(capturedMessages);
    }

    /**
     * Returns whether showGoodbye was called.
     * @return true if goodbye was shown
     */
    public boolean isGoodbyeCalled() {
        return goodbyeCalled;
    }

    /**
     * Clears all captured state for reuse in multiple tests.
     */
    public void reset() {
        capturedMessages.clear();
        goodbyeCalled = false;
    }

    @Override
    public void showGoodbye() {
        goodbyeCalled = true;
        capturedMessages.add("GOODBYE");
    }

    @Override
    public void showError(String message) {
        capturedMessages.add("ERROR: " + message);
    }

    @Override
    public void showList(TodoList tasks) {
        capturedMessages.add("LIST: " + tasks.getSize() + " tasks");
    }

    @Override
    public void showTaskAdded(ToDo task, int totalTasks) {
        capturedMessages.add("ADDED: " + task.toString() + " (total: " + totalTasks + ")");
    }

    @Override
    public void showTaskMarked(ToDo task) {
        capturedMessages.add("MARKED: " + task.toString());
    }

    @Override
    public void showTaskUnmarked(ToDo task) {
        capturedMessages.add("UNMARKED: " + task.toString());
    }

    @Override
    public void showTaskDeleted(ToDo task, int totalTasks) {
        capturedMessages.add("DELETED: " + task.toString() + " (total: " + totalTasks + ")");
    }

    @Override
    public void showFind(TodoList foundTasks) {
        capturedMessages.add("FOUND: " + foundTasks.getSize() + " matching tasks");
    }
}
