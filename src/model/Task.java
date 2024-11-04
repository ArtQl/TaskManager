package model;

import java.io.Serializable;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class Task implements Serializable {
    protected Integer id;
    protected final String title;
    protected final String description;
    protected TaskStatus status;
    protected Duration duration;
    protected LocalDateTime startTime;

    public Task(String title, String description) {
        this.title = title;
        this.description = description;
        this.status = TaskStatus.NEW;
    }

    public Task(int id, String title, String description, TaskStatus status) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.status = switch (status) {
            case DONE -> TaskStatus.DONE;
            case IN_PROGRESS -> TaskStatus.IN_PROGRESS;
            default -> TaskStatus.NEW;
        };
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

    public void setStartTime(int year, int month, int day, int hour, int min, int sec) {
        this.startTime = LocalDateTime.of(year, month, day, hour, min, sec);
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

        if (!Objects.equals(title, task.title))
            return false;
        if (!Objects.equals(description, task.description))
            return false;
        return Objects.equals(status, task.status);
    }

    @Override
    public int hashCode() {
        int result = title != null ? title.hashCode() : 0;
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (status != null ? status.hashCode() : 0);
        return result;
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
        getDuration().ifPresent(time -> list.add(Long.toString(time.toNanos())));
        getEndTime().ifPresent(time -> list.add(Long.toString(time.toInstant(ZoneOffset.UTC).toEpochMilli())));
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
}
