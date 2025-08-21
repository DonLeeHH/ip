import java.util.Scanner;

public class Sid {
    private static final String HR = "_".repeat(60);

    public static void main(String[] args) {
        try (Scanner sc = new Scanner(System.in)) {
            // greeting
            System.out.println(HR);
            System.out.println(" Hello! I'm Sid");
            System.out.println(" What can I do for you?");
            System.out.println(HR);

            while (true) {
                if (!sc.hasNextLine()) break;           // handle EOF (Ctrl+D/Ctrl+Z)
                String raw = sc.nextLine();              // echo exactly what was typed
                String cmd = raw.trim();                 // remove whitespaces from both ends

                if (cmd.equalsIgnoreCase("bye")) {
                    System.out.println(HR);
                    System.out.println(" ByeByeBye");
                    System.out.println(HR);
                    break;
                }

                System.out.println(HR);
                System.out.println(" " + raw);
                System.out.println(HR);
            }
        }
    }
}
