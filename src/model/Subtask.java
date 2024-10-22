package model;

public class Subtask extends Task {
    private int idEpic;

    public Subtask(String title, String description) {
        super(title, description);
        this.idEpic = 0;
    }

    public Subtask(String title, String description, TaskStatus status, int idEpic) {
        super(title, description, status);
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
        return "Subtask{" +
                "id=" + id +
                ", status='" + status + '\'' +
                '}';
    }
}
