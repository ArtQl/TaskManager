package managers;

import managers.history.HistoryManager;
import model.Subtask;
import model.Task;
import model.Epic;
import managers.history.InMemoryHistoryManager;
import model.TaskStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class HistoryManagerTest {
    HistoryManager historyManager;
    Task task;
    Task task2;
    Epic epic;
    Subtask subtask;
    Subtask subtask2;

    @BeforeEach
    void beforeEach() {
        historyManager = new InMemoryHistoryManager();
        task = new Task(1, "Title", "Desc", TaskStatus.NEW);
        task2 = new Task(2, "Title", "Desc", TaskStatus.NEW);
        epic = new Epic(3,"Title", "Desc", TaskStatus.NEW);
        subtask = new Subtask(4,"Title", "Desc", TaskStatus.NEW, 3);
        subtask2 = new Subtask(5,"Title", "Desc", TaskStatus.NEW, 3);
    }

    @Test
    void emptyHistoryManager() {
        assertTrue(historyManager.getHistory().isEmpty(), "empty history");

        historyManager.add(task);
        historyManager.add(task2);
        historyManager.add(epic);
        historyManager.add(subtask);
        historyManager.add(subtask2);
        assertEquals(5, historyManager.getHistory().size());

        historyManager.add(epic);
        assertEquals(3, historyManager.getHistory().size(), "remove subtasks");
        assertEquals(task, historyManager.getHistory().getFirst());
        assertEquals(epic, historyManager.getHistory().getLast());
    }

    @Test
    void removeFirstLast() {
        historyManager.add(task);
        historyManager.add(task2);
        historyManager.getHistoryTasks().removeFirst();
        historyManager.getHistoryTasks().removeFirst();
        assertThrows(IllegalArgumentException.class, () -> historyManager.getHistoryTasks().removeFirst());
        historyManager.add(task);
        historyManager.add(task2);
        historyManager.getHistoryTasks().removeLast();
        historyManager.getHistoryTasks().removeLast();
        assertThrows(IllegalArgumentException.class, () -> historyManager.getHistoryTasks().removeLast());

    }
}
