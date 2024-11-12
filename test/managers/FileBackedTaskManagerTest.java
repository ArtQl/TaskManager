package managers;

import managers.backed.FileBackedTaskManager;
import model.Subtask;
import model.TaskStatus;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;


public class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {
    @Override
    protected FileBackedTaskManager createTaskManager() {
        return new FileBackedTaskManager(Managers.getDefaultStorageManager());
    }

    @Test
    void shouldSaveDataToFile() {
        assertDoesNotThrow(() -> taskManager.addTask(task));
        assertDoesNotThrow(() -> taskManager.addTask(epic));
        assertDoesNotThrow(() -> taskManager.addTask(new Subtask("title", "desc", TaskStatus.NEW, 2)));
        assertTrue(taskManager.getStorageManager().load().containsValue(task));
        taskManager.removeTaskById(task.getId());
        assertEquals(2, taskManager.getMapTasks().size());
        taskManager.getEpics();
        taskManager.getSubtasks();
        assertEquals(2, taskManager.getHistoryManager().getHistory().size());

        TaskManager newTaskManager = Managers.loadFromFile();
        assertEquals(taskManager.getMapTasks(), newTaskManager.getMapTasks(), "Task the same");
        assertEquals(taskManager.getHistoryManager().getHistory(), newTaskManager.getHistoryManager().getHistory(), "History the same");
    }

    @Test
    void getTime() {
        taskManager.addTask(task);
        taskManager.updateTimeTask(task,
                LocalDateTime.of(2024, 1, 1, 10,10, 0),
                Duration.ofHours(5));

        taskManager.addTask(epic);
        taskManager.addTask(new Subtask("SubOne", "Hello", 2));
        taskManager.updateTimeTask(taskManager.getTaskById(3),
                LocalDateTime.of(2024, 1, 15, 15,10, 0),
                Duration.ofDays(10).plusMinutes(20));

        taskManager.addTask(new Subtask("SubTwo", "Hello", 2));
        taskManager.updateTimeTask(taskManager.getTaskById(4),
                LocalDateTime.of(2024, 1, 30, 18,10, 0),
                Duration.ofDays(15).plusHours(3).plusMinutes(30));

        taskManager.getSubtasks();
        taskManager.getEpics();
        taskManager.getTasks();
    }

    @Test
    void updateAndRemoveTask() {
        taskManager.addTask(task);
        taskManager.addTask(epic);
        taskManager.addTask(new Subtask("SubOne", "Hello", 2));
        taskManager.addTask(new Subtask("SubTwo", "Hello", 2));

        taskManager.updateTask(new Subtask("SubOne", "Hello", TaskStatus.DONE, 3, 2));
        taskManager.updateTask(new Subtask("SubOne", "Hello", TaskStatus.DONE, 4, 2));
        taskManager.removeEpics();
        assertEquals(1, taskManager.getStorageManager().load().size());

        taskManager.addTask(epic);
        taskManager.addTask(new Subtask("SubOne", "Hello", 2));
        taskManager.addTask(new Subtask("SubTwo", "Hello", 2));
        taskManager.removeTasks();
        assertEquals(3, taskManager.getStorageManager().load().size());

        taskManager.addTask(task);
        taskManager.removeSubtasks();
        assertEquals(2, taskManager.getStorageManager().load().size());
    }
}
