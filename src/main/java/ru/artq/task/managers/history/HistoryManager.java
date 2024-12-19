package ru.artq.task.managers.history;

import ru.artq.task.model.Task;

import java.util.List;

public interface HistoryManager {

    void add(Task task);

    void remove(int id);

    void removeFirst();

    void removeLast();

    List<Task> getHistory();

}
