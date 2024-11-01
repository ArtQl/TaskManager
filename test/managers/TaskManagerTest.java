package managers;

import managers.backed.FileBackedTaskManager;
import managers.history.InMemoryHistoryManager;
import managers.memory.InMemoryTaskManager;
import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


class TaskManagerTest<T extends TaskManager> {
    TaskManager inMemoryTaskManager;
    FileBackedTaskManager fileBackedTaskManager;
    Task task;
    Epic epic;

    @BeforeEach
    void beforeEach() {
        inMemoryTaskManager = new InMemoryTaskManager(new InMemoryHistoryManager());
        task = new Task("1", "status");
        epic = new Epic("1", "status");
    }

    @Test
    void shouldAddTask() {
        inMemoryTaskManager.addTask(task);
        assertFalse(inMemoryTaskManager.getTasks().isEmpty(), "Task not added");

        assertThrows(IllegalArgumentException.class, () -> inMemoryTaskManager.addTask(new Task(1, "1", "1", TaskStatus.NEW)), "Add Task With ID");

        assertThrows(IllegalArgumentException.class, () -> inMemoryTaskManager.addTask(task), "Add same task");
    }

    @Test
    void shouldAddEpic() {
        inMemoryTaskManager.addTask(epic);
        assertFalse(inMemoryTaskManager.getEpics().isEmpty());
        assertEquals(TaskStatus.NEW, inMemoryTaskManager.getTaskById(1).getStatus());
    }

    @Test
    void shouldAddEpicSubtask() {
        inMemoryTaskManager.addTask(epic);
        inMemoryTaskManager.addTask(new Subtask(1, "1Sub", "d"));
        assertEquals(TaskStatus.NEW, inMemoryTaskManager.getTaskById(1).getStatus());
        assertEquals(TaskStatus.NEW, inMemoryTaskManager.getTaskById(2).getStatus());
    }

    @Test
    void shouldNotAddSubtaskWithNullIDEpic() {
        assertThrows(IllegalArgumentException.class, () -> inMemoryTaskManager.addTask(new Subtask(2, "1Sub", "d")));
    }

    @Test
    void shouldUpdateStatusTask() {
        inMemoryTaskManager.addTask(task);
        inMemoryTaskManager.updateTask(new Task(1, "First", "1", TaskStatus.DONE));
        assertEquals(TaskStatus.DONE, inMemoryTaskManager.getMapTasks().get(1).getStatus());
    }

    @Test
    void shouldUpdateStatusSubtask() {
        inMemoryTaskManager.addTask(epic);
        inMemoryTaskManager.addTask(new Subtask(1, "Subtask", "d"));
        inMemoryTaskManager.updateTask(new Subtask(2, "Subtask", "First", TaskStatus.DONE, 1));
        assertEquals(TaskStatus.DONE, inMemoryTaskManager.getMapTasks().get(1).getStatus());
    }

    @Test
    void shouldNotUpdateStatusTaskWithZeroID() {
        inMemoryTaskManager.addTask(task);
        assertThrows(IllegalArgumentException.class, () ->
                inMemoryTaskManager.updateTask(new Task("1", "1")));

        assertThrows(IllegalArgumentException.class, () -> inMemoryTaskManager.updateTask(task), "Try update status with same task");
    }

    @Test
    void shouldNotUpdateTaskOnEpic() {
        inMemoryTaskManager.addTask(epic);
        inMemoryTaskManager.addTask(new Subtask(1, "Subtask", "d"));
        assertThrows(IllegalArgumentException.class, () ->
                inMemoryTaskManager.updateTask(new Task(1, "Task", "First", TaskStatus.DONE)));
    }

    @Test
    void shouldUpdateSubtaskFromOtherEpic() {
        inMemoryTaskManager.addTask(epic);
        epic = (Epic) inMemoryTaskManager.getTaskById(1);
        inMemoryTaskManager.addTask(new Epic("2", "status"));
        inMemoryTaskManager.addTask(new Subtask(1, "Subtask", "d"));
        inMemoryTaskManager.updateTask(new Subtask(3, "Subtask", "d", TaskStatus.DONE, 2));
        assertTrue(((Epic) inMemoryTaskManager.getTaskById(1)).getSubtaskList().isEmpty());
    }

    @Test
    void shouldRemoveAllTacksMap() {
        inMemoryTaskManager.addTask(task);
        inMemoryTaskManager.addTask(epic);
        inMemoryTaskManager.addTask(new Subtask(2, "1", "1"));
        inMemoryTaskManager.removeAllTasks();
        assertThrows(RuntimeException.class, () -> inMemoryTaskManager.getMapTasks(), "tasks no empty");
    }

