package managers;

import managers.history.HistoryManager;
import model.Epic;
import model.Subtask;
import model.Task;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface TaskManager {
    void addTask(Task task);

    void updateTask(Task task);

    void removeAllTasks();

    void removeTaskById(int id);

    Task getTaskById(int id);

    List<Subtask> getSubtasks();

    List<Task> getTasks();

    List<Epic> getEpics();

    HashMap<Integer, Subtask> getSubtasksOfEpic(int id);

    void removeTasks();
    void removeSubtasks();
    void removeEpics();

    Map<Integer, Task> getMapTasks();

    HistoryManager getHistoryManager();

}
