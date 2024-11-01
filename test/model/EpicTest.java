package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EpicTest {
    Epic epic;

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
}