package server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import utility.gson_adapter.DurationAdapter;
import utility.gson_adapter.LocalDateAdapter;

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
        server.createContext("/register", this::handleRegister);
        server.createContext("/save", this::handleSave);
        server.createContext("/load", this::handleLoad);
        server.createContext("/remove", this::handleRemove);
    }

    private void handleRegister(HttpExchange exchange) throws IOException {
        System.out.println("\n/register");
        if ("GET".equals(exchange.getRequestMethod())) sendText(exchange, apiToken);
        else sendResponse(exchange, 405, "/register need GET-запрос: " + exchange.getRequestMethod());
    }

    private void handleRemove(HttpExchange h) throws IOException {
        String key = h.getRequestURI().getPath().substring("/load/".length());
        if (hasNotAuth(h, "remove")) {
            sendResponse(h, 403, "Запрос неавторизован, нужен API_TOKEN в query со значением апи-ключа");
        } else if (!"REMOVE".equals(h.getRequestMethod())) {
            sendResponse(h, 405, "/remove ждёт REMOVE-запрос, а получил: " + h.getRequestMethod());
        } else if (key.isEmpty()) {
            sendResponse(h, 400, "Key для удаления пустой. key указывается в пути: /save/{key}");
        } else if (!data.containsKey(key)) {
            sendResponse(h, 400, "Key для удаления отсутствует в базе");
        } else {
            sendResponse(h, 200, "Data:\n" + data.remove(key));
        }
    }

    private void handleLoad(HttpExchange h) throws IOException {
        String key = h.getRequestURI().getPath().substring("/load/".length());
        if (hasNotAuth(h, "load")) {
            sendResponse(h, 403, "Запрос неавторизован, нужен API_TOKEN в query со значением апи-ключа");
        } else if (!"GET".equals(h.getRequestMethod())) {
            sendResponse(h, 405, "/load ждёт GET-запрос, а получил: " + h.getRequestMethod());
        } else if (key.isEmpty()) {
            sendResponse(h, 400, "Key для загрузки пустой. key указывается в пути: /save/{key}");
        } else if (!data.containsKey(key)) {
            sendResponse(h, 400, "Key для загрузки отсутствует в базе");
        } else {
            sendResponse(h, 200, "Data:\n" + data.get(key));
        }
    }

    private void handleSave(HttpExchange h) throws IOException {
        String key = h.getRequestURI().getPath().substring("/save/".length());
        String value = readText(h);
        if (hasNotAuth(h, "save")) {
            sendResponse(h, 403, "Запрос неавторизован, нужен API_TOKEN в query со значением апи-ключа");
        } else if (!"POST".equals(h.getRequestMethod())) {
            sendResponse(h, 405, "/save ждёт POST-запрос, а получил: " + h.getRequestMethod());
        } else if (key.isEmpty()) {
            sendResponse(h, 400, "Key для сохранения пустой. key указывается в пути: /save/{key}");
        } else if (value.isEmpty()) {
            sendResponse(h, 400, "Value для сохранения пустой. value указывается в теле запроса");
        } else {
            data.put(key, value);
            sendResponse(h, 200, "Значение для ключа " + key + " успешно обновлено!");
        }
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

    protected boolean hasNotAuth(HttpExchange h, String path) {
        System.out.println("\n/" + path);
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
