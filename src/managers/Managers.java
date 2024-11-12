package managers;

import managers.backed.FileBackedTaskManager;
import managers.history.HistoryManager;
import managers.history.InMemoryHistoryManager;
import server.HTTPTaskManager;

import java.io.File;
import java.net.URI;

public class Managers {

    private static final File fileStorage = new File(System.getProperty("user.dir") + "/src/" + "fileHistory.csv");
    private static final FileStorageManager storageManager = getDefaultStorageManager();

    public static TaskManager getDefault() {
//        return loadFromFile();
        return new HTTPTaskManager(storageManager, URI.create("http://localhost:8080/register"));
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

    public static FileStorageManager getDefaultStorageManager() {
        return new FileStorageManager(fileStorage);
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
