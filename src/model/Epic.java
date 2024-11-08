package model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Optional;

public class Epic extends Task {
    private final HashMap<Integer, Subtask> subtaskList = new HashMap<>();

    public Epic(String title, String description) {
        super(title, description);
    }

    public Epic(String title, String description, TaskStatus status, int id) {
        super(title, description, status, id);
    }

    @Override
    public Optional<LocalDateTime> getStartTime() {
        return subtaskList.values().stream()
                .map(Subtask::getStartTime)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .min(LocalDateTime::compareTo);
    }

    @Override
    public Optional<Duration> getDuration() {
        return subtaskList.values().stream()
                .map(Subtask::getDuration)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .reduce(Duration::plus);
    }

    @Override
    public Optional<LocalDateTime> getEndTime() {
        return subtaskList.values().stream()
                .map(Subtask::getEndTime)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .max(LocalDateTime::compareTo);
    }

    public HashMap<Integer, Subtask> getSubtaskList() {
        return subtaskList;
    }

    public void addSubtask(Subtask subtask) {
        subtaskList.put(subtask.getId(), subtask);
        updateStatus();
    }

    public void removeSubtask(int id) {
        subtaskList.remove(id);
        updateStatus();
    }

    public void updateStatus() {
        if (subtaskList.isEmpty()) {
            setStatus(TaskStatus.NEW);
            return;
        }
        boolean hasDone = false;
        boolean hasInProgress = false;
        boolean hasNew = false;
        for (Subtask subtask : subtaskList.values()) {
            if (subtask.getStatus().equals(TaskStatus.DONE))
                hasDone = true;
            else if (subtask.getStatus().equals(TaskStatus.IN_PROGRESS))
                hasInProgress = true;
            else hasNew = true;
            if (hasDone && hasInProgress) break;
        }
        if (hasDone && !hasInProgress && !hasNew)
            setStatus(TaskStatus.DONE);
        else if (hasDone || hasInProgress)
            setStatus(TaskStatus.IN_PROGRESS);
        else
            setStatus(TaskStatus.NEW);
    }
}
