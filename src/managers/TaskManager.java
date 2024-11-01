package managers;

import model.Epic;
import model.Subtask;
import model.Task;

import java.util.HashMap;

public interface TaskManager {
    void addTask(Task task);

    void updateTask(Task task);

    void removeAllTasks();

    void removeTaskById(int id);

    Task getTaskById(int id);

    HashMap<Integer, Subtask> getSubtasks();

    HashMap<Integer, Task> getTasks();

    HashMap<Integer, Epic> getEpics();

    HashMap<Integer, Subtask> getSubtasksOfEpic(int id);

}
