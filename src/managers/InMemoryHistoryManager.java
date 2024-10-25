package managers;

import model.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private final HistoryLinkedList<Task> historyTasks;

    public InMemoryHistoryManager() {
        this.historyTasks = new HistoryLinkedList<>();
    }

    public static String toString(HistoryManager manager) {
        StringBuilder str = new StringBuilder();
        for (Task task : manager.getHistory()) {
            str.append(task.getId()).append(",");
        }
        return str.toString();
    }

    public static List<Integer> fromString(String value) {
        List<Integer> list = new ArrayList<>();
        for (String s : value.split(",")) {
            list.add(Integer.parseInt(s));
        }
        return list;
    }

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
