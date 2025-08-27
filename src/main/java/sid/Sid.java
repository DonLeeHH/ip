package sid;

import sid.enums.SidMsg;
import sid.exceptions.SidException;
import sid.models.Deadline;
import sid.models.Event;
import sid.models.ToDo;
import sid.models.TodoList;

import java.util.Scanner;

public class Sid {
    private static TodoList todoList= new TodoList();
    private static boolean running = true;
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
                            todoList.add(new ToDo(arg));
                            break;
                        case "deadline":
                            if (arg.isEmpty()) {
                                throw new SidException("Usage: deadline <description> /by <when>");
                            }
                            // split on "/by" with optional spaces around it, only once
                            String[] seg = arg.split("\\s*/by\\s+", 2);
                            if (seg.length < 2 || seg[0].isBlank() || seg[1].isBlank()) {
                                throw new SidException("Usage: deadline <description> /by <when>");
                            }
                            String deadline_desc = seg[0].trim();
                            String when = seg[1].trim();
                            todoList.add(new Deadline(deadline_desc, when));
                            break;
                        case "event":
                            if (arg.isEmpty()) {
                                throw new SidException("Usage: event <description> /from <start> /to <end>");
                            }
                            // Split on "/from", then on "/to" (case-insensitive, tolerate extra spaces)
                            String[] a = arg.split("(?i)\\s*/from\\s+", 2);
                            if (a.length < 2 || a[0].isBlank()) {
                                throw new SidException("Usage: event <description> /from <start> /to <end>");
                            }
                            String desc = a[0].trim();

                            String[] b = a[1].split("(?i)\\s*/to\\s+", 2);
                            if (b.length < 2 || b[0].isBlank() || b[1].isBlank()) {
                                throw new SidException("Usage: event <description> /from <start> /to <end>");
                            }
                            String start = b[0].trim();
                            String end = b[1].trim();

                            Event event = new Event(desc, start, end);
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
                                throw new SidException("Please provide a valid number after 'mark'/'unmark'.");
                            }
                            todoList.delete(id);
                            break;
                        }
                        default:
                            // ... other commands
                            throw new SidException("OOPSS!!! I DON'T UNDERSTAND YOUU, GO TO README");
                    }
                } catch (Exception e) {

                }

            }
        }
    }
    public static void SpecialPrint(String msg){
        System.out.println(SidMsg.HR);
        System.out.println(msg);
        System.out.println(SidMsg.HR);
    }
    public static void SpecialPrint(SidMsg SIDMSG){
        System.out.println(SidMsg.HR);
        System.out.println(SIDMSG);
        System.out.println(SidMsg.HR);
    }
}
