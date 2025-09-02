package sid.parser;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import sid.exceptions.SidException;
import sid.models.Deadline;
import sid.models.Event;
import sid.models.ToDo;
import sid.models.TodoList;
import sid.ui.Ui;

/**
 * Parses user commands and executes them against the task list.
 *
 * <p>Level 8: Dates/times are parsed into {@link LocalDateTime}. Accepted inpu
 * formats include {@code yyyy-MM-dd[ HHmm]} and {@code d/M/yyyy[ HHmm]}; ISO
 * {@code yyyy-MM-dd'T'HH:mm} is also supported. Date-only inputs default to
 * {@code 00:00}.
 */
public class Parser {

    /**
     * Parses and executes a single command line.
     *
     * <p>Returns {@code false} when the session should end (on {@code bye});
     * returns {@code true} otherwise.
     *
     * @param input Raw user input.
     * @param tasks Task list to operate on.
     * @param ui    UI for user-visible output.
     * @return {@code false} to terminate; {@code true} to continue.
     * @throws SidException If the command/arguments are invalid.
     */
    public boolean parseAndExecute(String input, TodoList tasks, Ui ui) throws SidException {
        if (input == null) {
            throw new SidException("No input provided.");
        }

        String line = input.trim();
        if (line.isEmpty()) {
            return true; // ignore empty lines
        }

        String[] parts = line.split("\\s+", 2);
        String cmd = parts[0].toLowerCase();
        String arg = (parts.length > 1) ? parts[1].trim() : "";

        switch (cmd) {
        case "bye":
            ui.showGoodbye();
            return false;

        case "list":
            ui.showList(tasks);
            return true;

        case "todo": {
            if (arg.isEmpty()) {
                throw new SidException("Usage: todo <description>");
            }
            ToDo todo = new ToDo(arg, false);
            tasks.add(todo);
            ui.showTaskAdded(todo, tasks.getSize());
            return true;
        }

        case "deadline": {
            if (arg.isEmpty()) {
                throw new SidException("Usage: deadline <description> /by <yyyy-MM-dd HHmm>");
            }
            String[] seg = arg.split("\\s*/by\\s+", 2);
            if (seg.length < 2 || seg[0].isBlank() || seg[1].isBlank()) {
                throw new SidException("Usage: deadline <description> /by <yyyy-MM-dd HHmm>");
            }
            String desc = seg[0].trim();
            LocalDateTime when = parseFlexibleDateTime(seg[1].trim());
            Deadline d = new Deadline(desc, when, false);
            tasks.add(d);
            ui.showTaskAdded(d, tasks.getSize());
            return true;
        }

        case "event": {
            if (arg.isEmpty()) {
                throw new SidException("Usage: event <description> /from <yyyy-MM-dd[ HHmm]> /to <yyyy-MM-dd HHmm>");
            }
            String[] a = arg.split("(?i)\\s*/from\\s+", 2);
            if (a.length < 2 || a[0].isBlank()) {
                throw new SidException("Usage: event <description> /from <yyyy-MM-dd[ HHmm]> /to <yyyy-MM-dd HHmm>");
            }
            String desc = a[0].trim();
            String[] b = a[1].split("(?i)\\s*/to\\s+", 2);
            if (b.length < 2 || b[0].isBlank() || b[1].isBlank()) {
                throw new SidException("Usage: event <description> /from <yyyy-MM-dd HHmm> /to <yyyy-MM-dd HHmm>");
            }
            LocalDateTime start = parseFlexibleDateTime(b[0].trim());
            LocalDateTime end = parseFlexibleDateTime(b[1].trim());
            if (end.isBefore(start)) {
                throw new SidException("Event end must be on/after start.");
            }
            Event e = new Event(desc, start, end, false);
            tasks.add(e);
            ui.showTaskAdded(e, tasks.getSize());
            return true;
        }

        case "mark": {
            if (arg.isEmpty()) {
                throw new SidException("Usage: mark <task-number>");
            }
            int id = parseIndex(arg, "Please provide a valid number after 'mark'.");
            ToDo updated = tasks.markDone(id);
            ui.showTaskMarked(updated);
            return true;
        }

        case "unmark": {
            if (arg.isEmpty()) {
                throw new SidException("Usage: unmark <task-number>");
            }
            int id = parseIndex(arg, "Please provide a valid number after 'unmark'.");
            ToDo updated = tasks.unmarkDone(id);
            ui.showTaskUnmarked(updated);
            return true;
        }

        case "delete": {
            if (arg.isEmpty()) {
                throw new SidException("Usage: delete <task-number>");
            }
            int id = parseIndex(arg, "Please provide a valid number after 'delete'.");
            // capture the item first so we can show it after deletion
            ToDo toRemove = tasks.getTodo(id);
            tasks.delete(id);
            ui.showTaskDeleted(toRemove, tasks.getSize());
            return true;
        }

        case "find": {
            if (arg.isEmpty()) {
                throw new SidException("Usage: find <keyword>");
            }
            TodoList foundTodos = tasks.findTodos(arg);
            if (foundTodos.isEmpty()) {
                ui.showError("No tasks found.");
            } else {
                ui.showFind(foundTodos);
            }
            return true;
        }

        default:
            throw new SidException(
                    "Unknown command. Try: todo | deadline | event | list | mark <n> | unmark <n> | delete <n> | bye"
            );
        }
    }


