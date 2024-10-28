import managers.backed.FileBackedTaskManager;
import managers.history.HistoryManager;
import managers.Managers;
import managers.TaskManager;
import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskStatus;

import java.io.File;

public class Main {
    public static void main(String[] args) {
        TaskManager taskManager = Managers.getDefault();
        FileBackedTaskManager backedTaskManager = new FileBackedTaskManager(System.getProperty("user.dir") + "/src/" + "fileHistory.csv", Managers.getDefaultHistory());

        backedTaskManager.addTask(new Task("First", "One"));
        backedTaskManager.addTask(new Task("Twice", "Two"));
        backedTaskManager.addTask(new Epic("Two", "Two epic"));
        backedTaskManager.addTask(new Subtask(3, "SubOne", "Hello"));
        backedTaskManager.addTask(new Subtask(3, "SubTwo", "Hello"));
        backedTaskManager.updateTask(new Task(1,"First","One", TaskStatus.DONE));
        backedTaskManager.updateTask(new Subtask(4, "SubOne","Hello",TaskStatus.IN_PROGRESS, 3));
        backedTaskManager.updateTask(new Subtask(5, "SubOne","Hello",TaskStatus.DONE, 3));
//        backedTaskManager.removeTaskById(4);
        backedTaskManager.removeTaskById(5);

        System.out.println(backedTaskManager.getTasks());
        System.out.println(backedTaskManager.getSubtasks());
        System.out.println(backedTaskManager.getEpics());
        System.out.println(backedTaskManager.historyManager.getHistory());
        System.out.println("///////////////////////");

        FileBackedTaskManager newBack = FileBackedTaskManager.loadFromFile(new File(System.getProperty("user.dir") + "/src/" + "fileHistory.csv"), Managers.getDefaultHistory());
        System.out.println(newBack.historyManager.getHistory());
        System.out.println(newBack.getTasks());
        System.out.println(newBack.getSubtasks());
        System.out.println(newBack.getEpics());

    }
}

