package managers.memory;

import managers.TaskManager;
import managers.history.HistoryManager;
import managers.history.InMemoryHistoryManager;
import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskStatus;

import java.util.HashMap;
import java.util.Map;

public class InMemoryTaskManager implements TaskManager {
    protected final Map<Integer, Task> tasks = new HashMap<>();
    public static int id;
    public final HistoryManager historyManager;

    public InMemoryTaskManager(HistoryManager historyManager) {
        this.historyManager = historyManager;
    }

    @Override
    public void addTask(Task task) {
        if (task.getId() != null) {
            System.out.println("taskID set");
            return;
        }
        if (tasks.containsKey(task.getId()) || tasks.containsValue(task)) {
            System.out.println("Task already added in tasks");
            return;
        }
        if (task instanceof Subtask subtask) {
            if (!tasks.containsKey(subtask.getIdEpic())) {
                throw new RuntimeException("Epic not found");
            }
            subtask.setId();
            ((Epic) tasks.get(subtask.getIdEpic())).addSubtask(subtask);
            updateStatus(subtask.getIdEpic());
        }
        if (task.getId() == null) task.setId();
        tasks.put(task.getId(), task);
    }

    @Override
    public void updateTask(Task task) {
        if (!tasks.containsKey(task.getId())) {
            System.out.println("ID not founded");
            return;
        }

        if (task instanceof Subtask subtask) {
            int idEpic = subtask.getIdEpic();
            if (((Subtask) tasks.get(task.getId())).getIdEpic() != idEpic) {
                removeTaskById(task.getId());
            }
            ((Epic) tasks.get(idEpic)).addSubtask((Subtask) task);
            updateStatus(subtask.getIdEpic());
        }
        tasks.put(task.getId(), task);
    }

    @Override
    public void removeAllTasks() {
        tasks.clear();
    }

    public void removeTasks() {
        for (Task task : tasks.values()) {
            if (task instanceof Epic || task instanceof Subtask) continue;
            tasks.remove(task.getId());
        }
    }

    public void removeSubtasks() {
        for (Task task : tasks.values()) {
            if (task instanceof Subtask) tasks.remove(task.getId());
        }
    }

    public void removeEpics() {
        for (Task task : tasks.values()) {
            if (task instanceof Epic) tasks.remove(task.getId());
        }
    }

    @Override
    public void removeTaskById(int id) {
        if (!tasks.containsKey(id)) {
            System.out.println("ID not founded");
            return;
        }
        if (tasks.get(id) instanceof Epic epic) {
            for (int idSubtask : epic.getSubtaskList().keySet()) {
                tasks.remove(idSubtask);
            }
        } else if (tasks.get(id) instanceof Subtask subtask) {
            ((Epic) tasks.get(subtask.getIdEpic())).getSubtaskList().remove(id);
            updateStatus(subtask.getIdEpic());
        }
        tasks.remove(id);
        if (!historyManager.getHistory().isEmpty()) historyManager.remove(id);
    }

    @Override
    public Task getTaskById(int id) {
        if (!tasks.containsKey(id)) {
            throw new IllegalArgumentException("ID not founded");
        }
        return tasks.get(id);
    }

    @Override
    public HashMap<Integer, Task> getTasks() {
        HashMap<Integer, Task> taskMap = new HashMap<>();
        for (Task task : tasks.values()) {
            if (task instanceof Epic || task instanceof Subtask) continue;
            historyManager.add(task);
            taskMap.put(task.getId(), task);
        }
        return taskMap;
    }

    @Override
    public HashMap<Integer, Subtask> getSubtasks() {
        HashMap<Integer, Subtask> subtasks = new HashMap<>();
        for (Task task : tasks.values()) {
            if (task instanceof Subtask subtask) {
                historyManager.add(subtask);
                subtasks.put(subtask.getId(), subtask);
            }
        }
        return subtasks;
    }

    @Override
    public HashMap<Integer, Epic> getEpics() {
        HashMap<Integer, Epic> epics = new HashMap<>();
        for (Task task : tasks.values()) {
            if (task instanceof Epic epic) {
                historyManager.add(epic);
                epics.put(epic.getId(), epic);
            }
        }
        return epics;
    }

    @Override
    public HashMap<Integer, Subtask> getSubtasksOfEpic(int id) {
        return tasks.get(id) != null &&
                tasks.get(id) instanceof Epic ?
                ((Epic) tasks.get(id)).getSubtaskList() : null;
    }

    public Epic getEpicByTitle(String title) {
        for (Task task : tasks.values()) {
            if (task.getTitle().equals(title)) return (Epic) task;
        }
        return null;
    }

    @Override
    public void updateStatus(int idEpic) {
        Epic epic = (Epic) getTaskById(idEpic);
        if (epic.getSubtaskList().size() == 0) {
            epic.setStatus(TaskStatus.NEW);
            return;
        }
        boolean hasDone = false;
        boolean hasInProgress = false;
        for (Subtask value : epic.getSubtaskList().values()) {
            if (value.getStatus().equals(TaskStatus.DONE)) {
                hasDone = true;
            } else {
                hasInProgress = true;
            }
            if (hasDone && hasInProgress) break;
        }
        if (hasDone && !hasInProgress) {
            epic.setStatus(TaskStatus.DONE);
        } else if (hasDone || hasInProgress) {
            epic.setStatus(TaskStatus.IN_PROGRESS);
        } else {
            epic.setStatus(TaskStatus.NEW);
        }
    }
}
