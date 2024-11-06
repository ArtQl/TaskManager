package managers.backed;

import managers.history.HistoryManager;
import managers.memory.InMemoryTaskManager;
import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskStatus;

import java.io.*;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager implements Serializable {
    private final File file;

    public FileBackedTaskManager(String filePath, HistoryManager historyManager) {
        super(historyManager);
        this.file = new File(filePath);
        if (!file.exists()) {
            try {
                if (file.createNewFile())
                    System.out.println("File created");
            } catch (IOException e) {
                System.out.println("Не удалось создать файл");
            }
        }
    }

    @Override
    public void addTask(Task task) {
        super.addTask(task);
        save();
    }

    @Override
    public List<Task> getTasks() {
        List<Task> map = super.getTasks();
        save();
        return map;
    }

    @Override
    public List<Subtask> getSubtasks() {
        List<Subtask> map = super.getSubtasks();
        save();
        return map;
    }

    @Override
    public List<Epic> getEpics() {
        List<Epic> map = super.getEpics();
        save();
        return map;
    }

    public void save() throws ManagerSaveException {
        if (tasks.isEmpty()) throw new ManagerSaveException("Tasks are empty");
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
            bw.write("id,type,name,status,description,epic\n");
            for (Task task : tasks.values()) bw.write(task + "\n");
            bw.newLine();
            if (!historyManager.getHistory().isEmpty()) {
                for (Task task : historyManager.getHistory())
                    bw.write(task.getId() + ",");
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Error saving data: " + e.getMessage(), e);
        }
    }

    public Task getTaskFromString(String str) {
        String[] parts = str.trim().split(",");
        if (parts.length < 5 || parts.length > 8)
            throw new IllegalArgumentException("Wrong str length");

        Task task;
        try {
            int id = Integer.parseInt(parts[0]);
            String title = parts[2];
            String description = parts[4];
            TaskStatus taskStatus = TaskStatus.valueOf(parts[3]);
            task = switch (parts[1]) {
                case "EPIC" -> new Epic(title, description, taskStatus, id);
                case "TASK" -> new Task(title, description, taskStatus, id);
                default ->
                        new Subtask(title, description, taskStatus, id, Integer.parseInt(parts[5]));
            };
            if (parts.length >= 7) {
                if (task instanceof Subtask) {
                    task.setStartTime(LocalDateTime.ofInstant(Instant.ofEpochMilli(Long.parseLong(parts[6])), ZoneOffset.UTC));
                    task.setDuration(Duration.ofMillis(Long.parseLong(parts[7])));
                } else {
                    task.setStartTime(LocalDateTime.ofInstant(Instant.ofEpochMilli(Long.parseLong(parts[5])), ZoneOffset.UTC));
                    task.setDuration(Duration.ofMillis(Long.parseLong(parts[6])));
                }
            }
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Error parsing data from string");
        }
        return task;
    }

    public static FileBackedTaskManager loadFromFile(File file, HistoryManager historyManager) {
        FileBackedTaskManager fileBacked = new FileBackedTaskManager(file.getPath(), historyManager);
        List<String> tasksStr = fileBacked.readFile();
        if (tasksStr.isEmpty())
            throw new IllegalArgumentException("File is empty");
        if (tasksStr.getLast().isEmpty())
            throw new IllegalArgumentException("History is empty");

        List<Integer> historyId = Arrays.stream(tasksStr.getLast().split(",")).map(Integer::parseInt).toList();
        tasksStr.removeLast();
        tasksStr.removeLast();
        tasksStr.forEach(fileBacked::addTaskFromString);
        historyId.forEach(id -> {
            if (fileBacked.tasks.containsKey(id))
                historyManager.add(fileBacked.tasks.get(id));
        });
        fileBacked.setId();
        return fileBacked;
    }

    public List<String> readFile() {
        List<String> list = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line = br.readLine();
            if (line == null || !line.equals("id,type,name,status,description,epic"))
                throw new ManagerSaveException("File not have data");
            while ((line = br.readLine()) != null) {
                list.add(line);
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Wrong file");
        }
        return list;
    }

    private void addTaskFromString(String str) {
        Task task = getTaskFromString(str);
        if (task instanceof Epic epic) {
            tasks.put(epic.getId(), epic);
        } else if (task instanceof Subtask subtask) {
            ((Epic) tasks.get(subtask.getIdEpic())).addSubtask(subtask);
            tasks.put(subtask.getId(), subtask);
        } else {
            tasks.put(task.getId(), task);
        }
    }

    // TODO: 10/25/24 Сериализация 
//    public static FileBackedTaskManager loadFromFile(File file) {
//        FileBackedTaskManager fileBacked;
//        try (FileInputStream fis = new FileInputStream(file);
//             ObjectInputStream ois = new ObjectInputStream(fis)) {
//            fileBacked = (FileBackedTaskManager) ois.readObject();
//        } catch (IOException | ClassNotFoundException e) {
//            throw new ManagerSaveException("Error load class: " + e.getMessage(), e);
//        }
//        return fileBacked;
//    }
//
//    public static void putInFile(FileBackedTaskManager backedTaskManager, File file) {
//        try (FileOutputStream fos = new FileOutputStream(file);
//             ObjectOutputStream oos = new ObjectOutputStream(fos)) {
//            oos.writeObject(backedTaskManager);
//        } catch (IOException e) {
//            throw new ManagerSaveException("Error put class: " + e.getMessage(), e);
//        }
//    }
}
