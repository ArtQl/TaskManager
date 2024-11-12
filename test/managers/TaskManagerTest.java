package managers;

import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;


abstract public class TaskManagerTest<T extends TaskManager> {
    protected T taskManager;
    protected Task task;
    protected Epic epic;

    protected abstract T createTaskManager();

    @BeforeEach
    void beforeEach() {
        taskManager = createTaskManager();
        task = new Task("title", "desc");
        epic = new Epic("title", "desc");
    }

    @Test
    void shouldAddTask() {
        taskManager.addTask(task);
        assertFalse(taskManager.getTasks().isEmpty(), "Task not added");
        assertThrows(IllegalArgumentException.class, () -> taskManager.addTask(new Task("1", "1", TaskStatus.NEW, 1)), "Add Task With the same ID");
        assertThrows(IllegalArgumentException.class, () -> taskManager.addTask(task), "Add same task");
    }

    @Test
    void shouldAddEpic() {
        taskManager.addTask(epic);
        assertFalse(taskManager.getEpics().isEmpty());
        assertEquals(TaskStatus.NEW, taskManager.getTaskById(1).getStatus());
    }

    @Test
    void shouldAddEpicSubtask() {
        taskManager.addTask(epic);
        taskManager.addTask(new Subtask("1Sub", "d", 1));
        assertEquals(TaskStatus.NEW, taskManager.getTaskById(1).getStatus());
        assertEquals(TaskStatus.NEW, taskManager.getTaskById(2).getStatus());
    }

    @Test
    void shouldNotAddSubtaskWithNullIDEpic() {
        assertThrows(IllegalArgumentException.class, () -> taskManager.addTask(new Subtask("1Sub", "d", 2)));
    }

    @Test
    void shouldUpdateStatusTask() {
        taskManager.addTask(task);
        taskManager.updateTask(new Task("First", "1", TaskStatus.DONE, 1));
        assertEquals(TaskStatus.DONE, taskManager.getMapTasks().get(1).getStatus());
    }

    @Test
    void shouldUpdateStatusSubtask() {
        taskManager.addTask(epic);
        taskManager.addTask(new Subtask("Subtask", "d", 1));
        taskManager.updateTask(new Subtask("Subtask", "First", TaskStatus.DONE, 2, 1));
        assertEquals(TaskStatus.DONE, taskManager.getMapTasks().get(1).getStatus());
    }

    @Test
    void shouldNotUpdateStatusTaskWithZeroID() {
        taskManager.addTask(task);
        assertThrows(IllegalArgumentException.class, () ->
                taskManager.updateTask(new Task("1", "1")));

        assertThrows(IllegalArgumentException.class, () -> taskManager.updateTask(task), "Try update status with same task");
    }

    @Test
    void shouldNotUpdateTaskOnEpic() {
        taskManager.addTask(epic);
        taskManager.addTask(new Subtask("Subtask", "d", 1));
        assertThrows(IllegalArgumentException.class, () ->
                taskManager.updateTask(new Task("Task", "First", TaskStatus.DONE, 1)));
    }

    @Test
    void shouldUpdateSubtaskFromOtherEpic() {
        taskManager.addTask(epic);
        epic = (Epic) taskManager.getTaskById(1);
        taskManager.addTask(new Epic("2", "status"));
        taskManager.addTask(new Subtask("Subtask", "d", 1));
        taskManager.updateTask(new Subtask("Subtask", "d", TaskStatus.DONE, 3, 2));
        assertTrue(((Epic) taskManager.getTaskById(1)).getSubtaskList().isEmpty());
    }

    @Test
    void shouldRemoveAllTacksMap() {
        taskManager.addTask(task);
        taskManager.addTask(epic);
        taskManager.addTask(new Subtask("1", "1", 2));
        taskManager.removeAllTasks();
        assertTrue(taskManager.getMapTasks().isEmpty());
    }

    @Test
    void shouldRemoveAllTasks() {
        taskManager.addTask(task);
        taskManager.addTask(epic);
        taskManager.addTask(new Subtask("1", "1", 2));
        taskManager.removeTasks();
        assertTrue(taskManager.getTasks().isEmpty());
        assertFalse(taskManager.getEpics().isEmpty());
        assertEquals(2, taskManager.getHistoryManager().getHistory().getFirst().getId());
    }

    @Test
    void shouldRemoveAllEpics() {
        taskManager.addTask(task);
        taskManager.addTask(epic);
        taskManager.addTask(new Subtask("1", "1", 2));
        taskManager.removeEpics();
        assertFalse(taskManager.getTasks().isEmpty());
        assertTrue(taskManager.getEpics().isEmpty());
        assertTrue(taskManager.getSubtasks().isEmpty());
    }

    @Test
    void shouldRemoveAllSubtask() {
        taskManager.addTask(task);
        taskManager.addTask(epic);
        taskManager.addTask(new Subtask("1", "1", 2));
        taskManager.removeSubtasks();
        assertFalse(taskManager.getTasks().isEmpty());
        assertFalse(taskManager.getEpics().isEmpty());
        assertTrue(taskManager.getSubtasks().isEmpty());
    }

    @Test
    void shouldRemoveTaskById() {
        taskManager.addTask(task);
        taskManager.removeTaskById(1);
        assertThrows(RuntimeException.class, () -> taskManager.getTasks(), "tasks no empty");
        assertTrue(taskManager.getMapTasks().isEmpty());
        assertTrue(taskManager.getHistoryManager().getHistory().isEmpty());
    }

