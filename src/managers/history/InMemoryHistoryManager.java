package managers.history;

import model.Task;

import java.util.List;
import java.util.Objects;

public class InMemoryHistoryManager implements HistoryManager {
    private final HistoryLinkedList<Task> historyTasks;

    public InMemoryHistoryManager() {
        this.historyTasks = new HistoryLinkedList<>();
    }

    @Override
    public HistoryLinkedList<Task> getHistoryTasks() {
        return historyTasks;
    }

    @Override
    public String toString() {
        return "InMemoryHistoryManager{historyTasks=" + historyTasks + '}';
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InMemoryHistoryManager that = (InMemoryHistoryManager) o;
        return Objects.equals(historyTasks, that.historyTasks);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(historyTasks);
    }
}
