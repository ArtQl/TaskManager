package managers.history;

import model.Task;

import java.util.List;
import java.util.Map;

public interface HistoryManager {

    void add(Task task);

    void remove(int id);

    void removeFirst();

    void removeLast();

    List<Task> getHistory();

}
