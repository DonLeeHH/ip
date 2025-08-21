import java.util.ArrayList;

public class TodoList {
    private ArrayList<ToDo> todoList;
    public TodoList() {
        todoList = new ArrayList<>();
    }

    public void add(ToDo task) {
        todoList.add(task);
        Sid.SpecialPrint("Got it. I've added this task: \n  " + task + "\nNow you have " + todoList.size() + " tasks in the list");
    }

    public ToDo markDone(int id) throws NotFoundException {
        // Convert to 0 based index
        int i = id - 1;

        if (i < 0 || i >= todoList.size()) {
            throw new NotFoundException();
        }
        ToDo t = todoList.get(i);
        t.markTask();
        Sid.SpecialPrint(" YAY! You've completed this task:\n   " + t);
        return t;
    }

    public ToDo unmarkDone(int id) throws NotFoundException {
        // Convert to 0 based index
        int i = id - 1;

        if (i < 0 || i >= todoList.size()) {
            throw new NotFoundException();
        }
        ToDo t = todoList.get(i);
        t.unmarkTask();
        Sid.SpecialPrint(" OK, I've marked this task as not done yet:\n   " + t);
        return t;
    }

    public int size() {
        return todoList.size();
    }

    @Override
    public String toString() {
        String output = "Here are your tasks: \n";
        for (int i = 0; i < todoList.size(); i++) {
            output += (i+1) + ". " +  todoList.get(i);
            if (i < todoList.size() - 1) {
                output += "\n";
            }
        }
        return output;
    }
}
