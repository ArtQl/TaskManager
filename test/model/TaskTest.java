package model;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.*;

public class TaskTest {

    @Test
    void getTime() {
        Task task = new Task("Title", "Desc", TaskStatus.NEW, 1);
        assertTrue(task.getStartTime().isEmpty());
        assertTrue(task.getDuration().isEmpty());
        assertTrue(task.getEndTime().isEmpty());
        assertDoesNotThrow(() -> task);

        task.setStartTime(LocalDateTime.of(2024, 1, 1, 10, 30, 30));
        assertEquals(LocalDateTime.ofInstant(Instant.ofEpochMilli(1704105030000L), ZoneOffset.UTC),
                task.getStartTime().get(), "Start set");

        task.setDuration(Duration.ofDays(10));
        assertEquals(Duration.ofNanos(864000000000000L), task.getDuration().get(), "Duration set");


        assertEquals(LocalDateTime.ofInstant(Instant.ofEpochMilli(1704969030000L), ZoneOffset.UTC),
                task.getEndTime().get(), "End set");

        assertEquals(LocalDateTime.of(2024, 1, 11, 10, 30, 30),
                task.getEndTime().get());
    }
}
