package sid.storage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import sid.enums.TaskType;
import sid.exceptions.SidException;
import sid.models.Deadline;
import sid.models.Event;
import sid.models.ToDo;
import sid.models.TodoList;

/**
 * Persists and retrieves tasks in a pipe-separated flat file.
 *
 * <p>Each record occupies one line; optional whitespace is allowed around
 * the {@code |} separators. The second field is a done flag
 * ({@code 1} = done, {@code 0} = not done).
 *
 * <p>Level 8 dates/times are stored in ISO-8601 {@code LocalDateTime} format
 * (e.g., {@code 2019-12-02T18:00}) for unambiguous parsing. Examples:
 * <pre>
 * T | 1 | read book
 * D | 0 | return book | 2019-12-02T18:00
 * E | 0 | project meeting | 2019-08-06T14:00 | 2019-08-06T16:00
 * </pre>
 *
 * <p>On load, a missing file is treated as empty; malformed lines are skipped
 * with a warning to {@code System.err}. On save, the parent directory is
 * created if it does not exist.
 */

public class Storage {
    private static final DateTimeFormatter ISO_DT = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    /** Minimum fields required in storage format: type, done flag, description. */
    private static final int MIN_STORAGE_FIELDS = 3;

    /** Fields required for deadline tasks: type, done flag, description, due date. */
    private static final int DEADLINE_FIELDS = 4;

    /** Fields required for event tasks: type, done flag, description, start date, end date. */
    private static final int EVENT_FIELDS = 5;

    private static final String DONE_FLAG = "1";

    private static final String NOT_DONE_FLAG = "0";

    private final File file;

    /**
     * Constructs a storage backed by the specified file path.
     *
     * @param relativePath Path to save file (e.g., {@code data/sid.txt}).
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
     * Saves the current list into the disk
     * @param list The current list
     */
    public void save(TodoList list) {
        // Ensure ./data exists
        File parent = this.file.getParentFile();
        if (parent != null && !parent.exists()) {
            parent.mkdirs(); // safe even if it already exists
        }

        try (FileWriter fw = new FileWriter(this.file, false)) { // overwrite mode
            for (int i = 1; i <= list.getSize(); i++) {
                ToDo t = list.getTodo(i);
                fw.write(serializeTodo(t));
                fw.write(System.lineSeparator());
            }
        } catch (SidException | IOException e) {
            System.err.println("Failed to save tasks: " + e.getMessage());
        }
    }

    /**
     * Returns the serialized string representing a todo to be saved onto the disk
     * @param t The task to be serialized into a string
     * @return a serialized string
     * @throws SidException If task is of unknown type
     * @see sid.enums.TaskType
     */
    private String serializeTodo(ToDo t) throws SidException {
        TaskType type;
        String extra = "";

        if (t instanceof Deadline) {
            Deadline deadline = (Deadline) t;
            type = TaskType.DEADLINE;
            extra = deadline.getDueDate().format(ISO_DT);

        } else if (t instanceof Event) {
            Event event = (Event) t;
            type = TaskType.EVENT;
            extra = event.getStartDate().format(ISO_DT) + " | " + event.getEndDate().format(ISO_DT);

        } else if (t instanceof ToDo) {
            type = TaskType.TODO;

        } else {
            throw new SidException("Unknown task type: " + t.getClass().getName());
        }

        String done = t.isDone() ? DONE_FLAG : NOT_DONE_FLAG;
        String base = type.toString() + " | " + done + " | " + t.getDescription();
        return extra.isEmpty() ? base : base + " | " + extra;
    }

    /**
     * Parses a line into a task instance.
     *
     * <p>Expected formats (ISO date-times for D/E):
     * <pre>
     * T | 0|1 | description
     * D | 0|1 | description | 2019-12-02T18:00
     * E | 0|1 | description | 2019-12-02T18:00 | 2019-12-02T20:00
     * </pre>
     */
    private ToDo deserializeToDo(String line) throws SidException {
        String[] parts = line.split("\\s*\\|\\s*");
        if (parts.length < MIN_STORAGE_FIELDS) {
            throw new IllegalArgumentException("Too few fields");
        }
        String type = parts[0].trim();
        String doneFlag = parts[1].trim();
        if (!doneFlag.equals(DONE_FLAG) && !doneFlag.equals(NOT_DONE_FLAG)) {
            throw new SidException("Invalid done flag");
        }
        boolean isDone = doneFlag.equals(DONE_FLAG);
        String description = parts[2].trim();
        ToDo task;

        switch (TaskType.fromCode(type)) {
        case TODO:
            task = new ToDo(description, isDone);
            break;

        case DEADLINE:
            // Guard clause for validation
            if (parts.length < DEADLINE_FIELDS) {
                throw new SidException("Deadline missing 'by' field");
            }
            LocalDateTime by = LocalDateTime.parse(parts[3].trim(), ISO_DT);
            task = new Deadline(description, by, isDone);
            break;

        case EVENT:
            // Guard clause for validation
            if (parts.length < EVENT_FIELDS) {
                throw new SidException("Event missing start/end fields");
            }
            LocalDateTime start = LocalDateTime.parse(parts[3].trim(), ISO_DT);
            LocalDateTime end = LocalDateTime.parse(parts[4].trim(), ISO_DT);
            task = new Event(description, start, end, isDone);
            break;

        default:
            throw new SidException("Unsupported type: " + type);
        }
        return task;
    }
}
