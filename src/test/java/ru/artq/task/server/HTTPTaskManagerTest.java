package ru.artq.task.server;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.artq.task.managers.backed.FileStorageManager;
import ru.artq.task.managers.server.HTTPTaskManager;
import ru.artq.task.managers.server.KVServer;
import ru.artq.task.model.Epic;
import ru.artq.task.model.Subtask;
import ru.artq.task.model.Task;
import ru.artq.task.model.TaskStatus;
import ru.artq.task.utility.TaskParser;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;


class HTTPTaskManagerTest {
    static HTTPTaskManager taskManager;

    @BeforeEach
    void makeTaskManager() {
        try {
            new KVServer().start();
        } catch (IOException e) {
            System.out.println("Ошибка старта сервера" + e.getMessage());
        }
        taskManager = new HTTPTaskManager(new FileStorageManager(new File("src/main/resources/fileHistory.csv")), "http://localhost:8080");
        taskManager.addTask(new Task("Task", "Des"));
        taskManager.addTask(new Epic("Epic", "D"));
        taskManager.addTask(new Subtask("Sub1", "1", LocalDateTime.now(), Duration.ofDays(1), 2));
        taskManager.addTask(new Subtask("Sub2", "2", 2));
    }

    @Test
    void addTask() {
        assertEquals("{\"1\":{\"id\":1,\"title\":\"Task\",\"description\":\"Des\",\"status\":\"NEW\",\"type\":\"Task\"}}", taskManager.getKvTaskClient().load("Task"));
        assertNotEquals("{}", taskManager.getKvTaskClient().load("Task"));
        assertNotEquals("{}", taskManager.getKvTaskClient().load("Epic"));
        assertNotEquals("{}", taskManager.getKvTaskClient().load("Subtask"));
    }

    @Test
    void removeTaskById() {
        taskManager.removeTaskById(1);
        assertEquals("{}", taskManager.getKvTaskClient().load("Task"));
        assertNotEquals("{}", taskManager.getKvTaskClient().load("Epic"));
        assertNotEquals("{}", taskManager.getKvTaskClient().load("Subtask"));
        taskManager.removeTaskById(3);
        taskManager.getKvTaskClient().load("Epic");
        taskManager.getKvTaskClient().load("Subtask");
        taskManager.removeTaskById(2);
        assertEquals("{}", taskManager.getKvTaskClient().load("Epic"));
        assertEquals("{}", taskManager.getKvTaskClient().load("Subtask"));
    }

    @Test
    void removeEpics() {
        taskManager.removeEpics();
        assertNotEquals("{}", taskManager.getKvTaskClient().load("Task"));
        assertTrue(taskManager.getKvTaskClient().load("Epic").isEmpty());
        assertTrue(taskManager.getKvTaskClient().load("Subtask").isEmpty());
    }

    @Test
    void removeSubtasks() {
        taskManager.removeSubtasks();
        assertNotEquals("{}", taskManager.getKvTaskClient().load("Task"));
        assertNotEquals("{}", taskManager.getKvTaskClient().load("Epic"));
        assertTrue(taskManager.getKvTaskClient().load("Subtask").isEmpty());
    }

    @Test
    void removeTasks() {
        taskManager.removeTasks();
        assertTrue(taskManager.getKvTaskClient().load("Task").isEmpty());
        assertNotEquals("{}", taskManager.getKvTaskClient().load("Epic"));
        assertNotEquals("{}", taskManager.getKvTaskClient().load("Subtask"));
    }

    @Test
    void removeAllTasks() {
        taskManager.removeAllTasks();
        assertTrue(taskManager.getKvTaskClient().load("Task").isEmpty());
        assertTrue(taskManager.getKvTaskClient().load("Subtask").isEmpty());
        assertTrue(taskManager.getKvTaskClient().load("Epic").isEmpty());
        assertTrue(taskManager.getKvTaskClient().load("History").isEmpty());
    }

    @Test
    void updateTask() {
        taskManager.getKvTaskClient().load("Task");
        taskManager.updateTask(new Task("Task1", "d", TaskStatus.IN_PROGRESS, 1, LocalDateTime.of(1, 1, 1, 1, 1, 1).plusDays(30), Duration.ofHours(10)));
        assertEquals("{\"1\":{\"id\":1,\"title\":\"Task1\",\"description\":\"d\",\"status\":\"IN_PROGRESS\",\"duration\":36000,\"startTime\":\"0001-01-31T01:01:01\",\"type\":\"Task\"}}", taskManager.getKvTaskClient().load("Task"));
    }

    @Test
    void getTasks() {
        taskManager.getSubtasks();
        assertEquals(taskManager.getHistoryManager().getHistory(), TaskParser.parseJsonToListTasks(taskManager.getKvTaskClient().load("History")));

        taskManager.getTasks();
        assertEquals(taskManager.getHistoryManager().getHistory(), TaskParser.parseJsonToListTasks(taskManager.getKvTaskClient().load("History")));
        taskManager.getEpics();

        assertEquals(taskManager.getHistoryManager().getHistory(), TaskParser.parseJsonToListTasks(taskManager.getKvTaskClient().load("History")));
    }
}