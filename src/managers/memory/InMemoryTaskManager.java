package managers.memory;

import managers.TaskManager;
import managers.history.HistoryManager;
import managers.history.InMemoryHistoryManager;
import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskStatus;
import org.junit.jupiter.api.TestInstance;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryTaskManager implements TaskManager {
    protected final Map<Integer, Task> tasks = new HashMap<>();
    private int id;
    public final HistoryManager historyManager;

    public InMemoryTaskManager(HistoryManager historyManager) {
        this.historyManager = historyManager;
    }

    public void setId(Task task) {
        task.setId(++id);
    }

    @Override
    public void addTask(Task task) {
        if (task.getId() != null)
            throw new IllegalArgumentException("taskID set");
        if (tasks.containsValue(task))
            throw new IllegalArgumentException("Task already added in tasks");
        if (task instanceof Subtask subtask) {
            if (!tasks.containsKey(subtask.getIdEpic())) {
                throw new IllegalArgumentException("Epic not found");
            }
            setId(subtask);
            ((Epic) tasks.get(subtask.getIdEpic())).addSubtask(subtask);
        }
        if (task.getId() == null) setId(task);
        tasks.put(task.getId(), task);
    }

    @Override
    public void updateTask(Task task) {
        if (task.getId() == null || !tasks.containsKey(task.getId())) {
            throw new IllegalArgumentException("taskID not founded");
        } else if (tasks.get(task.getId()).equals(task)) {
            throw new IllegalArgumentException("Tasks the same");
        } else if (task.getClass() != tasks.get(task.getId()).getClass()) {
            throw new IllegalArgumentException("Current type task other");
        }

        if (task instanceof Subtask subtask) {
            Subtask oldSubtask = (Subtask) tasks.get(task.getId());
            if (oldSubtask.getIdEpic() != subtask.getIdEpic()) {
                removeTaskById(oldSubtask.getId());
            }
            ((Epic) tasks.get(subtask.getIdEpic())).addSubtask(subtask);
        }
        tasks.put(task.getId(), task);
    }

    @Override
    public void removeAllTasks() {
        tasks.clear();
        historyManager.getHistory().clear();
    }

    public void removeTasks() {
        List<Task> taskList = tasks.values().stream()
               .filter(value -> !(value instanceof Epic))
               .filter(value -> !(value instanceof Subtask)).toList();
        taskList.forEach(task -> removeTaskById(task.getId()));
    }

    public void removeSubtasks() {
        List<Task> taskList = tasks.values().stream()
                .filter(task -> task instanceof Subtask).toList();
        taskList.forEach(task -> removeTaskById(task.getId()));
    }

    public void removeEpics() {
        List<Task> taskList = tasks.values().stream()
                .filter(value -> value instanceof Epic)
                .toList();
        taskList.forEach(task -> removeTaskById(task.getId()));
    }

    @Override
    public void removeTaskById(int id) {
        if (!tasks.containsKey(id)) {
            throw new IllegalArgumentException("ID not founded");
        }
        if (tasks.get(id) instanceof Epic epic) {
            for (int idSubtask : epic.getSubtaskList().keySet()) {
                removeTaskById(idSubtask);
            }
        } else if (tasks.get(id) instanceof Subtask subtask) {
            ((Epic) tasks.get(subtask.getIdEpic())).removeSubtask(id);
        }
        tasks.remove(id);
        if (!historyManager.getHistory().isEmpty()) historyManager.remove(id);
    }

    @Override
    public Task getTaskById(int id) {
        if (!tasks.containsKey(id))
            throw new IllegalArgumentException("ID not founded");
        return tasks.get(id);
    }

    public Map<Integer, Task> getMapTasks() {
        return tasks;
    }

    @Override
    public List<Task> getTasks() {
        List<Task> taskList = tasks.values().stream()
                .filter(task -> !(task instanceof Epic || task instanceof Subtask)).toList();
        taskList.forEach(historyManager::add);
        return taskList;
    }

    @Override
    public List<Subtask> getSubtasks() {
        List<Subtask> taskList = tasks.values().stream()
                .filter(task -> task instanceof Subtask)
                .map(task -> (Subtask) task).toList();
        taskList.forEach(historyManager::add);
        return taskList;
    }

    @Override
    public List<Epic> getEpics() {
        List<Epic> taskList = tasks.values().stream()
                .filter(task -> task instanceof Epic)
                .map(task -> (Epic) task).toList();
        taskList.forEach(historyManager::add);
        return taskList;
    }

    @Override
    public HashMap<Integer, Subtask> getSubtasksOfEpic(int id) {
        if (tasks.containsKey(id) && !(tasks.get(id) instanceof Epic))
            throw new IllegalArgumentException("ID epic not founded");
        return ((Epic) tasks.get(id)).getSubtaskList();
    }
}
