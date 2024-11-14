package utility;

import com.google.gson.*;
import managers.backed.ManagerSaveException;
import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskStatus;
import server.gson_adapter.DurationAdapter;
import server.gson_adapter.LocalDateAdapter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

public class TaskParser {

    public static Task parseJsonToTask(String requestBody) {
        JsonElement jsonElement = JsonParser.parseString(requestBody);
        JsonObject jsonObject = jsonElement.getAsJsonObject();

        Integer id = jsonObject.get("id").getAsInt();
        String title = jsonObject.get("title").getAsString();
        String description = jsonObject.get("description").getAsString();
        TaskStatus taskStatus = TaskStatus.valueOf(jsonObject.get("status").getAsString());
        Duration duration = jsonObject.get("duration") instanceof JsonNull ? null
                : Duration.ofSeconds(jsonObject.get("duration").getAsLong());
        LocalDateTime startTime = jsonObject.get("startTime") instanceof JsonNull ? null
                : LocalDateTime.parse(jsonObject.get("startTime").getAsString());
        return switch (jsonObject.get("type").getAsString()) {
            case "Task" ->
                    new Task(title, description, taskStatus, id, startTime, duration);
            case "Subtask" ->
                    new Subtask(title, description, taskStatus, id, jsonObject.get("idEpic").getAsInt(), startTime, duration);
            default ->
                    new Epic(title, description, taskStatus, id, startTime, duration);
        };
    }

    public static String parseTaskToJson(Task task) {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .registerTypeAdapter(LocalDateTime.class, new LocalDateAdapter())
                .serializeNulls()
                .create();
        return gson.toJson(task);
    }

    public static String parseTaskToString(Task task) {
        List<String> list = new ArrayList<>();
        list.add(Integer.toString(task.getId()));
        list.add(task.getType());
        list.add(task.getTitle());
        list.add(task.getStatus().toString());
        list.add(task.getDescription());
        if (task instanceof Subtask subtask)
            list.add(Integer.toString(subtask.getIdEpic()));
        else list.add("0");
        task.getStartTime().ifPresentOrElse(time -> list.add(Long.toString(time.toInstant(ZoneOffset.UTC).toEpochMilli())), () -> list.add("null"));
        task.getDuration().ifPresentOrElse(time -> list.add(Long.toString(time.toMillis())), () -> list.add("null"));
        return String.join(",", list);
    }

    public static List<String> parseFileToCSVString(File file) {
        List<String> list = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line = br.readLine();
            if (line == null || !line.equals("id,type,title,status,description,epic,startTime,duration"))
                throw new ManagerSaveException("File not have data");
            while ((line = br.readLine()) != null) {
                list.add(line);
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Wrong file");
        }
        return list;
    }

    public static Task parseTaskFromCSVString(String str) {
        String[] parts = str.trim().split(",");
        if (parts.length < 8)
            throw new IllegalArgumentException("Wrong str length");

        Task task;
        try {
            int id = Integer.parseInt(parts[0]);
            String title = parts[2];
            TaskStatus taskStatus = TaskStatus.valueOf(parts[3]);
            String description = parts[4];
            int idEpic = parts[5].equals("0") ? 0 : Integer.parseInt(parts[5]);
            LocalDateTime startTime = parts[6].equals("null") ? null
                    : LocalDateTime.ofInstant(Instant.ofEpochMilli(Long.parseLong(parts[6])), ZoneOffset.UTC);
            Duration duration = parts[7].equals("null") ? null
                    : Duration.ofMillis(Long.parseLong(parts[7]));

            task = switch (parts[1]) {
                case "Epic" ->
                        new Epic(title, description, taskStatus, id, startTime, duration);
                case "Task" ->
                        new Task(title, description, taskStatus, id, startTime, duration);
                default ->
                        new Subtask(title, description, taskStatus, id, idEpic, startTime, duration);
            };
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Error parsing task from string");
        }
        return task;
    }
}
