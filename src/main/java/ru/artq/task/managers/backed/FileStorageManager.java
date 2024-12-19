package ru.artq.task.managers.backed;

import ru.artq.task.managers.StorageManager;
import ru.artq.task.managers.TaskManager;
import ru.artq.task.model.Epic;
import ru.artq.task.model.Subtask;
import ru.artq.task.model.Task;
import ru.artq.task.utility.TaskParser;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FileStorageManager implements StorageManager {

    private final File file;

    public FileStorageManager(File file) {
        this.file = file;
        if (!file.exists()) {
            try {
                if (file.createNewFile())
                    System.out.println("File created");
            } catch (IOException e) {
                System.out.println("Не удалось создать файл");
            }
        }
    }

    public void save(TaskManager taskManager) throws ManagerSaveException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
            bw.write("id,type,title,status,description,epic,startTime,duration\n");
            for (Task task : taskManager.getMapTasks().values())
                bw.write(TaskParser.parseTaskToString(task) + "\n");
            bw.newLine();
            if (!taskManager.getHistoryManager().getHistory().isEmpty()) {
                for (Task task : taskManager.getHistoryManager().getHistory())
                    bw.write(task.getId() + ",");
            }
            bw.flush();
        } catch (IOException e) {
            throw new ManagerSaveException("Error saving data: ", e);
        }
    }

    public Map<Integer, Task> load() {
        Map<Integer, Task> tasks = new HashMap<>();
        List<String> tasksStr = TaskParser.parseFileToCSVString(file);
        if (tasksStr.isEmpty()) return null;
        tasksStr.removeLast();
        if (tasksStr.getLast().isBlank()) tasksStr.removeLast();
        tasksStr.forEach(str -> {
            Task task = TaskParser.parseTaskFromCSVString(str);
            if (task instanceof Subtask subtask)
                ((Epic) tasks.get(subtask.getIdEpic())).addSubtask(subtask);
            tasks.put(task.getId(), task);
        });
        return tasks;
    }

    public List<Task> loadHistory() {
        String tasksStr = TaskParser.parseFileToCSVString(file).getLast();
        if (tasksStr.isBlank()) return null;
        List<Integer> historyId = Arrays.stream(tasksStr.split(",")).map(Integer::parseInt).toList();
        Map<Integer, Task> tasks = load();
        return historyId.stream().filter(tasks::containsKey).map(tasks::get).toList();
    }
}
