import managers.Managers;
import managers.TaskManager;
import managers.backed.FileBackedTaskManager;
import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskStatus;

public class Main {
    public static void main(String[] args) {
        TaskManager taskManager = Managers.getDefault();
        FileBackedTaskManager backedTaskManager = new FileBackedTaskManager(System.getProperty("user.dir") + "/src/" + "fileHistory.csv", Managers.getDefaultHistory());

        backedTaskManager.addTask(new Task("First", "One"));
        backedTaskManager.addTask(new Task("Twice", "Two"));
        backedTaskManager.addTask(new Epic("Two", "Two epic"));
        backedTaskManager.addTask(new Subtask("SubOne", "Hello", 3));
        backedTaskManager.addTask(new Subtask("SubTwo", "Hello", 3));
        backedTaskManager.updateTask(new Task("First", "One", TaskStatus.DONE, 1));
        backedTaskManager.updateTask(new Subtask("SubOne", "Hello", TaskStatus.NEW, 4, 3));
        backedTaskManager.updateTask(new Subtask("SubOne", "Hello", TaskStatus.DONE, 5, 3));
//        backedTaskManager.removeTaskById(4);
//        backedTaskManager.removeTaskById(5);

        System.out.println(backedTaskManager.getTasks());
        System.out.println(backedTaskManager.getSubtasks());
        System.out.println(backedTaskManager.getEpics());

        System.out.println("///////////////////////");

//        FileBackedTaskManager newBack = FileBackedTaskManager.loadFromFile(new File(System.getProperty("user.dir") + "/src/" + "fileHistory.csv"), Managers.getDefaultHistory());
//        System.out.println(newBack.historyManager.getHistory());
//        System.out.println(newBack.getTasks());
//        System.out.println(newBack.getSubtasks());
//        System.out.println(newBack.getEpics());

    }
}

