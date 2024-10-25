package model;

import java.io.Serializable;

public class Subtask extends Task implements Serializable {
    private int idEpic;

    public Subtask(String title, String description) {
        super(title, description);
        this.idEpic = 0;
    }

    public Subtask(String title, String description, TaskStatus status, int idEpic) {
        super(title, description, status);
        this.idEpic = idEpic;
    }

    public Subtask(int id, String title, String description, TaskStatus status, int idEpic) {
        super(id, title, description, status);
        this.idEpic = idEpic;
    }

    public void setIdEpic(int idEpic) {
        this.idEpic = idEpic;
    }

    public int getIdEpic() {
        return idEpic;
    }

    @Override
    public String toString() {
        return super.toString() + "," + idEpic;
    }
}
