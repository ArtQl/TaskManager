package server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import server.gson_adapter.DurationAdapter;
import server.gson_adapter.LocalDateAdapter;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;

public class KVServer {
    public static final int PORT = 8080;
    private final String apiToken;
    private final Gson gson;
    private final HttpServer server;
    private final Map<String, String> data = new HashMap<>();

    public KVServer() throws IOException {
        apiToken = generateApiToken();
        gson = new GsonBuilder()
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .registerTypeAdapter(LocalDateTime.class, new LocalDateAdapter())
                .serializeNulls().create();
        server = HttpServer.create(new InetSocketAddress("localhost", PORT), 0);
        server.createContext("/", this::handleTasks);
    }

    private void handleTasks(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        String query = exchange.getRequestURI().getQuery();
//        int taskId = query == null || query.isEmpty() ? 0 : Integer.parseInt(parseQueryParams(query).get("id"));
        if ("/register".equals(path)) {
            handleRegister(exchange);
        } else if("/save".equals(path)) {
            handleSave(exchange);
        } else if("/load".equals(path)) {
            handleLoad(exchange);
        } else if ("/tasks".equals(path) && exchange.getRequestMethod().equals("GET")) {
            handleAllTasks(exchange, gson);
        } else {
            sendResponse(exchange, 400, "Некорректный запрос");
        }

    }

    private void handleAllTasks(HttpExchange exchange, Gson gson) throws IOException {
        sendResponse(exchange, 200, gson.toJson(data.get("all tasks")));
    }

    private void handleLoad(HttpExchange h) throws IOException {
        System.out.println("\n/load");
        if (hasNotAuth(h)) {
            sendResponse(h, 403, "Запрос неавторизован, нужен API_TOKEN в query со значением апи-ключа");
        } else if ("GET".equals(h.getRequestMethod())) {
            String key = h.getRequestURI().getPath().substring("/load/".length());

            if (key.isEmpty()) {
                sendResponse(h, 400, "Key для загрузки пустой. key указывается в пути: /save/{key}");
            } else if (!data.containsKey(key)) {
                sendResponse(h, 400, "Key для загрузки отсутствует в базе");
            } else {
                sendResponse(h, 200, "Data:\n" + data.get(key));
            }
        } else {
            sendResponse(h, 405, "/load ждёт GET-запрос, а получил: " + h.getRequestMethod());
        }
    }

    private void handleSave(HttpExchange h) throws IOException {
        System.out.println("\n/save");
        if (hasNotAuth(h)) {
            sendResponse(h, 403, "Запрос неавторизован, нужен API_TOKEN в query со значением апи-ключа");
        } else if ("POST".equals(h.getRequestMethod())) {
            String key = h.getRequestURI().getPath().substring("/save/".length());
            String value = readText(h);

            if (key.isEmpty()) {
                sendResponse(h, 400, "Key для сохранения пустой. key указывается в пути: /save/{key}");
            } else if (value.isEmpty()) {
                sendResponse(h, 400, "Value для сохранения пустой. value указывается в теле запроса");
            } else {
                data.put(key, value);
                sendResponse(h, 200, "Значение для ключа " + key + " успешно обновлено!");
            }
        } else {
            sendResponse(h, 405, "/save ждёт POST-запрос, а получил: " + h.getRequestMethod());
        }
    }

    private void handleRegister(HttpExchange h) throws IOException {
        System.out.println("\n/register");
        if ("GET".equals(h.getRequestMethod())) sendText(h, apiToken);
        else sendResponse(h, 405, "/register need GET-запрос: " + h.getRequestMethod());
    }

    public void start() {
        System.out.println("Запускаем сервер на порту " + PORT);
        System.out.println("Открой в браузере http://localhost:" + PORT + "/");
        System.out.println("API_TOKEN: " + apiToken);
        server.start();
    }

    private void sendResponse(HttpExchange exchange, Integer rCode, String message) throws IOException {
        System.out.println(message);
        exchange.sendResponseHeaders(rCode, 0);
        exchange.close();
    }

    private String generateApiToken() {
        return "" + System.currentTimeMillis();
    }

    protected boolean hasNotAuth(HttpExchange h) {
        String rawQuery = h.getRequestURI().getRawQuery();
        return rawQuery == null || (!rawQuery.contains("API_TOKEN=" + apiToken) && !rawQuery.contains("API_TOKEN=DEBUG"));
    }

    protected String readText(HttpExchange h) throws IOException {
        return new String(h.getRequestBody().readAllBytes(), UTF_8);
    }

    protected void sendText(HttpExchange h, String text) throws IOException {
        byte[] resp = text.getBytes(UTF_8);
        h.getResponseHeaders().add("Content-Type", "application/json");
        h.sendResponseHeaders(200, resp.length);
        h.getResponseBody().write(resp);
    }
}
