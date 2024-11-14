package server;

import managers.FileStorageManager;
import managers.StorageManager;
import managers.backed.FileBackedTaskManager;
import model.Epic;
import model.Subtask;
import model.Task;

import java.io.File;
import java.net.URI;
import java.net.http.HttpRequest;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public class HTTPTaskManager extends FileBackedTaskManager {
    private final KVTaskClient kvTaskClient;


    public HTTPTaskManager(StorageManager fileStorageManager, String uri) {
        super(fileStorageManager);
        kvTaskClient = new KVTaskClient(uri);
        loadTasks();
    }

    private void loadTasks() {
        // TODO parse
        String tasksData = kvTaskClient.load("allTasks");
    }

    @Override
    public void removeTaskById(int id) {
        super.removeTaskById(id);
        kvTaskClient.remove(String.valueOf(id));
    }

    @Override
    public void removeEpics() {
        super.removeEpics();
        kvTaskClient.remove("Epics");
    }

    @Override
    public void removeSubtasks() {
        super.removeSubtasks();
        kvTaskClient.remove("Subtasks");
    }

    @Override
    public void removeTasks() {
        super.removeTasks();
        kvTaskClient.remove("Tasks");
    }

    @Override
    public void removeAllTasks() {
        super.removeAllTasks();
        kvTaskClient.remove("AllTasks");
    }

    @Override
    public void updateTimeTask(Task task, LocalDateTime localDateTime, Duration duration) {
        super.updateTimeTask(task, localDateTime, duration);
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        if (task instanceof Epic) kvTaskClient.save("Epics", task.toString());
        else if (task instanceof Subtask) kvTaskClient.save("Subtasks", task.toString());
        else kvTaskClient.save("Tasks", task.toString());
        kvTaskClient.save("AllTasks", tasks.toString());
        //todo
    }

    @Override
    public List<Task> getTasks() {
        List<Task> map = super.getTasks();
        kvTaskClient.save("History", "");
        return map;
        // TODO parse
    }

    @Override
    public List<Epic> getEpics() {
        List<Epic> map = super.getEpics();
        kvTaskClient.save("History", "");
        return map;
    }

    @Override
    public List<Subtask> getSubtasks() {
        List<Subtask> map = super.getSubtasks();
        kvTaskClient.save("History", "");
        return map;
    }

    @Override
    public void addTask(Task task) {
        super.addTask(task);
        if (task instanceof Epic) kvTaskClient.save("Epics", tasks.values().stream().filter(val -> val instanceof Epic).toList().toString());
        else if (task instanceof Subtask) kvTaskClient.save("Subtasks", task.toString());
        else kvTaskClient.save("Tasks", task.toString());
        kvTaskClient.save("AllTasks", tasks.toString());
    }
}
