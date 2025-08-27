package sid.storage;

import sid.enums.TaskType;
import sid.exceptions.SidException;
import sid.models.Deadline;
import sid.models.Event;
import sid.models.ToDo;
import sid.models.TodoList;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Persists and retrieves tasks using a simple pipe-separated flat file.
 *
 * <p>Each task occupies one line with optional whitespace around {@code |}. Examples:
 * <pre>
 * T | 1 | read book
 * D | 0 | return book | June 6th
 * E | 0 | project meeting | Aug 6th 2pm | Aug 6th 4pm
 * </pre>
 * The second field is a done flag ({@code 1} = done, {@code 0} = not done).
 * Missing files are handled gracefully on load; malformed lines are skipped and logged to {@code System.err}.
 */
public class Storage {
    private final File file;

    // Include JavaDoc later
    /**
     * Constructs a storage backed by the specified file path.
     *
     * @param relativePath Path to the save file (e.g., {@code data/duke.txt}); may be relative or absolute.
     */
    public Storage(String relativePath) {
        this.file = new File(relativePath);
    }

    /**
     * Loads tasks from disk into a new {@link TodoList}.
     *
     * <p>If the file does not exist, an empty list is returned. Corrupted lines are skipped with a warning.
     *
     * @return A {@link TodoList} containing all successfully parsed tasks; empty if no file exists.
     */
    public TodoList load() {
        List<ToDo> initialList = new ArrayList<>();

        if (!file.exists()) {
            // First run, nothing to load yet.
            return new TodoList(initialList, this);
        }
        try {
            Scanner s = new Scanner(file);
            while (s.hasNextLine()) {
                String line = s.nextLine().trim();
                if (line.isEmpty()) {
                    continue;
                }

                try {
                    ToDo todo = deserializeToDo(line);
                    initialList.add(todo);
                } catch (SidException e) {
                    System.err.println("Skipping corrupted line: " + line + ": " + e.getMessage());
                }
            }
        } catch (FileNotFoundException e) {
            System.err.println("Load failed (not found): " + file.getAbsolutePath());
        }
        return new TodoList(initialList, this);
    }

    /**
     * Saves the entire task list to disk, overwriting any existing content.
     *
     * <p>The parent directory (e.g., {@code ./data}) is created if missing. Any serialization problems are logged.
     *
     * @param list The task list to persist.
     */
    public void save(TodoList list) {
        // Ensure ./data exists
        File parent = this.file.getParentFile();
        if (parent != null && !parent.exists()) {
            parent.mkdirs(); // safe even if it already exists
        }

        try (FileWriter fw = new FileWriter(this.file, false)) { // overwrite mode
            for (int i = 0; i < list.getSize(); i++) {
                ToDo t = list.getTodo(i); // 1-based like your public API
                fw.write(serializeTodo(t));
                fw.write(System.lineSeparator());
            }
        } catch (SidException | IOException e) {
            System.err.println("Failed to save tasks: " + e.getMessage());
        }
    }

    private String serializeTodo(ToDo t) throws SidException {
        TaskType type;
        String extra = "";

        if (t instanceof Deadline) {
            Deadline deadline = (Deadline) t;
            type = TaskType.DEADLINE;
            extra = deadline.getDueDate();

        } else if (t instanceof Event) {
            Event event = (Event) t;
            type = TaskType.EVENT;
            extra = event.getStartDate() + " | " + event.getEndDate();
        } else if (t instanceof ToDo) {
            type = TaskType.TODO;
        } else {
            throw new SidException("Unknown task type: " + t.getClass().getName());
        }

        int done = t.isDone() ? 1 : 0;
        String base = type.toString() + " | " + done + " | " + t.getDescription();
        return extra.isEmpty() ? base : base + " | " + extra;
    }

    /** Parses a line back into a ToDo. Accepts:
     *   "T | 1 | description"
     *   "T | 0 | description"
     */
    private ToDo deserializeToDo(String line) throws SidException {
        String[] parts = line.split("\\s*\\|\\s*");
        if (parts.length < 3) {
            throw new IllegalArgumentException("Too few fields");
        }
        String type = parts[0].trim();
        String doneFlag = parts[1].trim();
        if (!doneFlag.equals("1") && !doneFlag.equals("0")) {
            throw new SidException("Invalid done flag");
        }
        boolean isDone = doneFlag.equals("1");
        String description = parts[2].trim();
        ToDo task;

        switch (TaskType.fromCode(type)) {
        case TODO:
            task = new ToDo(description, isDone);
            break;

        case DEADLINE:
            if (parts.length < 4) {
                throw new SidException("Deadline missing 'by' field");
            }
            String by = parts[3];
            task = new Deadline(description, by, isDone);
            break;

        case EVENT:
            if (parts.length < 5) {
                throw new SidException("Event missing start/end fields");
            }
            String start = parts[3];
            String end = parts[4];
            task = new Event(description, start, end, isDone);
            break;

        default:
            throw new SidException("Unsupported type: " + type);
        }
        return task;
    }
}
