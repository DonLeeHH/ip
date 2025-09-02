package sid.ui;

import java.util.Scanner;

import sid.enums.SidMsg;
import sid.models.ToDo;
import sid.models.TodoList;

/**
 * Handles user interaction: reading input and printing formatted output.
 *
 * <p>All messages are framed by a horizontal rule for consistency.
 */
public class Ui {
    private final Scanner scanner;

    /** Constructs a UI bound to standard input. */
    public Ui() {
        this.scanner = new Scanner(System.in);
    }

    /** Shows the startup greeting. */
    public void showWelcome() {
        frame(SidMsg.GREETING.toString());
    }

    /** Shows the farewell message. */
    public void showGoodbye() {
        frame(SidMsg.GOODBYE.toString());
    }

    /** Shows a normal informational message. */
    public void showMessage(String message) {
        frame(message);
    }

    /** Shows an error message. */
    public void showError(String message) {
        frame(message);
    }

    // ---------- High-level task messages (used by Parser) ----------

    public void showList(TodoList tasks) {
        frame("Here are your tasks:\n" + tasks.toString());
    }

    /** Shows confirmation that a task has been added successfully*/
    public void showTaskAdded(ToDo task, int total) {
        frame("Got it. I've added this task:\n  " + task
                + "\nNow you have " + total + " tasks in the list.");
    }

    /** Shows confirmation that a task has been deleted successfully*/
    public void showTaskDeleted(ToDo task, int total) {
        frame("Successfully deleted this task:\n  " + task
                + "\nNow you have " + total + " tasks in the list.");
    }

    public void showTaskMarked(ToDo task) {
        frame("YAY! You've completed this task:\n  " + task);
    }

    public void showTaskUnmarked(ToDo task) {
        frame("OK, I've marked this task as not done yet:\n  " + task);
    }

    public void showFind(TodoList foundTasks) {
        frame("Here are the tasks I found:\n" + foundTasks.toString());
    }

    // ---------- Input plumbing ----------

    /** Returns true if there is another line available from input. */
    public boolean hasNextLine() {
        return scanner.hasNextLine();
    }

    /** Reads the next input line (may be empty). */
    public String readLine() {
        return scanner.nextLine();
    }

    /** Closes the underlying scanner. */
    public void close() {
        scanner.close();
    }

    private void frame(String msg) {
        System.out.println(SidMsg.HR);
        System.out.println(msg);
        System.out.println(SidMsg.HR);
    }
}
