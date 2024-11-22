package managers;

import managers.backed.ManagerSaveException;
import model.Task;

import java.util.List;
import java.util.Map;

public interface StorageManager {
    void save(TaskManager taskManager) throws ManagerSaveException;

    Map<Integer, Task> load();

    List<Task> loadHistory();
}
