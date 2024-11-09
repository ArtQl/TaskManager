package server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import managers.Managers;
import managers.TaskManager;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpTaskServer {
    private final HttpServer httpServer;
    private final TaskManager taskManager;

    public HttpTaskServer() throws IOException {
        taskManager = Managers.getDefault();
        httpServer = HttpServer.create(new InetSocketAddress(8080), 0);
        createContexts();
        httpServer.start();
        System.out.println("start server");
    }

    private void createContexts() {
        httpServer.createContext("/tasks", this::handleTasks);
    }

    private void handleTasks(HttpExchange exchange) throws IOException {
        System.out.println("Handle start");
        String path = exchange.getRequestURI().getPath();
        String query = exchange.getRequestURI().getQuery();

        if ("/tasks".equals(path) && exchange.getRequestMethod().equals("GET"))
            handleAllTasks(exchange);
        else if ("/tasks".equals(path)
                && exchange.getRequestMethod().equals("POST")
                && query != null && !query.isEmpty())
            handleGetTaskByID(exchange, query);
        else if ("/tasks/task".equals(path) && exchange.getRequestMethod().equals("GET"))
            handleTask(exchange);

    }

    private void handleAllTasks(HttpExchange exchange) throws IOException {
        StringBuilder stringBuilder = new StringBuilder("Tasks:\n");
        taskManager.getMapTasks().values().forEach(task -> stringBuilder.append(task).append("\n"));
        exchange.sendResponseHeaders(200, stringBuilder.toString().getBytes().length);
        exchange.getResponseBody().write(stringBuilder.toString().getBytes());
    }

    private void handleTask(HttpExchange exchange) throws IOException {
        StringBuilder stringBuilder = new StringBuilder("Task:\n");
        taskManager.getTasks().forEach(task -> stringBuilder.append(task).append("\n"));
        exchange.sendResponseHeaders(200, stringBuilder.toString().getBytes().length);
        exchange.getResponseBody().write(stringBuilder.toString().getBytes());
    }

    private void handleGetTaskByID(HttpExchange exchange, String query) throws IOException {
        StringBuilder stringBuilder = new StringBuilder("Task:\n");
        Map<String, String> queryParams = parseQueryParams(query);
        int taskId = Integer.parseInt(queryParams.get("id"));
        if (taskId != 0)
            stringBuilder.append(taskManager.getTaskById(taskId));
        exchange.sendResponseHeaders(200, stringBuilder.toString().getBytes().length);
        exchange.getResponseBody().write(stringBuilder.toString().getBytes());
    }

    Map<String, String> parseQueryParams(String query) {
        String[] array = query.toLowerCase().split("&");
        return Arrays.stream(array)
                .map(param -> param.split("="))
                .collect(Collectors.toMap(param -> param[0], param -> param[1]));
    }

    public static void main(String[] args) throws IOException {
        new HttpTaskServer();
        URI uri = URI.create("http://localhost:8080/tasks?id=2");
        HttpRequest httpRequest = HttpRequest
                .newBuilder()
                .POST(HttpRequest.BodyPublishers.noBody())
                .uri(uri)
                .version(HttpClient.Version.HTTP_2)
                .build();

        try (HttpClient httpClient = HttpClient.newHttpClient()) {
            HttpResponse<String> httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            System.out.println(httpResponse.body());
        } catch (InterruptedException |
                 IOException e) {
            System.out.println("Ошибка отправки запроса");
            System.out.println(e.getMessage());
        }
    }
}

