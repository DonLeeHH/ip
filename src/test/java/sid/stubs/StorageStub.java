package sid.stubs;

import java.util.ArrayList;
import java.util.List;

import sid.exceptions.SidException;
import sid.models.ToDo;
import sid.models.TodoList;
import sid.storage.Storage;

/**
 * Test double for Storage that avoids disk I/O and records save() calls.
 */
public class StorageStub extends Storage {
    public final List<String> snapshots = new ArrayList<>();
    private int saveCalls = 0;

    /**
     * Constructs a StorageStub for testing purposes.
     */
    public StorageStub() {
        // Path is unused; superclass requires a constructor arg.
        super("build/test-tmp/unused.txt");
    }

    @Override
    public TodoList load() {
        // Not needed for these tests; return an empty list bound to this stub.
        return new TodoList(new ArrayList<ToDo>(), this);
    }

    /**
     * Returns the number of times save() has been called.
     * @return number of save calls
     */
    public int getSaveCalls() {
        return saveCalls;
    }

    @Override
    public void save(TodoList list) {
        saveCalls++;
        // Capture a lightweight snapshot: size and string forms of tasks (1-based).
        StringBuilder sb = new StringBuilder("size=").append(list.getSize());
        for (int i = 1; i <= list.getSize(); i++) {
            try {
                sb.append("|").append(list.getTodo(i).toString());
            } catch (SidException ignored) {
                // Should not happen if TodoList invariants are correct.
            }
        }
        snapshots.add(sb.toString());
    }
}
