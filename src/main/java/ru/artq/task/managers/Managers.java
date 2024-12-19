package ru.artq.task.managers;

import ru.artq.task.managers.backed.FileBackedTaskManager;
import ru.artq.task.managers.backed.FileStorageManager;
import ru.artq.task.managers.history.HistoryManager;
import ru.artq.task.managers.history.InMemoryHistoryManager;
import ru.artq.task.managers.server.HTTPTaskManager;
import ru.artq.task.utility.TaskParser;

import java.io.File;

public class Managers {

    private static final File fileStorage = new File("src/main/resources/fileHistory.csv");
    private static final String uri = "http://localhost:8080";
    private static final StorageManager storageManager = getDefaultStorageManager();

    public static TaskManager getDefault() {
//        return loadFromFile();
        return loadFromServer();
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

    public static StorageManager getDefaultStorageManager() {
        return new FileStorageManager(fileStorage);
    }

    public static TaskManager loadFromServer() {
        HTTPTaskManager httpTaskManager = new HTTPTaskManager(storageManager, uri);
        httpTaskManager.getMapTasks().putAll(TaskParser.parseJsonToTasks(httpTaskManager.getKvTaskClient().load("AllTasks")));
        TaskParser.parseJsonToTasks(httpTaskManager.getKvTaskClient().load("History"))
                .values().forEach(httpTaskManager.getHistoryManager()::add);
        httpTaskManager.setId();
        return httpTaskManager;
    }

    public static FileBackedTaskManager loadFromFile() {
        FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(storageManager);
        if (storageManager.load() != null)
            fileBackedTaskManager.getMapTasks().putAll(storageManager.load());
        if (storageManager.loadHistory() != null)
            storageManager.loadHistory().forEach(fileBackedTaskManager.getHistoryManager()::add);
        fileBackedTaskManager.setId();
        return fileBackedTaskManager;
    }
}