    /**
     * Parses and executes the command input from JavaFX GUI.
     *
     * <p>Returns response string
     *
     * @param input Raw user input.
     * @param tasks Task list to operate on.
     */
    public String parseAndExecute(String input, TodoList tasks) {
        String line = input.trim();
        if (line.isEmpty()) {
            return "Try: todo | deadline | event | list | mark <n> | unmark <n> | delete <n>";
        }

        String[] parts = line.split("\\s+", 2);
        String cmd = parts[0].toLowerCase();
        String arg = (parts.length > 1) ? parts[1].trim() : "";

        try {
            switch (cmd) {
            case "list":
                if (tasks.isEmpty()) {
                    return "You currently have no tasks!";
                }
                return "Here are your tasks:\n" + tasks.toString();

            case "todo": {
                if (arg.isEmpty()) {
                    return "Usage: todo <description>";
                }
                ToDo todo = new ToDo(arg, false);
                tasks.add(todo);
                return "Successfully added\nTodo: " + todo.toString();
            }

            case "deadline": {
                if (arg.isEmpty()) {
                    return "Usage: deadline <description> /by <yyyy-MM-dd HHmm>";
                }
                String[] seg = arg.split("\\s*/by\\s+", 2);
                if (seg.length < 2 || seg[0].isBlank() || seg[1].isBlank()) {
                    return "Usage: deadline <description> /by <yyyy-MM-dd HHmm>";
                }
                String desc = seg[0].trim();
                LocalDateTime when = parseFlexibleDateTime(seg[1].trim());
                Deadline d = new Deadline(desc, when, false);
                tasks.add(d);
                return "Successfully added\nDeadline: " + d.toString();
            }

            case "event": {
                if (arg.isEmpty()) {
                    return "Usage: event <description> /from <yyyy-MM-dd[ HHmm]> /to <yyyy-MM-dd HHmm>";
                }
                String[] a = arg.split("(?i)\\s*/from\\s+", 2);
                if (a.length < 2 || a[0].isBlank()) {
                    return "Usage: event <description> /from <yyyy-MM-dd[ HHmm]> /to <yyyy-MM-dd HHmm>";
                }
                String desc = a[0].trim();
                String[] b = a[1].split("(?i)\\s*/to\\s+", 2);
                if (b.length < 2 || b[0].isBlank() || b[1].isBlank()) {
                    return "Usage: event <description> /from <yyyy-MM-dd HHmm> /to <yyyy-MM-dd HHmm>";
                }
                LocalDateTime start = parseFlexibleDateTime(b[0].trim());
                LocalDateTime end = parseFlexibleDateTime(b[1].trim());
                if (end.isBefore(start)) {
                    return "Event end must be on/after start.";
                }
                Event e = new Event(desc, start, end, false);
                tasks.add(e);
                return "Successfully added\n Event: " + e.toString();
            }

            case "mark": {
                if (arg.isEmpty()) {
                    return "Usage: mark <task-number>";
                }
                int id = parseIndex(arg, "Please provide a valid number after 'mark'.");
                ToDo updated = tasks.markDone(id);
                return "Successfully marked task number " + id;
            }

            case "unmark": {
                if (arg.isEmpty()) {
                    return "Usage: unmark <task-number>";
                }
                int id = parseIndex(arg, "Please provide a valid number after 'unmark'.");
                ToDo updated = tasks.unmarkDone(id);
                return "Successfully unmarked task number " + id;
            }

            case "delete": {
                if (arg.isEmpty()) {
                    return "Usage: delete <task-number>";
                }
                int id = parseIndex(arg, "Please provide a valid number after 'delete'.");
                // capture the item first so we can show it after deletion
                ToDo toRemove = tasks.getTodo(id);
                tasks.delete(id);
                return "Successfully deleted task:\n" + toRemove.toString();
            }

            case "find": {
                if (arg.isEmpty()) {
                    return "Usage: find <keyword>";
                }
                TodoList foundTodos = tasks.findTodos(arg);
                if (foundTodos.isEmpty()) {
                    return "No tasks found";
                } else {
                    return "Here are the tasks I found:\n" + foundTodos.toString();
                }
            }

            case "bye": {
                return "Goodbye!";
            }

            default:
                return "Unknown command. Try: todo | deadline | event | list | mark <n> | unmark <n> | delete <n>";
            }
        } catch (SidException error) {
            return error.getMessage();
        }
    }

    // ---- helpers ------------------------------------------------------------

    private int parseIndex(String s, String errorMsg) throws SidException {
        try {
            return Integer.parseInt(s.trim());
        } catch (NumberFormatException e) {
            throw new SidException(errorMsg);
        }
    }

    /** Tries several patterns; if only a date is present, time defaults to 00:00. */
    private LocalDateTime parseFlexibleDateTime(String text) throws SidException {
        // date + time
        DateTimeFormatter[] dateTimePatterns = new DateTimeFormatter[] {
            DateTimeFormatter.ofPattern("yyyy-MM-dd HHmm"),
            DateTimeFormatter.ofPattern("d/M/yyyy HHmm"),
            DateTimeFormatter.ISO_LOCAL_DATE_TIME // e.g., 2019-12-02T18:00
        };
        for (DateTimeFormatter f : dateTimePatterns) {
            try {
                return LocalDateTime.parse(text, f);
            } catch (DateTimeParseException ignore) { /* try next */ }
        }

        // date only -> midnigh
        DateTimeFormatter[] dateOnlyPatterns = new DateTimeFormatter[] {
                DateTimeFormatter.ofPattern("yyyy-MM-dd"),
                DateTimeFormatter.ofPattern("d/M/yyyy")
        };
        for (DateTimeFormatter f : dateOnlyPatterns) {
            try {
                LocalDate d = LocalDate.parse(text, f);
                return LocalDateTime.of(d, LocalTime.MIDNIGHT);
            } catch (DateTimeParseException ignore) { /* try next */ }
        }

        throw new SidException(
                "Could not parse date/time: " + text
                        + "\nTry: 2025-12-02 1800, 2025-12-02, 2/12/2025 1800, or 2/12/2025"
        );
    }
}
