package controller.inmemory;

import controller.managers.HistoryManager;
import model.Task;

import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    public static HistoryLinkedList<Task> historyTasks = new HistoryLinkedList<>();

    @Override
    public void add(Task task) {
        historyTasks.add(task);
    }

    @Override
    public void remove(int id) {
        historyTasks.remove(id);
    }

    @Override
    public List<Task> getHistory() {
        return historyTasks.getTasks();
    }
}
