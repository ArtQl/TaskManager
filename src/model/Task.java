package model;

import managers.memory.InMemoryTaskManager;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Task implements Serializable {
    protected Integer id;
    protected final String title;
    protected final String description;
    protected TaskStatus status;

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
            default -> TaskStatus.NEW;
            case IN_PROGRESS -> TaskStatus.IN_PROGRESS;
            case DONE -> TaskStatus.DONE;
        };
    }

    public Task(String title, String description, TaskStatus status) {
        this.title = title;
        this.description = description;
        this.status = switch (status) {
            default -> TaskStatus.NEW;
            case IN_PROGRESS -> TaskStatus.IN_PROGRESS;
            case DONE -> TaskStatus.DONE;
        };
    }

    public void setId() {
        this.id = ++InMemoryTaskManager.id;
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
