package controller.inmemory;

import controller.Managers;
import controller.managers.TaskManager;
import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskStatus;

import java.util.HashMap;

public class InMemoryTaskManager implements TaskManager {
    private final HashMap<Integer, Task> tasks;
    private final HashMap<Integer, Subtask> subtasks;
    private final HashMap<Integer, Epic> epics;
    private static int id;

    public InMemoryTaskManager() {
        this.tasks = new HashMap<>();
        this.subtasks = new HashMap<>();
        this.epics = new HashMap<>();
    }

    @Override
    public void addTask(Task task) {
        if (task instanceof Epic epic) {
            epic.setId(++id);
            epics.put(epic.getId(), epic);
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
                task.setId(++id);
                tasks.put(task.getId(), task);
            }
        }
    }

    @Override
    public void addSubtask(Subtask subtask, String titleEpic) {
        Epic epic = getEpicByTitle(titleEpic);
        if (epic == null)
            throw new RuntimeException("Epic not found");
        subtask.setId(++id);
        subtask.setIdEpic(epic.getId());
        subtasks.put(subtask.getId(), subtask);
        epic.addSubtask(subtask);
    }

    @Override
    public void updateTask(int id, Task task) {
        task.setId(id);
        if (tasks.containsKey(id)) {
            tasks.put(id, task);
        } else if (epics.containsKey(id)) {
            epics.put(id, (Epic) task);
        } else if (subtasks.containsKey(id)) {
            int idEpic = ((Subtask) task).getIdEpic();
            if (subtasks.get(id).getIdEpic() != idEpic) {
                deleteTaskById(id);
            }
            epics.get(idEpic).addSubtask((Subtask) task);
            subtasks.put(id, (Subtask) task);
        }
        updateStatus();
    }

    @Override
    public void deleteAllTasks() {
        tasks.clear();
        epics.clear();
        subtasks.clear();
    }

    @Override
    public void deleteTaskById(int id) {
        if (tasks.containsKey(id)) {
            tasks.remove(id);
        } else if (epics.containsKey(id)) {
            epics.get(id).getSubtaskList().clear();
            epics.remove(id);
        } else {
            epics.get(subtasks.get(id).getIdEpic()).getSubtaskList().remove(id);
            subtasks.remove(id);
        }
        updateStatus();
    }

    @Override
    public Task getTaskById(int id) {
        if (tasks.containsKey(id)) return tasks.get(id);
        else if (epics.containsKey(id)) return epics.get(id);
        else return subtasks.get(id);
    }

    @Override
    public HashMap<Integer, Task> getTasks() {
        for (Task task : tasks.values()) {
            Managers.getDefaultHistory().add(task);
        }
        return tasks;
    }

    @Override
    public HashMap<Integer, Subtask> getSubtasks() {
        for (Subtask subtask : subtasks.values()) {
            Managers.getDefaultHistory().add(subtask);
        }
        return subtasks;
    }

    @Override
    public HashMap<Integer, Epic> getEpics() {
        for (Epic epic : epics.values()) {
            Managers.getDefaultHistory().add(epic);
        }
        return epics;
    }

    public HashMap<Integer, Subtask> getSubtasksOfEpic(int id) {
        return epics.get(id) != null ? epics.get(id).getSubtaskList() : null;
    }

    public Epic getEpicByTitle(String title) {
        for (Epic epic : epics.values()) {
            if (epic.getTitle().equals(title)) return epic;
        }
        return null;
    }

    @Override
    public void updateStatus() {
        for (Epic epic : epics.values()) {
            HashMap<Integer, Subtask> subtasksMap = epic.getSubtaskList();
            if (subtasksMap.size() == 0) {
                epic.setStatus(TaskStatus.NEW);
                continue;
            }
            int completedSubtasks = 0;
            int progresSubtasks = 0;
            for (Subtask value : subtasksMap.values()) {
                if (value.getStatus().equals(TaskStatus.DONE)) {
                    completedSubtasks++;
                } else if (value.getStatus().equals(TaskStatus.IN_PROGRESS)) {
                    progresSubtasks++;
                }
            }
            if (completedSubtasks == subtasksMap.size()) {
                epic.setStatus(TaskStatus.DONE);
            } else if (completedSubtasks > 0 || progresSubtasks > 0) {
                epic.setStatus(TaskStatus.IN_PROGRESS);
            } else {
                epic.setStatus(TaskStatus.NEW);
            }
        }
    }
}
