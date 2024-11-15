package managers.server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;

public class KVServer {
    public static final int PORT = 8080;
    private final String apiToken;
    private final HttpServer server;
    private final Map<String, String> data = new HashMap<>();

    public KVServer() throws IOException {
        apiToken = "" + System.currentTimeMillis();
        server = HttpServer.create(new InetSocketAddress("localhost", PORT), 0);
        server.createContext("/register", this::handleRegister);
        server.createContext("/save", this::handleSave);
        server.createContext("/load", this::handleLoad);
        server.createContext("/delete", this::handleDelete);
    }

    private void handleRegister(HttpExchange exchange) throws IOException {
        System.out.print("\n/register");
        if ("GET".equals(exchange.getRequestMethod())) sendText(exchange, apiToken);
        else sendResponse(exchange, 405, "/register need GET-запрос: " + exchange.getRequestMethod());
    }

    private void handleDelete(HttpExchange h) throws IOException {
        String key = h.getRequestURI().getPath().substring("/delete/".length());
        if (hasNotAuth(h, "delete")) {
            sendResponse(h, 403, "Запрос неавторизован, нужен API_TOKEN в query со значением апи-ключа");
        } else if (!"DELETE".equals(h.getRequestMethod())) {
            sendResponse(h, 405, "/remove ждёт DELETE-запрос, а получил: " + h.getRequestMethod());
        } else if (key.isEmpty()) {
            sendResponse(h, 400, "Key для удаления пустой. key указывается в пути: /save/{key}");
        } else if (!data.containsKey(key)) {
            sendResponse(h, 400, "Key для удаления отсутствует в базе");
        } else {
            data.remove(key);
            sendResponse(h, 200, "Ключ " + key + " удален");
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
            sendResponse(h, 200, data.get(key));
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
        System.out.print(" - " + message);
        exchange.sendResponseHeaders(rCode, 0);
        exchange.getResponseBody().write(message.getBytes());
        exchange.close();
    }

    protected boolean hasNotAuth(HttpExchange h, String path) {
        System.out.print("\n/" + path);
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
