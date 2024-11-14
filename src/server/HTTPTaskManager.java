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
    private final KVTaskClient httpClient;


    public HTTPTaskManager(StorageManager fileStorageManager, String uri) {
        super(fileStorageManager);
        this.httpClient = new KVTaskClient(uri);
    }

    public KVTaskClient getHttpClient() {
        return httpClient;
    }

    @Override
    public List<Task> getTasks() {
        httpClient.load("tasks");
//        HttpRequest httpRequest = HttpRequest.newBuilder().GET()
//                .uri(URI.create(HOST + "/tasks" + apiToken)).build();
//        return httpClient.sendRequestForResponse(httpRequest, "Ошибка при загрузке данных");
        return super.getTasks();
    }

    @Override
    public void removeTaskById(int id) {
        super.removeTaskById(id);
    }

    @Override
    public void removeEpics() {
        super.removeEpics();
    }

    @Override
    public void removeSubtasks() {
        super.removeSubtasks();
    }

    @Override
    public void removeTasks() {
        super.removeTasks();
    }

    @Override
    public void removeAllTasks() {
        super.removeAllTasks();
    }

    @Override
    public void updateTimeTask(Task task, LocalDateTime localDateTime, Duration duration) {
        super.updateTimeTask(task, localDateTime, duration);
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
    }

    @Override
    public List<Epic> getEpics() {
        return super.getEpics();
    }

    @Override
    public List<Subtask> getSubtasks() {
        return super.getSubtasks();
    }

    @Override
    public void addTask(Task task) {
        super.addTask(task);
    }
}
