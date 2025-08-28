package sid.stubs;

import sid.exceptions.SidException;
import sid.models.ToDo;
import sid.models.TodoList;
import sid.storage.Storage;

import java.util.ArrayList;
import java.util.List;

/**
 * Test double for Storage that avoids disk I/O and records save() calls.
 */
public class StorageStub extends Storage {
    public int saveCalls = 0;
    public final List<String> snapshots = new ArrayList<>();

    public StorageStub() {
        // Path is unused; superclass requires a constructor arg.
        super("build/test-tmp/unused.txt");
    }

    @Override
    public TodoList load() {
        // Not needed for these tests; return an empty list bound to this stub.
        return new TodoList(new ArrayList<ToDo>(), this);
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
