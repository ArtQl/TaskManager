package controller;

import controller.inmemory.InMemoryHistoryManager;
import controller.inmemory.InMemoryTaskManager;
import controller.managers.HistoryManager;
import controller.managers.TaskManager;

public class Managers {
    public static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
