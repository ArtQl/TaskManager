package model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class Task {
    protected Integer id;
    protected final String title;
    protected final String description;
    protected TaskStatus status;
    protected Duration duration;
    protected LocalDateTime startTime;
    protected String type = this.getClass().getSimpleName();

    public Task(String title, String description) {
        this.title = title;
        this.description = description;
        this.status = TaskStatus.NEW;
    }

    public Task(String title, String description, TaskStatus status) {
        this.title = title;
        this.description = description;
        this.status = switch (status) {
            case DONE -> TaskStatus.DONE;
            case IN_PROGRESS -> TaskStatus.IN_PROGRESS;
            default -> TaskStatus.NEW;
        };
    }

    public Task(String title, String description, TaskStatus status, int id) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.status = switch (status) {
            case DONE -> TaskStatus.DONE;
            case IN_PROGRESS -> TaskStatus.IN_PROGRESS;
            default -> TaskStatus.NEW;
        };
    }

    public Task(String title, String description, LocalDateTime startTime, Duration duration) {
        this.description = description;
        this.title = title;
        this.startTime = startTime;
        this.duration = duration;
        this.status = TaskStatus.NEW;
    }

    public Task(String title, String description, TaskStatus status, Integer id, LocalDateTime startTime, Duration duration) {
        this.title = title;
        this.description = description;
        this.id = id;
        this.status = status;
        this.duration = duration;
        this.startTime = startTime;
    }

    public Optional<LocalDateTime> getEndTime() {
        return getStartTime().isPresent() && getDuration().isPresent() ?
                Optional.of(startTime.plus(duration)) :
                Optional.empty();
    }

    public Optional<Duration> getDuration() {
        return Optional.ofNullable(duration);
    }

    public Optional<LocalDateTime> getStartTime() {
        return Optional.ofNullable(startTime);
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public void setStartTime(LocalDateTime localDateTime) {
        this.startTime = localDateTime;
    }

    public String getTime() {
        return getStartTime().isPresent() && getDuration().isPresent() && getEndTime().isPresent() ?
                "Start: " + startTime.format(DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm:ss"))
                + ", Duration: " + duration.toMinutes() + " min., "
                + "End time: " + getEndTime().get().format(DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm:ss")) :
                "No time";
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return Objects.equals(id, task.id)
                && Objects.equals(title, task.title)
                && Objects.equals(description, task.description)
                && status == task.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, description, status);
    }

    @Override
    public String toString() {
        List<String> list = new ArrayList<>();
        list.add(Integer.toString(id));
        list.add(getClass().getSimpleName().toUpperCase());
        list.add(title);
        list.add(status.toString());
        list.add(description);
        if (this instanceof Subtask subtask) list.add(Integer.toString(subtask.getIdEpic()));
        getStartTime().ifPresent(time -> list.add(Long.toString(time.toInstant(ZoneOffset.UTC).toEpochMilli())));
        getDuration().ifPresent(time -> list.add(Long.toString(time.toMillis())));
        return String.join(",", list);
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public Integer getId() {
        return id;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }
}
