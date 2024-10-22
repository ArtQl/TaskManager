import controller.Managers;
import controller.inmemory.InMemoryHistoryManager;
import controller.managers.TaskManager;
import model.Epic;
import model.Subtask;
import model.Task;

public class Main {
    public static void main(String[] args) {
        TaskManager taskManager = Managers.getDefault();

        taskManager.addTask(new Task("First", "One"));
        taskManager.addTask(new Task("Twice", "Two"));
        taskManager.addTask(new Epic("Two", "Two epic"));
        taskManager.addSubtask(new Subtask("SubOne", "Hello"), "Two");
        taskManager.addSubtask(new Subtask("SubTwo", "Hello"), "Two");
        taskManager.addTask(new Epic("Three", "Three epic"));
        taskManager.addSubtask(new Subtask("SubThree", "Hello"), "Two");

        taskManager.getEpics();
        taskManager.getTasks();
        taskManager.getSubtasks();
        taskManager.getTasks();
        System.out.println(Managers.getDefaultHistory().getHistory());
        InMemoryHistoryManager.historyTasks.remove(3);

        System.out.println(Managers.getDefaultHistory().getHistory());
    }
}