package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.*;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class EpicTest {
    Epic epic;
    Subtask subtask;

    @BeforeEach
    void beforeEach() {
        epic = new Epic(1, "Epic", "Description", TaskStatus.NEW);
    }

    @Test
    void statusShouldBeNewForEmptySubtasks() {
        assertTrue(epic.getSubtaskList().isEmpty());
        assertEquals(TaskStatus.NEW, epic.getStatus());
    }

    @Test
    void statusShouldBeNewForRemovedSubtasks() {
        epic.addSubtask(new Subtask(1, "1Sub", "d", TaskStatus.DONE, 1));
        epic.removeSubtask(1);
        assertEquals(TaskStatus.NEW, epic.getStatus());
    }

    @Test
    void statusShouldBeNewForAllSubtasksNew() {
        epic.addSubtask(new Subtask(1, "1Sub", "d", TaskStatus.NEW, 1));
        epic.addSubtask(new Subtask(2, "2Sub", "d", TaskStatus.NEW, 1));
        epic.addSubtask(new Subtask(3, "3Sub", "d", TaskStatus.NEW, 1));
        assertEquals(TaskStatus.NEW, epic.getStatus());
    }

    @Test
    void statusShouldBeDoneForAllSubtasksDone() {
        epic.addSubtask(new Subtask(1, "1Sub", "d", TaskStatus.DONE, 1));
        epic.addSubtask(new Subtask(2, "2Sub", "d", TaskStatus.DONE, 1));
        epic.addSubtask(new Subtask(3, "3Sub", "d", TaskStatus.DONE, 1));
        assertEquals(TaskStatus.DONE, epic.getStatus());
    }

    @Test
    void statusShouldBeInProgressForSubtasksNewDone() {
        epic.addSubtask(new Subtask(1, "1Sub", "d", TaskStatus.DONE, 1));
        epic.addSubtask(new Subtask(2, "2Sub", "d", TaskStatus.NEW, 1));
        epic.addSubtask(new Subtask(3, "3Sub", "d", TaskStatus.DONE, 1));
        assertEquals(TaskStatus.IN_PROGRESS, epic.getStatus());
    }

    @Test
    void statusShouldBeDoneForSubtasksInProgress() {
        epic.addSubtask(new Subtask(1, "1Sub", "d", TaskStatus.IN_PROGRESS, 1));
        epic.addSubtask(new Subtask(2, "2Sub", "d", TaskStatus.IN_PROGRESS, 1));
        epic.addSubtask(new Subtask(3, "3Sub", "d", TaskStatus.IN_PROGRESS, 1));
        assertEquals(TaskStatus.IN_PROGRESS, epic.getStatus());
    }

    @Test
    void getTimeEpic() {
        assertEquals(Optional.empty(), epic.getDuration());
        assertEquals(Optional.empty(), epic.getStartTime());
        assertEquals(Optional.empty(), epic.getEndTime());
        assertDoesNotThrow(() -> epic);
    }

    @Test
    void getTimeEpicWithSubtask() {
        epic.addSubtask(new Subtask(2, "1Sub", "d", TaskStatus.NEW, 1));
        epic.addSubtask(new Subtask(3, "2Sub", "d", TaskStatus.NEW, 1));
        epic.addSubtask(new Subtask(4, "3Sub", "d", TaskStatus.NEW, 1));

        subtask = epic.getSubtaskList().get(2);
        subtask.setStartTime(2024, 12, 10, 10, 10, 0);
        subtask.setDuration(Duration.ofDays(10));

        assertTrue(subtask.getStartTime().isPresent());
        assertTrue(subtask.getDuration().isPresent());
        assertTrue(subtask.getEndTime().isPresent());

        subtask = epic.getSubtaskList().get(3);
        subtask.setStartTime(2024, 12, 15, 9, 0, 0);
        subtask.setDuration(Duration.ofDays(15));

        subtask = epic.getSubtaskList().get(4);
        subtask.setStartTime(2025, 1, 1, 12, 30, 0);
        subtask.setDuration(Duration.ofDays(30));

        assertTrue(epic.getStartTime().isPresent(), "Have Start");
        assertTrue(epic.getDuration().isPresent(), "Have Duration");
        assertTrue(epic.getEndTime().isPresent(), "Have end");

        assertEquals(LocalDateTime.of(2024, 12, 10, 10, 10, 0), epic.getStartTime().get());
        assertEquals(55, epic.getDuration().get().toDays());
        assertEquals(LocalDateTime.of(2025, 1, 31, 12, 30, 0), epic.getEndTime().get());

        assertDoesNotThrow(() -> epic);
    }
}