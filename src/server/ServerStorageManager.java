package server;

import managers.StorageManager;
import managers.TaskManager;
import managers.backed.ManagerSaveException;
import model.Task;

import java.util.List;
import java.util.Map;

public class ServerStorageManager implements StorageManager {
    private final KVTaskClient httpClient;

    public ServerStorageManager(String uri) {
        this.httpClient = new KVTaskClient(uri);
    }

    @Override
    public void save(TaskManager taskManager) throws ManagerSaveException {

    }

    @Override
    public Map<Integer, Task> load() {
        return Map.of();
    }

    @Override
    public List<Task> loadHistory() {
        return List.of();
    }
}