    @Test
    void shouldRemoveEpicById() {
        taskManager.addTask(task);
        taskManager.addTask(epic);
        taskManager.addTask(new Subtask("1", "1", 2));
        taskManager.getTasks();
        taskManager.getEpics();
        taskManager.getSubtasks();
        taskManager.removeTaskById(2);
        assertTrue(taskManager.getEpics().isEmpty());
        assertTrue(taskManager.getSubtasks().isEmpty());
        assertEquals(1, taskManager.getHistoryManager().getHistory().getFirst().getId());
    }

    @Test
    void shouldRemoveSubtaskById() {
        taskManager.addTask(task);
        taskManager.addTask(epic);
        taskManager.addTask(new Subtask("1", "1", 2));
        taskManager.getTasks();
        taskManager.getEpics();
        taskManager.getSubtasks();
        taskManager.removeTaskById(3);
        assertFalse(taskManager.getEpics().isEmpty());
        assertTrue(taskManager.getSubtasks().isEmpty());
        assertEquals(1, taskManager.getHistoryManager().getHistory().get(0).getId());
        assertEquals(2, taskManager.getHistoryManager().getHistory().get(1).getId());
    }

    @Test
    void shouldNotRemoveTaskWithWrongID() {
        taskManager.addTask(task);
        assertThrows(IllegalArgumentException.class, () -> taskManager.removeTaskById(3));
    }

    @Test
    void shouldGetTasks() {
        taskManager.addTask(task);
        taskManager.addTask(new Task("1", "123"));
        assertFalse(taskManager.getTasks().isEmpty());
        assertFalse(taskManager.getHistoryManager().getHistory().isEmpty());
    }

    @Test
    void shouldGetSubtasks() {
        taskManager.addTask(epic);
        taskManager.addTask(new Subtask("1", "1", 1));
        taskManager.addTask(new Subtask("2", "1", 1));
        taskManager.getEpics();
        assertFalse(taskManager.getSubtasks().isEmpty());
        assertFalse(taskManager.getHistoryManager().getHistory().isEmpty());
        assertThrows(IllegalArgumentException.class, () -> taskManager.getSubtasksOfEpic(2));
    }

    @Test
    void shouldGetPrioritizeTasks() {
        taskManager.addTask(new Task("task3", "desc"));
        taskManager.addTask(new Task("task3", "desc", LocalDateTime.of(2024, 10, 10, 10, 0, 0), Duration.ofHours(1)));

        taskManager.addTask(new Task("task1", "desc"));
        taskManager.updateTimeTask(taskManager.getTaskById(3), LocalDateTime.of(2024, 10, 15, 10, 0, 0), Duration.ofDays(1));

        taskManager.addTask(new Task("task2", "desc"));
        taskManager.updateTimeTask(taskManager.getTaskById(4), (LocalDateTime.of(2024, 12, 10, 10, 0, 0)), Duration.ofDays(2));

        taskManager.addTask(epic);
        taskManager.addTask(new Subtask("Sub1", "desc", 5));
        taskManager.updateTimeTask(taskManager.getTaskById(6), LocalDateTime.of(2024, 9, 15, 10, 0, 0), Duration.ofDays(3));

        taskManager.addTask(new Subtask("Sub2", "desc", 5));
        taskManager.updateTimeTask(taskManager.getTaskById(7), LocalDateTime.of(2024, 9, 10, 10, 0, 0), Duration.ofDays(4));

        taskManager.addTask(new Subtask("Sub3", "desc", 5));
        taskManager.addTask(new Task("task1", "desc", LocalDateTime.of(2023, 10, 10, 10, 0, 0), Duration.ofHours(10)));

        assertEquals(9, taskManager.getPrioritizedTasks().size());
        assertEquals(taskManager.getTaskById(9), taskManager.getPrioritizedTasks().getFirst());
        assertEquals(taskManager.getTaskById(8), taskManager.getPrioritizedTasks().getLast());
    }

    @Test
    void crossingTasks() {
        taskManager.addTask(new Task("task3", "desc",
                LocalDateTime.of(2024, 10, 10, 10, 0, 0), Duration.ofDays(2)));

        assertThrows(IllegalArgumentException.class, () -> taskManager.addTask(new Task("task3", "desc",
                LocalDateTime.of(2024, 10, 10, 10, 0, 0), Duration.ofDays(2))),
                "The Same");
        assertThrows(IllegalArgumentException.class, () -> taskManager.addTask(new Task("task3", "desc",
                LocalDateTime.of(2024, 10, 8, 10, 0, 0), Duration.ofDays(2))),
                "before start, end the same");
        assertThrows(IllegalArgumentException.class, () -> taskManager.addTask(new Task("task3", "desc",
                LocalDateTime.of(2024, 10, 8, 10, 0, 0), Duration.ofDays(3))),
                "before start, end inner");
        assertThrows(IllegalArgumentException.class, () -> taskManager.addTask(new Task("task3", "desc",
                LocalDateTime.of(2024, 10, 8, 10, 0, 0), Duration.ofDays(5))),
                "before start, end after");
        assertThrows(IllegalArgumentException.class, () -> taskManager.addTask(new Task("task3", "desc",
                LocalDateTime.of(2024, 10, 11, 10, 0, 0), Duration.ofDays(3))),
                "start after, before end");
        assertThrows(IllegalArgumentException.class, () -> taskManager.addTask(new Task("task3", "desc",
                LocalDateTime.of(2024, 10, 11, 10, 0, 0), Duration.ofDays(10))),
                "start after, before after");

        assertDoesNotThrow(() -> taskManager.addTask(new Task("task3", "desc",
                LocalDateTime.of(2024, 10, 8, 9, 0, 0), Duration.ofDays(2))),
                "end before");

        assertThrows(IllegalArgumentException.class, () -> taskManager.addTask(new Task("task3", "desc",
                LocalDateTime.of(2024, 10, 12, 10, 0, 0), Duration.ofDays(2))),
                "start after, overlap - +15min no errors");

    }
}