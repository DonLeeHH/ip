import java.util.ArrayList;
import java.util.Scanner;

public class Sid {
    public static final String HR = "_".repeat(60);
    private static TodoList todoList= new TodoList();

    public static void main(String[] args) {
        try (Scanner sc = new Scanner(System.in)) {
            // greeting
            SpecialPrint("Hello! I'm Sid \nWhat can I do for you?");

            while (true) {
                if (!sc.hasNextLine()) break;           // handle EOF (Ctrl+D/Ctrl+Z)
                String raw = sc.nextLine();              // echo exactly what was typed
                String cmd = raw.trim();                 // remove whitespaces from both ends

                if (cmd.equalsIgnoreCase("bye")) {
                    SpecialPrint("ByeByeBye");
                    break;
                }
                else if (cmd.equalsIgnoreCase("list")) {
                    System.out.println(todoList);
                } else if (cmd.toLowerCase().startsWith("mark ") || cmd.toLowerCase().startsWith("unmark ")) {
                    String[] parts = cmd.split("\\s+"); // Split the input on one-or-more spaces
                    if (parts.length == 2) {
                        try {
                            int id = Integer.parseInt(parts[1]);
                            boolean isMark = parts[0].equalsIgnoreCase("mark");
                            if (isMark) todoList.markDone(id); else todoList.unmarkDone(id); // toggle the task
                        } catch (NumberFormatException e) {
                            // The second token wasn't a valid integer (e.g., "mark two")
                            SpecialPrint(" Please provide a valid number after 'mark'/'unmark'.");
                        } catch (NotFoundException e) {
                            SpecialPrint(" Item index not found in Todo list");
                        }
                    }
                } else {
                    todoList.add(new Task(cmd));
                    SpecialPrint("added:" + raw);
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
