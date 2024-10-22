package model;

import java.util.HashMap;

public class Epic extends Task {
    private final HashMap<Integer, Subtask> subtaskList;

    public Epic(String title, String description) {
        super(title, description);
        this.subtaskList = new HashMap<>();
    }

    public HashMap<Integer, Subtask> getSubtaskList() {
        return subtaskList;
    }

    public void addSubtask(Subtask subtask) {
        subtaskList.put(subtask.getId(), subtask);
    }

    @Override
    public String toString() {
        return "Epic{" +
                "id=" + id +
                ", status='" + status + '\'' +
                '}';
    }
}
