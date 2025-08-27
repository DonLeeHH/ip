package sid;

import sid.enums.SidMsg;
import sid.exceptions.SidException;
import sid.models.Deadline;
import sid.models.Event;
import sid.models.ToDo;
import sid.models.TodoList;
import sid.storage.Storage;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

/**
 * Runs the Sid chatbot REPL (read–eval–print loop) on the console.
 *
 * <p>The application loads tasks from disk on startup, prints a greeting, and then processes
 * user commands (e.g., {@code todo}, {@code deadline}, {@code event}, {@code list}, {@code mark},
 * {@code unmark}, {@code delete}, {@code bye}). Errors are reported to the user without terminating
 * the session. On exit, a farewell message is printed.
 */
public class Sid {
    // Storage at ./data/sid.txt (relative, OS-independent)
    private static final Storage STORAGE = new Storage("data/sid.txt");
    private static TodoList todoList= STORAGE.load();
    private static boolean running = true;

    /**
     * Starts the Sid chatbot and processes user commands until termination.
     *
     * <p>This method runs a line-based loop reading from standard input, parsing a command word and
     * its argument (if any), executing the corresponding action, and printing a formatted response.
     * The loop ends when the user enters {@code bye} or when end-of-file is encountered.
     *
     * @param args Command-line arguments (unused).
     */
    public static void main(String[] args) {
        try (Scanner sc = new Scanner(System.in)) {
            // greeting
            SpecialPrint(SidMsg.GREETING);

            while (running) {
                try {
                    if (!sc.hasNextLine()) break;           // handle EOF (Ctrl+D/Ctrl+Z)
                    String raw = sc.nextLine();              // echo exactly what was typed
                    String line = raw.trim();

                    String[] parts = line.split("\\s+", 2);
                    String cmd  = parts[0].toLowerCase();
                    String arg = (parts.length > 1) ? parts[1].trim() : "";// remove whitespaces from both ends

                    switch (cmd) {
                    case "bye":
                        SpecialPrint(SidMsg.GOODBYE);
                        running = false;
                        break;
                    case "list":
                        SpecialPrint(todoList.toString());
                        break;
                    case "todo":
                        if (arg.isEmpty()) {
                            throw new SidException("Usage: todo <description>");
                        }
                        todoList.add(new ToDo(arg, false));
                        break;
                    case "deadline":
                        if (arg.isEmpty()) {
                            throw new SidException("Usage: deadline <description> /by <yyyy-MM-dd HHmm>");
                        }
                        String[] seg = arg.split("\\s*/by\\s+", 2);
                        if (seg.length < 2 || seg[0].isBlank() || seg[1].isBlank()) {
                            throw new SidException("Usage: deadline <description> /by <yyyy-MM-dd HHmm>");
                        }
                        String deadline_desc = seg[0].trim();
                        LocalDateTime when = parseFlexibleDateTime(seg[1].trim());
                        todoList.add(new Deadline(deadline_desc, when, false));
                        break;
                    case "event":
                        if (arg.isEmpty()) {
                            throw new SidException("Usage: event <description> /from <yyyy-MM-dd HHmm> /to <yyyy-MM-dd HHmm>");
                        }
                        String[] a = arg.split("(?i)\\s*/from\\s+", 2);
                        if (a.length < 2 || a[0].isBlank()) {
                            throw new SidException("Usage: event <description> /from <yyyy-MM-dd HHmm> /to <yyyy-MM-dd HHmm>");
                        }
                        String desc = a[0].trim();
                        String[] b = a[1].split("(?i)\\s*/to\\s+", 2);
                        if (b.length < 2 || b[0].isBlank() || b[1].isBlank()) {
                            throw new SidException("Usage: event <description> /from <yyyy-MM-dd HHmm> /to <yyyy-MM-dd HHmm>");
                        }
                        LocalDateTime start = parseFlexibleDateTime(b[0].trim());
                        LocalDateTime end = parseFlexibleDateTime(b[1].trim());
                        Event event = new Event(desc, start, end, false);
                        todoList.add(event);
                        break;
                    case "mark":
                    case "unmark": {
                        if (arg.isEmpty()) {
                            throw new SidException("Usage: " + cmd + " <task-number>");
                        }

                        int id;
                        try {
                            id = Integer.parseInt(arg); // 1-based index
                        } catch (NumberFormatException e) {
                            throw new SidException("Please provide a valid number after 'mark'/'unmark'.");
                        }
                        ToDo updated = cmd.equals("mark") ? todoList.markDone(id) : todoList.unmarkDone(id);
                        break;
                    }
                    case "delete": {
                        if (arg.isEmpty()) {
                            throw new SidException("Usage: " + cmd + " <task-number>");
                        }
                        int id;
                        try {
                            id = Integer.parseInt(arg); // 1-based index
                        } catch (NumberFormatException e) {
                            throw new SidException("Please provide a valid number after 'delete'.");
                        }
                        todoList.delete(id);
                        break;
                    }
                    default:
                        throw new SidException("OOPSS!!! I DON'T UNDERSTAND YOUU, GO TO README");
                    }
                } catch (Exception e) {
                    SpecialPrint(e.getMessage());
                }
            }
        }
    }
    /**
     * Prints a plain string message framed by a horizontal rule.
     *
     * <p>This helper method formats the output consistently for normal text responses.
     *
     * @param msg Message to print.
     */
    public static void SpecialPrint(String msg){
        System.out.println(SidMsg.HR);
        System.out.println(msg);
        System.out.println(SidMsg.HR);
    }

