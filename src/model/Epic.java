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
    }
}
