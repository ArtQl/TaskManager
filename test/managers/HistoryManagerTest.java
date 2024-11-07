package managers;

import managers.history.HistoryManager;
import managers.history.InMemoryHistoryManager;
import model.Epic;
import model.Subtask;
import model.Task;
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
        task = new Task("Title", "Desc", TaskStatus.NEW, 1);
        task2 = new Task("Title", "Desc", TaskStatus.NEW, 2);
        epic = new Epic("Title", "Desc", TaskStatus.NEW, 3);
        subtask = new Subtask("Title", "Desc", TaskStatus.NEW, 4, 3);
        subtask2 = new Subtask("Title", "Desc", TaskStatus.NEW, 5, 3);
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
        historyManager.add(epic);
        historyManager.removeFirst();
        historyManager.removeFirst();
        historyManager.removeFirst();
        assertThrows(IllegalArgumentException.class, () -> historyManager.removeFirst());
        historyManager.add(task);
        historyManager.add(task2);
        historyManager.removeLast();
        historyManager.removeLast();
        assertThrows(IllegalArgumentException.class, () -> historyManager.removeLast());

    }
}
