package model;

import java.io.Serializable;

public class Subtask extends Task implements Serializable {
    private final Integer idEpic;

    public Subtask(String title, String description, int idEpic) {
        super(title, description);
        this.idEpic = idEpic;
    }

    public Subtask(String title, String description, TaskStatus status, int idEpic) {
        super(title, description, status);
        this.idEpic = idEpic;
    }

    public Subtask(String title, String description, TaskStatus status, int id, int idEpic) {
        super(title, description, status, id);
        this.idEpic = idEpic;
    }

    public int getIdEpic() {
        return idEpic;
    }
}
