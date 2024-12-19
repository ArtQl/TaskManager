package ru.artq.task.managers;

import ru.artq.task.managers.history.HistoryManager;
import ru.artq.task.model.Epic;
import ru.artq.task.model.Subtask;
import ru.artq.task.model.Task;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

public interface TaskManager {
    void addTask(Task task);

    void updateTask(Task task);

    void removeAllTasks();

    Task removeTaskById(int id);

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

    TreeSet<Task> getPrioritizedTasks();

    <T extends Task> Map<Integer, Task> getTasksByType(Class<T> type);

    void checkSubtasks();
}
