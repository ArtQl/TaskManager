package model;

import java.io.Serializable;
import java.util.HashMap;

public class Epic extends Task implements Serializable {
    private final HashMap<Integer, Subtask> subtaskList = new HashMap<>();

    public Epic(String title, String description) {
        super(title, description);
    }

    public Epic(int id, String title, String description, TaskStatus status) {
        super(id, title, description, status);
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
