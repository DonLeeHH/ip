package sid.models;
import java.util.ArrayList;
import java.util.List;

import sid.exceptions.SidException;
import sid.storage.Storage;

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
     * Constructs a task list initalized with the given tasks without a bounded storage.
     *
     * @param initialList Initial tasks to populate the list with.
     */
    public TodoList(List<ToDo> initialList) {
        this.todoList = new ArrayList<>(initialList);
        this.storage = null;
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

    /**
     * Adds the task to the end of the current list and saves it to the storage.
     *
     * @param task - the task to be added
     */
    public void add(ToDo task) {
        todoList.add(task);
        storage.save(this);
    }

    /**
     * Deletes the task at the given one-based index
     *
     * @param id 1-based task number as displayed to the user.
     * @throws SidException If {@code id} is out of range
     */
    public void delete(int id) throws SidException {
        int i = id - 1;
        if (i < 0 || i >= this.getSize()) {
            throw new SidException("Not a valid task number!");
        }
        ToDo deletedTask = this.todoList.remove(i);
        storage.save(this);
    }

    /**
     * Returns the task at the given one-based index
     *
     * @param id 1-based task number as displayed to the user.
     * @return The task at the specified position.
     * @throws SidException If {@code id} is out of range.
     */
    public ToDo getTodo(int id) throws SidException {
        // For one-based indexing
        id -= 1;
        if (id < 0 || id >= this.getSize()) {
            throw new SidException("Not a valid task number!");
        } else {
            return this.todoList.get(id);
        }
    }

    /**
     * Returns the found tasks matching the given keyword
     *
     * @param keyword The keyword to search for
     * @return A TodoList of the tasks that have matched the keyword
     */
    public TodoList findTodos(String keyword) {
        List<ToDo> results = new ArrayList<>();
        if (keyword == null || keyword.trim().isEmpty()) {
            return new TodoList(results); // empty
        }

        String query = keyword.trim().toLowerCase();
        for (ToDo t : this.todoList) {
            // Guard clause: skip if neither description nor toString contains query
            if (t.getDescription() == null && t.toString() == null) {
                continue;
            }

            boolean descriptionMatches = t.getDescription() != null
                && t.getDescription().toLowerCase().contains(query);
            boolean toStringMatches = t.toString() != null
                && t.toString().toLowerCase().contains(query);
            if (descriptionMatches || toStringMatches) {
                results.add(t);
            }
        }
        return new TodoList(results);
    }

    public boolean isEmpty() {
        return this.todoList.isEmpty();
    }

    @Override
    public String toString() {
        StringBuilder output = new StringBuilder();
        for (int i = 0; i < this.todoList.size(); i++) {
            output.append((i + 1)).append(". ").append(this.todoList.get(i));
            if (i < this.todoList.size() - 1) {
                output.append("\n");
            }
        }
        return output.toString();
    }
}
