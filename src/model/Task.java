package model;

import java.util.Objects;

public class Task {
    protected int id;
    protected String title;
    protected String description;
    protected TaskStatus status;

    public Task(String title, String description) {
        this.id = 0;
        this.title = title;
        this.description = description;
        this.status = TaskStatus.NEW;
    }

    public Task(String title, String description, TaskStatus status) {
        this.id = 0;
        this.title = title;
        this.description = description;
        this.status = switch (status) {
            default -> TaskStatus.NEW;
            case IN_PROGRESS -> TaskStatus.IN_PROGRESS;
            case DONE -> TaskStatus.DONE;
        };
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
        return "Task{" +
                "id=" + id +
                ", status='" + status + '\'' +
                '}';
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public String getTitle() {
        return title;
    }
}
