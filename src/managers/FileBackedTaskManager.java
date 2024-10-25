package managers;

import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskStatus;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager implements Serializable {
    private final File file;

    public FileBackedTaskManager(String filePath) {
        this.file = new File(filePath);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                System.out.println("Не удалось создать файл");
            }
        } else {
            loadFromFile(file);
        }
    }

    @Override
    public void addTask(Task task) {
        super.addTask(task);
        save();
    }

    @Override
    public void addSubtask(Subtask subtask, String titleEpic) {
        super.addSubtask(subtask, titleEpic);
        save();
    }

    @Override
    public HashMap<Integer, Task> getTasks() {
        HashMap<Integer, Task> map = super.getTasks();
        save();
        return map;
    }

    @Override
    public HashMap<Integer, Subtask> getSubtasks() {
        HashMap<Integer, Subtask> map = super.getSubtasks();
        save();
        return map;
    }

    @Override
    public HashMap<Integer, Epic> getEpics() {
        HashMap<Integer, Epic> map = super.getEpics();
        save();
        return map;
    }

    public void save() throws ManagerSaveException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
            bw.write("id,type,name,status,description,epic\n");
            writeTasks(bw);
            writeEpics(bw);
            writeSubtask(bw);
            bw.newLine();
            bw.write(InMemoryHistoryManager.toString(historyManager));
        } catch (IOException e) {
            throw new ManagerSaveException("Error saving data: " + e.getMessage(), e);
        }
    }

    private void writeTasks(BufferedWriter bw) throws IOException {
        if (!tasks.isEmpty()) {
            for (Task task : tasks.values()) {
                bw.write(task.toString() + "\n");
            }
        }
    }

    private void writeEpics(BufferedWriter bw) throws IOException {
        if (!epics.isEmpty()) {
            for (Epic epic : epics.values()) {
                bw.write(epic.toString() + "\n");
            }
        }
    }

    private void writeSubtask(BufferedWriter bw) throws IOException {
        if (!subtasks.isEmpty()) {
            for (Subtask subtask : subtasks.values()) {
                bw.write(subtask.toString() + "\n");
            }
        }
    }
//    private void writeHistory(BufferedWriter bw) throws IOException {
//        if (!historyManager.getHistory().isEmpty()) {
//            for (Task task : historyManager.getHistory()) {
//                bw.write(task.getId() + ",");
//            }
//        }
//    }

    public List<String> readFile() {
        List<String> list = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line = br.readLine();
            while ((line = br.readLine()) != null) {
                if (line.trim().length() <= 0) continue;
                list.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }

    public Task fromString(String str) {
        String[] newStr = str.split(",");
        int id = Integer.parseInt(newStr[0]);
        String title = newStr[2];
        TaskStatus taskStatus = TaskStatus.valueOf(newStr[3]);
        String description = newStr[4];

        return switch (newStr[1]) {
            case "EPIC" -> new Epic(id, title, description, taskStatus);
            case "SUBTASK" ->
                    new Subtask(id, title, description, taskStatus, Integer.parseInt(newStr[5]));
            default -> new Task(id, title, description, taskStatus);
        };
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager fileBacked = new FileBackedTaskManager(file.getPath());
        List<String> tasksStr = fileBacked.readFile();

        if(!tasksStr.isEmpty()) {
            List<Integer> historyId = InMemoryHistoryManager.fromString(tasksStr.getLast());
            tasksStr.removeLast();
            for (String str : tasksStr) {
                fileBacked.addTaskFromString(str);
            }
            fileBacked.restoreHistory(historyId);
        }
        return fileBacked;
    }

    private void addTaskFromString(String str) {
        Task task = fromString(str);
        if (task instanceof Epic epic) {
            epics.put(epic.getId(), epic);
        } else if (task instanceof Subtask subtask) {
            epics.get(subtask.getIdEpic()).addSubtask(subtask);
            subtasks.put(subtask.getId(),subtask);
        } else {
            tasks.put(task.getId(), task);
        }
    }

    private void restoreHistory(List<Integer> ids) {
        for (Integer id : ids) {
            if (tasks.containsKey(id))
                historyManager.add(tasks.get(id));
            else if (epics.containsKey(id))
                historyManager.add(epics.get(id));
            else historyManager.add(subtasks.get(id));
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
