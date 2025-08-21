import java.util.Scanner;

public class Sid {
    public static final String HR = "_".repeat(60);
    private static TodoList todoList= new TodoList();
    private static boolean running = true;
    public static void main(String[] args) {
        try (Scanner sc = new Scanner(System.in)) {
            // greeting
            SpecialPrint("Hello! I'm Sid\nWhat can I do for you?");

            while (running) {
                if (!sc.hasNextLine()) break;           // handle EOF (Ctrl+D/Ctrl+Z)
                String raw = sc.nextLine();              // echo exactly what was typed
                String line = raw.trim();

                String[] parts = line.split("\\s+", 2);
                String cmd  = parts[0].toLowerCase();
                String arg = (parts.length > 1) ? parts[1].trim() : "";// remove whitespaces from both ends

                switch (cmd) {
                    case "bye":
                        SpecialPrint("ByeByeBye");
                        running = false;
                        break;
                    case "list":
                        SpecialPrint(todoList.toString());
                        break;
                    case "todo":
                        if (arg.isEmpty()) {
                            SpecialPrint(" Usage: todo <description>");
                            break;
                        }
                        todoList.add(new ToDo(arg));
                        break;
                    case "deadline":
                        if (arg.isEmpty()) {
                            SpecialPrint(" Usage: deadline <description> /by <when>");
                            break;
                        }
                        // split on "/by" with optional spaces around it, only once
                        String[] seg = arg.split("\\s*/by\\s+", 2);
                        if (seg.length < 2 || seg[0].isBlank() || seg[1].isBlank()) {
                            SpecialPrint(" Usage: deadline <description> /by <when>");
                            break;
                        }
                        String deadline_desc = seg[0].trim();
                        String when = seg[1].trim();
                        todoList.add(new Deadline(deadline_desc, when));
                        break;
                    case "event":
                        if (arg.isEmpty()) {
                            SpecialPrint(" Usage: event <description> /from <start> /to <end>");
                            break;
                        }
                        // Split on "/from", then on "/to" (case-insensitive, tolerate extra spaces)
                        String[] a = arg.split("(?i)\\s*/from\\s+", 2);
                        if (a.length < 2 || a[0].isBlank()) {
                            SpecialPrint(" Usage: event <description> /from <start> /to <end>");
                            break;
                        }
                        String desc = a[0].trim();

                        String[] b = a[1].split("(?i)\\s*/to\\s+", 2);
                        if (b.length < 2 || b[0].isBlank() || b[1].isBlank()) {
                            SpecialPrint(" Usage: event <description> /from <start> /to <end>");
                            break;
                        }
                        String start = b[0].trim();
                        String end = b[1].trim();

                        Event event = new Event(desc, start, end);
                        todoList.add(event);
                        break;
                    case "mark":
                    case "unmark": {
                        if (arg.isEmpty()) {
                            SpecialPrint(" Usage: " + cmd + " <task-number>");
                            break;
                        }

                        int id;
                        try {
                            id = Integer.parseInt(arg); // 1-based index
                        } catch (NumberFormatException e) {
                            SpecialPrint(" Please provide a valid number after 'mark'/'unmark'.");
                            break;
                        }

                        try {
                            ToDo updated = cmd.equals("mark")
                                    ? todoList.markDone(id)
                                    : todoList.unmarkDone(id);

                        } catch (SidException e) {

                        }
                        break;
                    }
                    default:
                        // ... other commands
                        SpecialPrint("OOPSS!!! I DON'T UNDERSTAND YOUU, GO TO README");
                        break;
                }
            }
        }
    }
    public static void SpecialPrint(String message){
        System.out.println(HR);
        System.out.println(message);
        System.out.println(HR);
    }
}
