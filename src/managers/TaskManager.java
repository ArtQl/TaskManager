package managers;

import model.Epic;
import model.Subtask;
import model.Task;

import java.util.HashMap;

public interface TaskManager {
    void addTask(Task task);

    void updateTask(int id, Task task);

    void deleteAllTasks();

    void deleteTaskById(int id);

    Task getTaskById(int id);

    void updateStatus();

    void addSubtask(Subtask subtask, String titleEpic);

    HashMap<Integer, Subtask> getSubtasks();

    HashMap<Integer, Task> getTasks();

    HashMap<Integer, Epic> getEpics();

    HashMap<Integer, Subtask> getSubtasksOfEpic(int id);

    Epic getEpicByTitle(String title);

}
