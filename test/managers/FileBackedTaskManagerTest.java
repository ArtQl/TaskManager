package managers;

import managers.backed.FileBackedTaskManager;
import managers.backed.ManagerSaveException;
import managers.history.InMemoryHistoryManager;
import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskStatus;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

import static org.junit.jupiter.api.Assertions.*;


public class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {
    @Override
    protected FileBackedTaskManager createTaskManager() {
        return new FileBackedTaskManager(
                System.getProperty("user.dir") + "/src/" + "fileHistory.csv",
                new InMemoryHistoryManager());
    }

    @Test
    void shouldNotSaveDataToFileWithEmptyTasks() {
        assertThrows(ManagerSaveException.class, () -> taskManager.save());
    }

    @Test
    void shouldSaveDataToFile() {
        assertDoesNotThrow(() -> taskManager.addTask(task));
        assertDoesNotThrow(() -> taskManager.addTask(epic));
        assertDoesNotThrow(() -> taskManager.addTask(new Subtask("title", "desc", TaskStatus.NEW, 2)));
        assertEquals(4, taskManager.readFile().size());
        taskManager.getTasks();
        taskManager.getEpics();
        assertEquals(5, taskManager.readFile().size());
    }

    @Test
    void shouldGetTaskFromString() {
        assertEquals(new Task(1, "title", "desc", TaskStatus.NEW),
                taskManager.getTaskFromString("1,TASK,title,NEW,desc\n"));
        assertEquals(new Epic(2, "title", "desc", TaskStatus.NEW),
                taskManager.getTaskFromString("2,EPIC,title,NEW,desc\n"));
        assertEquals(new Subtask(3, "title", "desc", TaskStatus.NEW, 2),
                taskManager.getTaskFromString("3,SUBTASK,title,NEW,desc,2\n"));

        assertThrows(IllegalArgumentException.class, () -> task = taskManager.getTaskFromString("2, 1,TASK,1,NEW,desc\n"));
        assertThrows(IllegalArgumentException.class, () -> task = taskManager.getTaskFromString("1,TASK,1,a,desc\n"));
    }

    @Test
    void shouldNotReadWrongFile() {
        assertThrows(ManagerSaveException.class, () -> FileBackedTaskManager.loadFromFile(new File(System.getProperty("user.dir") + "/src/" + "file2.csv"), new InMemoryHistoryManager()));
    }

    @Test
    void shouldNotLoadEmptyFile() {
        try(PrintWriter printWriter = new PrintWriter(System.getProperty("user.dir") + "/src/" + "fileHistory.csv")) {
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        assertThrows(ManagerSaveException.class, () -> FileBackedTaskManager.loadFromFile(new File(System.getProperty("user.dir") + "/src/" + "fileHistory.csv"), new InMemoryHistoryManager()), "file no have data");
    }
    @Test
    void shouldReturnTaskManagerAndHistoryManager() {
        taskManager.addTask(task);
        taskManager.addTask(epic);
        taskManager.addTask(new Subtask("title", "desc", TaskStatus.NEW, 2));
        taskManager.getTasks();
        taskManager.getEpics();
        FileBackedTaskManager fileBackedTaskManager = FileBackedTaskManager.loadFromFile(new File(System.getProperty("user.dir") + "/src/" + "fileHistory.csv"), new InMemoryHistoryManager());

        assertEquals(taskManager.getMapTasks(), fileBackedTaskManager.getMapTasks());
        assertEquals(taskManager.getHistoryManager().getHistory(), fileBackedTaskManager.getHistoryManager().getHistory());
//        assertEquals(taskManager.getHistoryManager(), fileBackedTaskManager.getHistoryManager());
    }
}
