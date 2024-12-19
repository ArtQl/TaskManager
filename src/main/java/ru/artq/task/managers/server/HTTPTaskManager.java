package ru.artq.task.managers.server;

import ru.artq.task.managers.StorageManager;
import ru.artq.task.managers.backed.FileBackedTaskManager;
import ru.artq.task.model.Epic;
import ru.artq.task.model.Subtask;
import ru.artq.task.model.Task;
import ru.artq.task.utility.TaskParser;

import java.util.List;

public class HTTPTaskManager extends FileBackedTaskManager {
    private final KVTaskClient kvTaskClient;

    public HTTPTaskManager(StorageManager fileStorageManager, String uri) {
        super(fileStorageManager);
        kvTaskClient = new KVTaskClient(uri);
    }

    public KVTaskClient getKvTaskClient() {
        return kvTaskClient;
    }

    @Override
    public Task removeTaskById(int id) {
        Task task = super.removeTaskById(id);
        save();
        kvTaskClient.save("History", TaskParser.parseTasksToJson(historyManager.getHistory()));
        return task;
    }

    @Override
    public void removeEpics() {
        super.removeEpics();
        kvTaskClient.remove(Epic.class.getSimpleName());
        kvTaskClient.remove(Subtask.class.getSimpleName());
        kvTaskClient.save("AllTasks", TaskParser.parseTasksToJson(tasks));
        kvTaskClient.save("History", TaskParser.parseTasksToJson(historyManager.getHistory()));
    }

    @Override
    public void removeSubtasks() {
        super.removeSubtasks();
        kvTaskClient.remove(Subtask.class.getSimpleName());
        kvTaskClient.save(Epic.class.getSimpleName(), TaskParser.parseTasksToJson(getTasksByType(Epic.class)));
        kvTaskClient.save("AllTasks", TaskParser.parseTasksToJson(tasks));
        kvTaskClient.save("History", TaskParser.parseTasksToJson(historyManager.getHistory()));
    }

    @Override
    public void removeTasks() {
        super.removeTasks();
        kvTaskClient.remove(Task.class.getSimpleName());
        kvTaskClient.save("AllTasks", TaskParser.parseTasksToJson(tasks));
        kvTaskClient.save("History", TaskParser.parseTasksToJson(historyManager.getHistory()));
    }

    @Override
    public void removeAllTasks() {
        super.removeAllTasks();
        kvTaskClient.remove("History");
        kvTaskClient.remove("AllTasks");
        kvTaskClient.remove(Epic.class.getSimpleName());
        kvTaskClient.remove(Task.class.getSimpleName());
        kvTaskClient.remove(Subtask.class.getSimpleName());
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public List<Task> getTasks() {
        List<Task> map = super.getTasks();
        kvTaskClient.save("History", TaskParser.parseTasksToJson(historyManager.getHistory()));
        return map;
    }

    @Override
    public List<Epic> getEpics() {
        List<Epic> map = super.getEpics();
        kvTaskClient.save("History", TaskParser.parseTasksToJson(historyManager.getHistory()));
        return map;
    }

    @Override
    public List<Subtask> getSubtasks() {
        List<Subtask> map = super.getSubtasks();
        kvTaskClient.save("History", TaskParser.parseTasksToJson(historyManager.getHistory()));
        return map;
    }

    @Override
    public void addTask(Task task) {
        super.addTask(task);
        save();
    }

    private void save() {
        kvTaskClient.save("Epic", TaskParser.parseTasksToJson(getTasksByType(Epic.class)));
        kvTaskClient.save("Subtask", TaskParser.parseTasksToJson(getTasksByType(Subtask.class)));
        kvTaskClient.save("Task", TaskParser.parseTasksToJson(getTasksByType(Task.class)));
        kvTaskClient.save("AllTasks", TaskParser.parseTasksToJson(tasks));
    }
}