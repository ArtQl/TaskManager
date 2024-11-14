package model;

import java.time.Duration;
import java.time.LocalDateTime;

public class Subtask extends Task {
    private final Integer idEpic;

    public Subtask(String title, String description, int idEpic) {
        super(title, description);
        this.idEpic = idEpic;
    }

    public Subtask(String title, String description, LocalDateTime startTime, Duration duration, Integer idEpic) {
        super(title, description, startTime, duration);
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

    public Subtask(String title, String description, TaskStatus status, Integer id, Integer idEpic, LocalDateTime startTime, Duration duration) {
        super(title, description, status, id, startTime, duration);
        this.idEpic = idEpic;
    }

    public int getIdEpic() {
        return idEpic;
    }
}