    @Test
    void shouldRemoveAllTasks() {
        inMemoryTaskManager.addTask(task);
        inMemoryTaskManager.addTask(epic);
        inMemoryTaskManager.addTask(new Subtask(2, "1", "1"));
        inMemoryTaskManager.removeTasks();
        assertTrue(inMemoryTaskManager.getTasks().isEmpty());
        assertFalse(inMemoryTaskManager.getEpics().isEmpty());
        assertEquals(2, inMemoryTaskManager.getHistoryManager().getHistory().get(0).getId());
    }

    @Test
    void shouldRemoveAllEpics() {
        inMemoryTaskManager.addTask(task);
        inMemoryTaskManager.addTask(epic);
        inMemoryTaskManager.addTask(new Subtask(2, "1", "1"));
        inMemoryTaskManager.removeEpics();
        assertFalse(inMemoryTaskManager.getTasks().isEmpty());
        assertTrue(inMemoryTaskManager.getEpics().isEmpty());
        assertTrue(inMemoryTaskManager.getSubtasks().isEmpty());
    }

    @Test
    void shouldRemoveAllSubtask() {
        inMemoryTaskManager.addTask(task);
        inMemoryTaskManager.addTask(epic);
        inMemoryTaskManager.addTask(new Subtask(2, "1", "1"));
        inMemoryTaskManager.removeSubtasks();
        assertFalse(inMemoryTaskManager.getTasks().isEmpty());
        assertFalse(inMemoryTaskManager.getEpics().isEmpty());
        assertTrue(inMemoryTaskManager.getSubtasks().isEmpty());
    }

    @Test
    void shouldRemoveTaskById() {
        inMemoryTaskManager.addTask(task);
        inMemoryTaskManager.removeTaskById(1);
        assertThrows(RuntimeException.class, () -> inMemoryTaskManager.getTasks(), "tasks no empty");
        assertThrows(RuntimeException.class, () -> inMemoryTaskManager.getMapTasks(), "tasksMap no empty");
        assertTrue(inMemoryTaskManager.getHistoryManager().getHistory().isEmpty());
    }

    @Test
    void shouldRemoveEpicById() {
        inMemoryTaskManager.addTask(task);
        inMemoryTaskManager.addTask(epic);
        inMemoryTaskManager.addTask(new Subtask(2, "1", "1"));
        inMemoryTaskManager.getTasks();
        inMemoryTaskManager.getEpics();
        inMemoryTaskManager.getSubtasks();
        inMemoryTaskManager.removeTaskById(2);
        assertTrue(inMemoryTaskManager.getEpics().isEmpty());
        assertTrue(inMemoryTaskManager.getSubtasks().isEmpty());
        assertEquals(1, inMemoryTaskManager.getHistoryManager().getHistory().get(0).getId());
    }

    @Test
    void shouldRemoveSubtaskById() {
        inMemoryTaskManager.addTask(task);
        inMemoryTaskManager.addTask(epic);
        inMemoryTaskManager.addTask(new Subtask(2, "1", "1"));
        inMemoryTaskManager.getTasks();
        inMemoryTaskManager.getEpics();
        inMemoryTaskManager.getSubtasks();
        inMemoryTaskManager.removeTaskById(3);
        assertFalse(inMemoryTaskManager.getEpics().isEmpty());
        assertTrue(inMemoryTaskManager.getSubtasks().isEmpty());
        assertEquals(1, inMemoryTaskManager.getHistoryManager().getHistory().get(0).getId());
        assertEquals(2, inMemoryTaskManager.getHistoryManager().getHistory().get(1).getId());
    }

    @Test
    void shouldNotRemoveTaskWithWrongID() {
        inMemoryTaskManager.addTask(task);
        assertThrows(IllegalArgumentException.class, () -> inMemoryTaskManager.removeTaskById(3));
    }

    @Test
    void shouldGetTasks() {
        inMemoryTaskManager.addTask(task);
        inMemoryTaskManager.addTask(new Task("1", "123"));
        assertFalse(inMemoryTaskManager.getTasks().isEmpty());
        assertFalse(inMemoryTaskManager.getHistoryManager().getHistory().isEmpty());
    }

    @Test
    void shouldGetSubtasks() {
        inMemoryTaskManager.addTask(epic);
        inMemoryTaskManager.addTask(new Subtask(1, "1", "1"));
        inMemoryTaskManager.addTask(new Subtask(1, "2", "1"));
        assertFalse(inMemoryTaskManager.getSubtasks().isEmpty());
        assertFalse(inMemoryTaskManager.getHistoryManager().getHistory().isEmpty());
        assertThrows(IllegalArgumentException.class, () -> inMemoryTaskManager.getSubtasksOfEpic(2));
    }
}
