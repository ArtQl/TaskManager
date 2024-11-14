package managers.backed;

import managers.FileStorageManager;
import managers.StorageManager;
import managers.memory.InMemoryTaskManager;
import model.Epic;
import model.Subtask;
import model.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager {
    StorageManager fileStorageManager;

    public FileBackedTaskManager(StorageManager fileStorageManager) {
        super();
        this.fileStorageManager = fileStorageManager;
        checkSubtasks();
    }

    @Override
    public void addTask(Task task) {
        super.addTask(task);
        fileStorageManager.save(this);
    }

    @Override
    public List<Task> getTasks() {
        List<Task> map = super.getTasks();
        fileStorageManager.save(this);
        return map;
    }

    @Override
    public List<Subtask> getSubtasks() {
        List<Subtask> map = super.getSubtasks();
        fileStorageManager.save(this);
        return map;
    }

    @Override
    public List<Epic> getEpics() {
        List<Epic> map = super.getEpics();
        fileStorageManager.save(this);
        return map;
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        fileStorageManager.save(this);
    }

    @Override
    public void updateTimeTask(Task task, LocalDateTime localDateTime, Duration duration) {
        super.updateTimeTask(task, localDateTime, duration);
        fileStorageManager.save(this);
    }

    @Override
    public void removeAllTasks() {
        super.removeAllTasks();
        fileStorageManager.save(this);
    }

    @Override
    public void removeTasks() {
        super.removeTasks();
        fileStorageManager.save(this);
    }

    @Override
    public void removeSubtasks() {
        super.removeSubtasks();
        fileStorageManager.save(this);
    }

    @Override
    public void removeEpics() {
        super.removeEpics();
        fileStorageManager.save(this);
    }

    @Override
    public void removeTaskById(int id) {
        super.removeTaskById(id);
        fileStorageManager.save(this);
    }

    public StorageManager getStorageManager() {
        return fileStorageManager;
    }

    private void checkSubtasks() {
        List<Subtask> subtasks = getMapTasks().values().stream()
                .filter(task -> task instanceof Subtask)
                .map(task -> (Subtask) task).toList();
        subtasks.forEach(task -> ((Epic) getMapTasks().get(task.getIdEpic())).addSubtask(task));
    }
}
