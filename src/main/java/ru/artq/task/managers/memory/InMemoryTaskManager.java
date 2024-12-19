package ru.artq.task.managers.memory;

import ru.artq.task.managers.TaskManager;
import ru.artq.task.managers.history.HistoryManager;
import ru.artq.task.managers.history.InMemoryHistoryManager;
import ru.artq.task.model.Epic;
import ru.artq.task.model.Subtask;
import ru.artq.task.model.Task;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class InMemoryTaskManager implements TaskManager {
    protected final Map<Integer, Task> tasks;
    protected final TreeSet<Task> priorityTask;
    private int id;
    protected final HistoryManager historyManager;
    protected final TimeIntervalTracker timeIntervalTracker;

    public InMemoryTaskManager() {
        this.historyManager = new InMemoryHistoryManager();
        tasks = new HashMap<>();
        priorityTask = new TreeSet<>(Comparator.comparing(
                (Task task) -> task.getStartTime().orElse(LocalDateTime.MAX)
        ).thenComparing(Task::getId));
        timeIntervalTracker = new TimeIntervalTracker();
    }

    @Override
    public HistoryManager getHistoryManager() {
        return historyManager;
    }

    @Override
    public TreeSet<Task> getPrioritizedTasks() {
        return priorityTask;
    }

    public void setId(Task task) {
        task.setId(++id);
    }

    public void setId() {
        if (id == 0 && !tasks.isEmpty()) id = Collections.max(tasks.keySet());
    }

    public int getId() {
        return id;
    }

    @Override
    public <T extends Task> Map<Integer, Task> getTasksByType(Class<T> type) {
        return tasks.entrySet().stream()
                .filter(entry -> type.equals(Task.class)
                        ? entry.getValue().getClass() == Task.class
                        : type.isInstance(entry.getValue()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (a, b) -> b));
    }

    @Override
    public void addTask(Task task) {
        if (task.getId() != null && tasks.containsKey(task.getId()) || tasks.containsValue(task))
            throw new IllegalArgumentException("Task already added in tasks");
        if (task.getStartTime().isPresent() && task.getEndTime().isPresent() &&
                timeIntervalTracker.hasOverlap(task) && !(task instanceof Subtask))
            throw new IllegalArgumentException("Tasks overlap time");
        if (task instanceof Subtask subtask) {
            if (!tasks.containsKey(subtask.getIdEpic())) {
                throw new IllegalArgumentException("Epic not found");
            }
            setId(subtask);
            ((Epic) tasks.get(subtask.getIdEpic())).addSubtask(subtask);
        }
        if (task.getId() == null) setId(task);
        tasks.put(task.getId(), task);
        priorityTask.add(task);
        timeIntervalTracker.addTaskInInterval(task);
    }

    @Override
    public void updateTask(Task task) {
        if (tasks.isEmpty() || task.getId() == null || !tasks.containsKey(task.getId()))
            throw new IllegalArgumentException("taskID not founded");
        if (tasks.get(task.getId()).equals(task))
            throw new IllegalArgumentException("Tasks the same");
        if (task.getClass() != tasks.get(task.getId()).getClass())
            throw new IllegalArgumentException("Current type task other");
        if (task.getStartTime().isPresent() && task.getEndTime().isPresent() &&
                timeIntervalTracker.hasOverlap(task))
            throw new IllegalArgumentException("Tasks overlap time");

        priorityTask.remove(getTaskById(task.getId()));
        timeIntervalTracker.removeTaskFromInterval(tasks.get(task.getId()));

        if (task instanceof Subtask subtask) {
            priorityTask.remove(getTaskById(subtask.getIdEpic()));
            timeIntervalTracker.removeTaskFromInterval(getTaskById(subtask.getIdEpic()));

            Subtask oldSubtask = (Subtask) tasks.get(task.getId());
            if (oldSubtask.getIdEpic() != subtask.getIdEpic()) {
                removeTaskById(oldSubtask.getId());
            }
            ((Epic) tasks.get(subtask.getIdEpic())).addSubtask(subtask);

            priorityTask.add(getTaskById(subtask.getIdEpic()));
            timeIntervalTracker.addTaskInInterval(getTaskById(subtask.getIdEpic()));
        }
        tasks.put(task.getId(), task);
        timeIntervalTracker.addTaskInInterval(task);
        priorityTask.add(task);
    }

    @Override
    public void removeAllTasks() {
        if (tasks.isEmpty()) throw new RuntimeException("tasks empty");
        tasks.clear();
        historyManager.getHistory().clear();
        timeIntervalTracker.getIntervalMap().clear();
    }

    @Override
    public void removeTasks() {
        if (tasks.isEmpty()) throw new RuntimeException("tasks empty");
        List<Integer> taskIds = tasks.values().stream()
                .filter(value -> !(value instanceof Epic || value instanceof Subtask))
                .map(Task::getId).toList();
        taskIds.forEach(this::removeTaskById);
    }

    @Override
    public void removeSubtasks() {
        if (tasks.isEmpty()) throw new RuntimeException("tasks empty");
        List<Integer> subtaskIds = tasks.values().stream().filter(value -> value instanceof Subtask)
                .map(Task::getId).toList();
        subtaskIds.forEach(this::removeTaskById);
    }

    @Override
    public void removeEpics() {
        if (tasks.isEmpty()) throw new RuntimeException("tasks empty");
        List<Integer> epicIds = tasks.values().stream().filter(value -> value instanceof Epic)
                .map(Task::getId).toList();
        epicIds.forEach(this::removeTaskById);
    }

    @Override
    public Task removeTaskById(int id) {
        if (tasks.isEmpty()) throw new RuntimeException("tasks empty");
        if (!tasks.containsKey(id)) {
            throw new IllegalArgumentException("ID not founded");
        }
        if (tasks.get(id) instanceof Epic epic) {
            List<Integer> subtaskIds = new ArrayList<>(epic.getSubtaskList().keySet());
            for (int idSubtask : subtaskIds) {
                removeTaskById(idSubtask);
            }
        } else if (tasks.get(id) instanceof Subtask subtask) {
            ((Epic) tasks.get(subtask.getIdEpic())).removeSubtask(id);
        }
        if (!historyManager.getHistory().isEmpty() && historyManager.getHistory().contains(tasks.get(id)))
            historyManager.remove(id);
        return tasks.remove(id);
    }

    @Override
    public Task getTaskById(int id) {
        if (tasks.isEmpty() || !tasks.containsKey(id))
            throw new IllegalArgumentException("ID not founded");
        return tasks.get(id);
    }

    @Override
    public Map<Integer, Task> getMapTasks() {
        return tasks;
    }

    @Override
    public List<Task> getTasks() {
        if (tasks.isEmpty()) throw new RuntimeException("tasks no found");
        List<Task> taskList = tasks.values().stream()
                .filter(task -> !(task instanceof Epic || task instanceof Subtask)).toList();
        taskList.forEach(historyManager::add);
        return taskList;
    }

    @Override
    public List<Subtask> getSubtasks() {
        if (tasks.isEmpty()) throw new RuntimeException("Subtasks no found");
        List<Subtask> taskList = tasks.values().stream()
                .filter(task -> task instanceof Subtask)
                .map(task -> (Subtask) task).toList();
        taskList.forEach(historyManager::add);
        return taskList;
    }

    @Override
    public List<Epic> getEpics() {
        if (tasks.isEmpty()) throw new RuntimeException("Epic no found");
        List<Epic> taskList = tasks.values().stream()
                .filter(task -> task instanceof Epic)
                .map(task -> (Epic) task).toList();
        taskList.forEach(historyManager::add);
        return taskList;
    }

    @Override
    public HashMap<Integer, Subtask> getSubtasksOfEpic(int id) {
        if (tasks.isEmpty() || tasks.containsKey(id) && !(tasks.get(id) instanceof Epic))
            throw new IllegalArgumentException("ID epic not founded");
        return ((Epic) tasks.get(id)).getSubtaskList();
    }

    @Override
    public void checkSubtasks() {
        List<Subtask> subtasks = getMapTasks().values().stream()
                .filter(task -> task instanceof Subtask)
                .map(task -> (Subtask) task).toList();
        subtasks.forEach(task -> ((Epic) getMapTasks().get(task.getIdEpic())).addSubtask(task));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InMemoryTaskManager that = (InMemoryTaskManager) o;
        return id == that.id && Objects.equals(tasks, that.tasks) && Objects.equals(historyManager, that.historyManager);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tasks, id, historyManager);
    }


}