    /**
     * Prints a {@link SidMsg} enum value framed by a horizontal rule.
     *
     * <p>This helper method formats predefined system messages (e.g., greetings, farewells).
     *
     * @param SIDMSG Predefined message to print.
     */
    public static void SpecialPrint(SidMsg SIDMSG){
        System.out.println(SidMsg.HR);
        System.out.println(SIDMSG);
        System.out.println(SidMsg.HR);
    }

    /**
     * Parses flexible date/time strings.
     *
     * <p>Accepted patterns (examples):
     * <ul>
     *   <li>{@code yyyy-MM-dd HHmm}  →  {@code 2019-12-02 1800}</li>
     *   <li>{@code yyyy-MM-dd}       →  {@code 2019-12-02}</li>
     *   <li>{@code d/M/yyyy HHmm}    →  {@code 2/12/2019 1800}</li>
     *   <li>{@code d/M/yyyy}         →  {@code 2/12/2019}</li>
     * </ul>
     * If only a date is supplied, time defaults to 00:00.
     *
     * @param text Input text to parse.
     * @return Parsed {@link LocalDateTime}.
     * @throws SidException If parsing fails for all supported patterns.
     */
    private static LocalDateTime parseFlexibleDateTime(String text) throws SidException {
        // Try date+time first
        DateTimeFormatter[] dateTimePatterns = new DateTimeFormatter[] {
                DateTimeFormatter.ofPattern("yyyy-MM-dd HHmm"),
                DateTimeFormatter.ofPattern("d/M/yyyy HHmm")
        };
        for (DateTimeFormatter f : dateTimePatterns) {
            try {
                return LocalDateTime.parse(text, f);
            } catch (DateTimeParseException ignore) {
                /* try next */
            }
        }

        // Try date-only, default to 00:00
        DateTimeFormatter[] dateOnlyPatterns = new DateTimeFormatter[] {
                DateTimeFormatter.ofPattern("yyyy-MM-dd"),
                DateTimeFormatter.ofPattern("d/M/yyyy")
        };
        for (DateTimeFormatter f : dateOnlyPatterns) {
            try {
                LocalDate d = LocalDate.parse(text, f);
                return LocalDateTime.of(d, LocalTime.MIDNIGHT);
            } catch (DateTimeParseException ignore) {
                /* try next */
            }
        }

        throw new SidException("Could not parse date/time: " + text +
                "\nTry formats like: 2025-12-02 1800 or 2/12/2025 1800");
    }
}