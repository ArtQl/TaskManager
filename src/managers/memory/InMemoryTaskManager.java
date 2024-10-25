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
    public final HistoryManager historyManager = new InMemoryHistoryManager();

    public Task getLastTask() {
        return getTaskById(id);
    }

    @Override
    public void addTask(Task task) {
        if (task instanceof Epic) {
            task.setId();
            tasks.put(task.getId(), task);
        } else {
            int equals = 0;
            if (tasks.size() != 0) {
                for (Task value : tasks.values()) {
                    if (value.equals(task)) {
                        equals++;
                        break;
                    }
                }
            }
            if (equals == 0) {
                task.setId();
                tasks.put(task.getId(), task);
            }
        }
    }

    @Override
    public void addSubtask(Subtask subtask, String titleEpic) {
        Epic epic = getEpicByTitle(titleEpic);
        if (epic == null)
            throw new RuntimeException("Epic not found");
        subtask.setId();
        subtask.setIdEpic(epic.getId());
        tasks.put(subtask.getId(), subtask);
        epic.addSubtask(subtask);
    }

    @Override
    public void updateTask(int id, Task task) {
        task.setId(id);
        if (tasks.containsKey(id)) {
            if (task instanceof Subtask subtask) {
                int idEpic = subtask.getIdEpic();
                if (((Subtask) tasks.get(id)).getIdEpic() != idEpic) {
                    deleteTaskById(id);
                }
                ((Epic) tasks.get(idEpic)).addSubtask((Subtask) task);
            }
            tasks.put(id, task);
        }
        updateStatus();
    }

    @Override
    public void deleteAllTasks() {
        tasks.clear();
    }

    @Override
    public void deleteTaskById(int id) {
        if (tasks.containsKey(id)) {
            if (tasks.get(id) instanceof Epic epic) {
                epic.getSubtaskList().clear();
            } else if (tasks.get(id) instanceof Subtask subtask) {
                ((Epic) tasks.get(subtask.getIdEpic())).getSubtaskList().remove(id);
            }
            tasks.remove(id);
        }
        historyManager.remove(id);
        updateStatus();
    }

    @Override
    public Task getTaskById(int id) {
        return tasks.getOrDefault(id, null);
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
        return tasks.get(id) != null ? ((Epic) tasks.get(id)).getSubtaskList() : null;
    }

    @Override
    public Epic getEpicByTitle(String title) {
        for (Task task : tasks.values()) {
            if (task.getTitle().equals(title)) return (Epic) task;
        }
        return null;
    }

    @Override
    public void updateStatus() {
        HashMap<Integer, Epic> epics = new HashMap<>();
        for (Task task : tasks.values()) {
            if (task instanceof Epic epic) epics.put(epic.getId(), epic);
        }

        for (Epic epic : epics.values()) {
            HashMap<Integer, Subtask> subtasksMap = epic.getSubtaskList();
            if (subtasksMap.size() == 0) {
                epic.setStatus(TaskStatus.NEW);
                continue;
            }
            int completedSubtasks = 0;
            int progressSubtasks = 0;
            for (Subtask value : subtasksMap.values()) {
                if (value.getStatus().equals(TaskStatus.DONE)) {
                    completedSubtasks++;
                } else if (value.getStatus().equals(TaskStatus.IN_PROGRESS)) {
                    progressSubtasks++;
                }
            }
            if (completedSubtasks == subtasksMap.size()) {
                epic.setStatus(TaskStatus.DONE);
            } else if (completedSubtasks > 0 || progressSubtasks > 0) {
                epic.setStatus(TaskStatus.IN_PROGRESS);
            } else {
                epic.setStatus(TaskStatus.NEW);
            }
        }
    }
}
