package ru.artq.task.managers;

import ru.artq.task.managers.backed.ManagerSaveException;
import ru.artq.task.model.Task;

import java.util.List;
import java.util.Map;

public interface StorageManager {
    void save(TaskManager taskManager) throws ManagerSaveException;

    Map<Integer, Task> load();

    List<Task> loadHistory();
}
