package sid;

import sid.enums.SidMsg;
import sid.exceptions.SidException;
import sid.models.TodoList;
import sid.parser.Parser;
import sid.storage.Storage;
import sid.ui.Ui;

/**
 * Application entry point that wires UI, storage, parser, and task list,
 * then runs a simple REPL loop.
 */
public class Sid {

    private final Storage storage;
    private final TodoList todoList;
    private final Ui ui;
    private final Parser parser;

    private boolean running = true;

    /**
     * Constructs the Sid application with the given save file path.
     *
     * @param filePath Relative path (e.g., {@code data/sid.txt}).
     */
    public Sid(String filePath) {
        this.ui = new Ui();
        this.storage = new Storage(filePath);
        this.todoList = storage.load();   // Storage returns a TodoList bound to itself
        this.parser = new Parser();
    }

    /** Runs the REPL until the user exits. */
    public void run() {
        ui.showWelcome();

        try {
            while (running && ui.hasNextLine()) {
                String input = ui.readLine();
                try {
                    boolean keepGoing = parser.parseAndExecute(input, todoList, ui);
                    if (!keepGoing) {
                        running = false;
                    }
                } catch (SidException e) {
                    ui.showError(e.getMessage());
                } catch (Exception e) {
                    String msg = (e.getMessage() == null || e.getMessage().trim().isEmpty())
                            ? "Something went wrong."
                            : e.getMessage();
                    ui.showError(msg);
                }
            }
        } finally {
            ui.close();
        }
    }

    public static void main(String[] args) {
        new Sid("data/sid.txt").run();
    }
}
