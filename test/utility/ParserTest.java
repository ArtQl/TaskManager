package utility;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import managers.ParserException;
import managers.memory.InMemoryTaskManager;
import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskStatus;
import org.junit.jupiter.api.Test;
import utility.gson_adapter.DurationAdapter;
import utility.gson_adapter.LocalDateAdapter;

import java.io.File;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ParserTest {

    private final static Gson gson = new GsonBuilder()
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .registerTypeAdapter(LocalDateTime.class, new LocalDateAdapter())
            .create();

    @Test
    void parseTasksToStr() {
        assertEquals(new Task("title", "desc", TaskStatus.NEW, 1),
                TaskParser.parseTaskFromCSVString("1,Task,title,NEW,desc,0,null,null\n"));
        assertEquals(new Epic("title", "desc", TaskStatus.NEW, 2),
                TaskParser.parseTaskFromCSVString("2,Epic,title,NEW,desc,0,null,null\n"));
        assertEquals(new Subtask("title", "desc", TaskStatus.NEW, 3, 2),
                TaskParser.parseTaskFromCSVString("3,Subtask,title,NEW,desc,2,null,null\n"));

        assertEquals(TaskParser.parseTaskToString(new Task("title", "desc", TaskStatus.NEW, 1)), "1,Task,title,NEW,desc,0,null,null");
        assertEquals(TaskParser.parseFileToCSVString(new File("test/utility/test.csv")), List.of(
                "1,Task,title,NEW,desc,0,1704103800000,18000000",
                "2,Epic,title,NEW,desc,0,1705331400000,2173800000",
                "3,Subtask,SubOne,NEW,Hello,2,1705331400000,865200000",
                "4,Subtask,SubTwo,NEW,Hello,2,1706638200000,1308600000",
                "",
                "3,4,2,1,"
        ));

        assertThrows(IllegalArgumentException.class, () -> TaskParser.parseTaskFromCSVString("2, 1,TASK,1,NEW,desc\n"));
        assertThrows(IllegalArgumentException.class, () -> TaskParser.parseTaskFromCSVString("1,TASK,1,a,desc\n"));
    }

    @Test
    void parseTasksToJson() {
        InMemoryTaskManager taskManager = new InMemoryTaskManager();
        Task task = new Task("Task2", "Ds", null, Duration.ofDays(10));
        taskManager.addTask(task);
        taskManager.addTask(new Epic("Epic", "Da"));
        taskManager.addTask(new Subtask("Sub1", "a", LocalDateTime.now().plusDays(30), Duration.ofDays(10), 2));
        taskManager.addTask(new Subtask("Sub2", "a", 2));

        assertEquals(TaskParser.parseTaskToJson(task), TaskParser.parseTaskToJson(taskManager.getTaskById(1)));
        String jsonEpic = TaskParser.parseTaskToJson(taskManager.getTaskById(2));
        Epic epic = (Epic) TaskParser.parseJsonToTask(jsonEpic);
        assertThrows(ParserException.class, () -> TaskParser.parseTaskToJson(null));

        assertThrows(ParserException.class, () -> TaskParser.parseJsonToTask(""));
        assertThrows(ParserException.class, () -> TaskParser.parseJsonToTask("   "));
        assertThrows(ParserException.class, () -> TaskParser.parseJsonToTask("/asdfkj"));

        assertTrue(TaskParser.parseJsonToTasks("").isEmpty());
        assertTrue(TaskParser.parseJsonToTasks("   ").isEmpty());
        assertThrows(ParserException.class, () -> TaskParser.parseJsonToTasks("/asdfkj"));

        String json = TaskParser.parseTasksToJson(taskManager.getMapTasks());
        Map<Integer, Task> map = TaskParser.parseJsonToTasks(json);
        assertEquals(taskManager.getMapTasks(), map);
    }

}
