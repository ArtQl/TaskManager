package managers;

import managers.backed.FileBackedTaskManager;
import managers.history.HistoryManager;
import managers.history.InMemoryHistoryManager;
import managers.memory.InMemoryTaskManager;

import java.io.File;

public class Managers {
    public static TaskManager getDefault() {
        return FileBackedTaskManager.loadFromFile(new File(System.getProperty("user.dir") + "/src/" + "fileHistory.csv"), getDefaultHistory());
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
