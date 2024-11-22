package managers.server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import managers.Managers;
import managers.TaskManager;
import model.Task;
import utility.TaskParser;
import utility.gson_adapter.DurationAdapter;
import utility.gson_adapter.LocalDateAdapter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpTaskServer {
    private final TaskManager taskManager;
    private final Gson gson;
    private final KVTaskClient kvTaskClient;

    public HttpTaskServer(KVTaskClient kvTaskClient) throws IOException {
        this.kvTaskClient = kvTaskClient;
        taskManager = Managers.getDefault();
        gson = new GsonBuilder()
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .registerTypeAdapter(LocalDateTime.class, new LocalDateAdapter())
                .serializeNulls()
                .create();
        HttpServer httpServer = HttpServer.create(new InetSocketAddress(8080), 0);
        httpServer.createContext("/tasks", this::handleTasks);
        httpServer.start();
    }

    private void handleTasks(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        String query = exchange.getRequestURI().getQuery();
        int taskId = query == null || query.isEmpty() ? 0 : Integer.parseInt(parseQueryParams(query).get("id"));

        if ("/tasks".equals(path) && exchange.getRequestMethod().equals("GET")) {
            handleAllTasks(exchange, gson);
        } else if ("/tasks/task".equals(path) && exchange.getRequestMethod().equals("GET") && taskId != 0) {
            handleGetTaskByID(exchange, gson, taskId);
        } else if ("/tasks/task".equals(path) && exchange.getRequestMethod().equals("GET")) {
            handleTask(exchange, gson);
        } else if ("/tasks/task".equals(path) && exchange.getRequestMethod().equals("POST")) {
            handleAddTask(exchange);
        } else if ("/tasks/task".equals(path) && exchange.getRequestMethod().equals("DELETE") && taskId != 0) {
            handleDeleteTaskByID(exchange, taskId);
        } else if ("/tasks/task".equals(path) && exchange.getRequestMethod().equals("DELETE")) {
            handleDeleteTasks(exchange);
        } else if ("/tasks/subtask".equals(path) && exchange.getRequestMethod().equals("GET")) {
            handleSubtask(exchange, gson);
        } else if ("/tasks/epic".equals(path) && exchange.getRequestMethod().equals("GET")) {
            handleEpic(exchange, gson);
        } else if ("/tasks/subtask/epic".equals(path) && exchange.getRequestMethod().equals("GET") && taskId != 0) {
            handleGetSubtasksOfEpic(exchange, gson, taskId);
        } else if ("/tasks/history".equals(path) && exchange.getRequestMethod().equals("GET")) {
            handleGetHistory(exchange, gson);
        } else {
            sendResponse(exchange, "Некорректный запрос", 400);
            System.out.println("Некорректный запрос");
        }
    }

    private void handleAllTasks(HttpExchange exchange, Gson gson) throws IOException {
        sendResponse(exchange, gson.toJson(taskManager.getMapTasks()), 200);
    }

    private void handleTask(HttpExchange exchange, Gson gson) throws IOException {
        sendResponse(exchange, gson.toJson(taskManager.getTasks()), 200);
    }

    private void handleSubtask(HttpExchange exchange, Gson gson) throws IOException {
        sendResponse(exchange, gson.toJson(taskManager.getSubtasks()), 200);
    }

    private void handleEpic(HttpExchange exchange, Gson gson) throws IOException {
        sendResponse(exchange, gson.toJson(taskManager.getEpics()), 200);
    }

    private void handleGetTaskByID(HttpExchange exchange, Gson gson, Integer taskId) throws IOException {
        sendResponse(exchange, gson.toJson(taskManager.getTaskById(taskId)), 200);
    }

    private void handleGetSubtasksOfEpic(HttpExchange exchange, Gson gson, Integer taskId) throws IOException {
        sendResponse(exchange, gson.toJson(taskManager.getSubtasksOfEpic(taskId)), 200);
    }

    private void handleGetHistory(HttpExchange exchange, Gson gson) throws IOException {
        sendResponse(exchange, gson.toJson(taskManager.getHistoryManager().getHistory()), 200);
    }

    private void handleDeleteTaskByID(HttpExchange exchange, Integer taskId) throws IOException {
        taskManager.removeTaskById(taskId);
        sendResponse(exchange, "Delete task", 200);
    }

    private void handleDeleteTasks(HttpExchange exchange) throws IOException {
        taskManager.removeTasks();
        sendResponse(exchange, "Tasks are deleted", 200);
    }

    private void handleAddTask(HttpExchange exchange) throws IOException {
        Task task = TaskParser.parseJsonToTask(readRequestBody(exchange));
        taskManager.addTask(task);
        sendResponse(exchange, "Task added", 201);
    }

    private void sendResponse(HttpExchange exchange, String responseBody, Integer rCode) throws IOException {
        exchange.getResponseHeaders().add("Content-Type", "application/json");
        exchange.sendResponseHeaders(rCode, responseBody.getBytes().length);
        exchange.getResponseBody().write(responseBody.getBytes());
        exchange.close();
    }

    private String readRequestBody(HttpExchange exchange) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(exchange.getRequestBody()))) {
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
        }
        return sb.toString();
    }

    private Map<String, String> parseQueryParams(String query) {
        String[] array = query.toLowerCase().split("&");
        return Arrays.stream(array)
                .map(param -> param.split("="))
                .collect(Collectors.toMap(param -> param[0], param -> param[1]));
    }
}