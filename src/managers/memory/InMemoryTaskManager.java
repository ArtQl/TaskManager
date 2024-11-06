package managers.memory;

import managers.TaskManager;
import managers.history.HistoryManager;
import model.Epic;
import model.Subtask;
import model.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    protected final Map<Integer, Task> tasks;
    protected final TreeSet<Task> priorityTask;
    private int id;
    protected final HistoryManager historyManager;
    protected final TimeIntervalTracker timeIntervalTracker;

    public InMemoryTaskManager(HistoryManager historyManager) {
        this.historyManager = historyManager;
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
//        if (tasks.isEmpty()) return new ArrayList<>();
//        List<Task> prioritizedList = new ArrayList<>(tasks.values().stream()
//                .filter(task -> task.getStartTime().isPresent())
//                .sorted(Comparator.comparing(task -> task.getStartTime().get())).toList());
//
//        prioritizedList.addAll(tasks.values().stream()
//                .filter(task -> task.getStartTime().isEmpty()).toList());
//        return prioritizedList;
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
    public void addTask(Task task) {
        if (task.getId() != null)
            throw new IllegalArgumentException("taskID set");
        if (tasks.containsValue(task))
            throw new IllegalArgumentException("Task already added in tasks");
        if (task.getStartTime().isPresent() && task.getEndTime().isPresent() &&
                timeIntervalTracker.hasOverlap(task))
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

//    private boolean hasOverlap(Task newTask) {
//        LocalDateTime newStart = newTask.getStartTime().orElse(null);
//        LocalDateTime newEnd = newTask.getEndTime().orElse(null);
//        if (newStart == null && newEnd == null) return false;
//        for (Task task : priorityTask) {
//            if (task.equals(newTask)) continue;
//            LocalDateTime existingStart = task.getStartTime().orElse(null);
//            LocalDateTime existingEnd = task.getEndTime().orElse(null);
//            if (existingStart != null && existingStart.isAfter(newEnd)) break;
//
//            if (existingStart != null && existingEnd != null)
//                //newStart.isBefore(existEnd) && newEnd.isAfter(existStart)
//                if (!(newEnd.isBefore(existingStart) || newStart.isAfter(existingEnd)))
//                    return true;
//        }
//        return false;
//    }

    @Override
    public void updateTimeTask(Task task, LocalDateTime localDateTime, Duration duration) {
        if (timeIntervalTracker.hasOverlap(task))
            throw new IllegalArgumentException("Tasks overlap time");

        priorityTask.remove(task);
        timeIntervalTracker.removeTaskFromInterval(task);

        if (task instanceof Subtask subtask) {
            priorityTask.remove(getTaskById(subtask.getIdEpic()));
            timeIntervalTracker.removeTaskFromInterval(getTaskById(subtask.getIdEpic()));
            task.setStartTime(localDateTime);
            task.setDuration(duration);
            priorityTask.add(getTaskById(subtask.getIdEpic()));
            timeIntervalTracker.addTaskInInterval(getTaskById(subtask.getIdEpic()));
        } else {
            task.setStartTime(localDateTime);
            task.setDuration(duration);
        }
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

        if (task instanceof Subtask subtask) {
            Subtask oldSubtask = (Subtask) tasks.get(task.getId());
            if (oldSubtask.getIdEpic() != subtask.getIdEpic()) {
                removeTaskById(oldSubtask.getId());
            }
            ((Epic) tasks.get(subtask.getIdEpic())).addSubtask(subtask);
        }
        tasks.put(task.getId(), task);
        timeIntervalTracker.removeTaskFromInterval(tasks.get(task.getId()));
        timeIntervalTracker.addTaskInInterval(task);
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
        List<Task> taskList = tasks.values().stream()
                .filter(value -> !(value instanceof Epic))
                .filter(value -> !(value instanceof Subtask)).toList();
        taskList.forEach(task -> removeTaskById(task.getId()));
    }

    @Override
    public void removeSubtasks() {
        if (tasks.isEmpty()) throw new RuntimeException("tasks empty");
        List<Task> taskList = tasks.values().stream()
                .filter(task -> task instanceof Subtask).toList();
        taskList.forEach(task -> removeTaskById(task.getId()));
    }

    @Override
    public void removeEpics() {
        if (tasks.isEmpty()) throw new RuntimeException("tasks empty");
        List<Task> taskList = tasks.values().stream()
                .filter(value -> value instanceof Epic)
                .toList();
        taskList.forEach(task -> removeTaskById(task.getId()));
    }

    @Override
    public void removeTaskById(int id) {
        if (tasks.isEmpty()) throw new RuntimeException("tasks empty");
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
        if (tasks.isEmpty() || !tasks.containsKey(id))
            throw new IllegalArgumentException("ID not founded");
        return tasks.get(id);
    }

    @Override
    public Map<Integer, Task> getMapTasks() {
        if (tasks.isEmpty()) throw new RuntimeException("tasks empty");
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
