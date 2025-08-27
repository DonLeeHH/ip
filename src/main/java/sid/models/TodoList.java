package sid.models;

import sid.exceptions.SidException;
import sid.storage.Storage;

import java.util.ArrayList;
import java.util.List;

/**
 * Holds an in-memory list of tasks and provides user-facing operations.
 *
 * <p>All mutating operations ({@code add}, {@code delete}, {@code markDone}, {@code unmarkDone})
 * automatically persist the updated list via the injected {@link Storage}
 *
 * <p>Unless stated otherwise, user-facing task indices are 1-based (as shown in {@link #toString()}).
 */
public class TodoList {
    private final ArrayList<ToDo> todoList;
    private final Storage storage;

    /**
     * Constructs a task list initialized with the given tasks and bound storage.
     *
     * @param initialList Initial tasks to populate the list with.
     * @param storage     Storage used to persist changes after mutations.
     */
    public TodoList(List<ToDo> initialList, Storage storage) {
        this.todoList = new ArrayList<>(initialList);
        this.storage = storage;
    }

    /**
     * Marks the specified task as done (1-based index), saves, and prints a confirmation.
     *
     * @param id 1-based task number as displayed to the user.
     * @return The task that was marked as done.
     * @throws SidException If {@code id} is out of range.
     */
    public ToDo markDone(int id) throws SidException {
        // Convert to 0 based index
        int i = id - 1;

        if (i < 0 || i >= this.getSize()) {
            throw new SidException("Not a valid task number!");
        }
        ToDo t = todoList.get(i);
        t.markTask();
        storage.save(this);
        return t;
    }

    /**
     * Marks the specified task as not done yet (1-based index), saves, and prints a confirmation.
     *
     * @param id 1-based task number as displayed to the user.
     * @return The task that was unmarked.
     * @throws SidException If {@code id} is out of range.
     */
    public ToDo unmarkDone(int id) throws SidException {
        // Convert to 0 based index
        int i = id - 1;

        if (i < 0 || i >= this.getSize()) {
            throw new SidException("Not a valid task number!");
        }
        ToDo t = this.todoList.get(i);
        t.unmarkTask();
        storage.save(this);
        return t;
    }

    public int getSize() {
        return this.todoList.size();
    }

    public void add(ToDo task) {
        todoList.add(task);
        storage.save(this);
    }

    public void delete(int id) throws SidException {
        int i = id - 1;
        if (i < 0 || i >= this.getSize()) {
            throw new SidException("Not a valid task number!");
        }
        ToDo deletedTask = this.todoList.remove(i);
        storage.save(this);
    }

    /**
     * Returns the task at the given zero-based index.
     *
     * @param id One-based position in the internal list.
     * @return The task at the specified position.
     * @throws SidException If {@code id} is out of range.
     */
    public ToDo getTodo(int id) throws SidException {
        // For one-based indexing
        id -= 1;
        if  (id < 0 || id >= this.getSize()) {
            throw new SidException("Not a valid task number!");
        } else {
            return this.todoList.get(id);
        }
    }

    @Override
    public String toString() {
        StringBuilder output = new StringBuilder("Here are your tasks:\n");
        for (int i = 0; i < this.todoList.size(); i++) {
            output.append((i + 1)).append(". ").append(this.todoList.get(i));
            if (i < this.todoList.size() - 1) {
                output.append("\n");
            }
        }
        return output.toString();
    }
}
