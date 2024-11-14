package managers;

import managers.backed.FileBackedTaskManager;
import managers.history.HistoryManager;
import managers.history.InMemoryHistoryManager;
import server.HTTPTaskManager;

import java.io.File;

public class Managers {

    private static final File fileStorage = new File(System.getProperty("user.dir") + "/src/" + "fileHistory.csv");
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
//        return new ServerStorageManager(uri);
        return new FileStorageManager(fileStorage);
    }

    public static HTTPTaskManager loadFromServer() {
        HTTPTaskManager httpTaskManager = new HTTPTaskManager(storageManager, uri);
        if (storageManager.load() != null)
            httpTaskManager.getMapTasks().putAll(storageManager.load());
        if (storageManager.loadHistory() != null)
            storageManager.loadHistory().forEach(httpTaskManager.getHistoryManager()::add);
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
