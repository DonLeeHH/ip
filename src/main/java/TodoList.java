import java.util.ArrayList;

public class TodoList {
    protected ArrayList<Task> todoList;
    public TodoList() {
        todoList = new ArrayList<>();
    }

    public void add(Task task) {
        todoList.add(task);
    }

    public void markDone(int id) throws NotFoundException {
        // Convert to 0 based index
        int i = id - 1;

        if (i < 0 || i >= todoList.size()) {
            throw new NotFoundException();
        }
        Task t = todoList.get(i);
        System.out.println(Sid.HR);
        t.markTask();
        System.out.println(" YAY! You've completed this task:");
        System.out.println("   " + t);
        System.out.println(Sid.HR);
    }

    public void unmarkDone(int id) throws NotFoundException {
        // Convert to 0 based index
        int i = id - 1;

        if (i < 0 || i >= todoList.size()) {
            throw new NotFoundException();
        }
        Task t = todoList.get(i);
        System.out.println(Sid.HR);
        t.unmarkTask();
        System.out.println(" OK, I've marked this task as not done yet:");
        System.out.println("   " + t);
        System.out.println(Sid.HR);
    }

    public int size() {
        return todoList.size();
    }

    @Override
    public String toString() {
        String output = "";
        output += Sid.HR + "\n" + "Here are your tasks: \n";
        for (int i = 0; i < todoList.size(); i++) {
            output += (i+1) + ". " +  todoList.get(i) + "\n";
        }
        output += Sid.HR;
        return output;
    }
}
