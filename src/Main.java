import managers.FileBackedTaskManager;
import managers.HistoryManager;
import managers.Managers;
import managers.TaskManager;
import model.Epic;
import model.Subtask;
import model.Task;

import java.io.File;

public class Main {
    public static void main(String[] args) {
        TaskManager taskManager = Managers.getDefault();
        HistoryManager historyManager = Managers.getDefaultHistory();
        FileBackedTaskManager backedTaskManager = new FileBackedTaskManager(System.getProperty("user.dir") + "/src/" + "fileHistory.csv");

        backedTaskManager.addTask(new Task("First", "One"));
        backedTaskManager.addTask(new Task("Twice", "Two"));
        backedTaskManager.addTask(new Epic("Two", "Two epic"));
        backedTaskManager.addSubtask(new Subtask("SubOne", "Hello"), "Two");
        backedTaskManager.addSubtask(new Subtask("SubTwo", "Hello"), "Two");

        backedTaskManager.getTasks();
        backedTaskManager.getSubtasks();
        backedTaskManager.getEpics();
        System.out.println(backedTaskManager.historyManager.getHistory());

        FileBackedTaskManager newBack = FileBackedTaskManager.loadFromFile(new File(System.getProperty("user.dir") + "/src/" + "fileHistory.csv"));
        System.out.println(newBack.historyManager.getHistory());
        System.out.println(newBack.getEpics());
        System.out.println(newBack.getTasks());
        System.out.println(newBack.getSubtasks());

    }
}

