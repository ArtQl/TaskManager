package managers;

import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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

        assertThrows(IllegalArgumentException.class, () -> taskManager.addTask(new Task(1, "1", "1", TaskStatus.NEW)), "Add Task With ID");

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
        taskManager.addTask(new Subtask(1, "1Sub", "d"));
        assertEquals(TaskStatus.NEW, taskManager.getTaskById(1).getStatus());
        assertEquals(TaskStatus.NEW, taskManager.getTaskById(2).getStatus());
    }

    @Test
    void shouldNotAddSubtaskWithNullIDEpic() {
        assertThrows(IllegalArgumentException.class, () -> taskManager.addTask(new Subtask(2, "1Sub", "d")));
    }

    @Test
    void shouldUpdateStatusTask() {
        taskManager.addTask(task);
        taskManager.updateTask(new Task(1, "First", "1", TaskStatus.DONE));
        assertEquals(TaskStatus.DONE, taskManager.getMapTasks().get(1).getStatus());
    }

    @Test
    void shouldUpdateStatusSubtask() {
        taskManager.addTask(epic);
        taskManager.addTask(new Subtask(1, "Subtask", "d"));
        taskManager.updateTask(new Subtask(2, "Subtask", "First", TaskStatus.DONE, 1));
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
        taskManager.addTask(new Subtask(1, "Subtask", "d"));
        assertThrows(IllegalArgumentException.class, () ->
                taskManager.updateTask(new Task(1, "Task", "First", TaskStatus.DONE)));
    }

    @Test
    void shouldUpdateSubtaskFromOtherEpic() {
        taskManager.addTask(epic);
        epic = (Epic) taskManager.getTaskById(1);
        taskManager.addTask(new Epic("2", "status"));
        taskManager.addTask(new Subtask(1, "Subtask", "d"));
        taskManager.updateTask(new Subtask(3, "Subtask", "d", TaskStatus.DONE, 2));
        assertTrue(((Epic) taskManager.getTaskById(1)).getSubtaskList().isEmpty());
    }

    @Test
    void shouldRemoveAllTacksMap() {
        taskManager.addTask(task);
        taskManager.addTask(epic);
        taskManager.addTask(new Subtask(2, "1", "1"));
        taskManager.removeAllTasks();
        assertThrows(RuntimeException.class, () -> taskManager.getMapTasks(), "tasks no empty");
    }

    @Test
    void shouldRemoveAllTasks() {
        taskManager.addTask(task);
        taskManager.addTask(epic);
        taskManager.addTask(new Subtask(2, "1", "1"));
        taskManager.removeTasks();
        assertTrue(taskManager.getTasks().isEmpty());
        assertFalse(taskManager.getEpics().isEmpty());
        assertEquals(2, taskManager.getHistoryManager().getHistory().getFirst().getId());
    }

    @Test
    void shouldRemoveAllEpics() {
        taskManager.addTask(task);
        taskManager.addTask(epic);
        taskManager.addTask(new Subtask(2, "1", "1"));
        taskManager.removeEpics();
        assertFalse(taskManager.getTasks().isEmpty());
        assertTrue(taskManager.getEpics().isEmpty());
        assertTrue(taskManager.getSubtasks().isEmpty());
    }

    @Test
    void shouldRemoveAllSubtask() {
        taskManager.addTask(task);
        taskManager.addTask(epic);
        taskManager.addTask(new Subtask(2, "1", "1"));
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
        assertThrows(RuntimeException.class, () -> taskManager.getMapTasks(), "tasksMap no empty");
        assertTrue(taskManager.getHistoryManager().getHistory().isEmpty());
    }

    @Test
    void shouldRemoveEpicById() {
        taskManager.addTask(task);
        taskManager.addTask(epic);
        taskManager.addTask(new Subtask(2, "1", "1"));
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
        taskManager.addTask(new Subtask(2, "1", "1"));
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
        taskManager.addTask(new Subtask(1, "1", "1"));
        taskManager.addTask(new Subtask(1, "2", "1"));
        taskManager.getEpics();
        assertFalse(taskManager.getSubtasks().isEmpty());
        assertFalse(taskManager.getHistoryManager().getHistory().isEmpty());
        assertThrows(IllegalArgumentException.class, () -> taskManager.getSubtasksOfEpic(2));
    }
}