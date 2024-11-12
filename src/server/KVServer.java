package server;

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
        apiToken = generateApiToken();
        server = HttpServer.create(new InetSocketAddress("localhost", PORT), 0);
        server.createContext("/register", this::register);
        server.createContext("/save", this::save);
        server.createContext("/load", this::load);
    }

    private void load(HttpExchange h) throws IOException {
        System.out.println("\n/load");
        if (hasNotAuth(h)) {
            sendResponse(h, 403, "Запрос неавторизован, нужен параметр в query API_TOKEN со значением апи-ключа");
        } else if ("GET".equals(h.getRequestMethod())) {
            String key = h.getRequestURI().getPath().substring("/load/".length());

            if (key.isEmpty()) {
                sendResponse(h, 400, "Key для загрузки пустой. key указывается в пути: /save/{key}");
            } else if (!data.containsKey(key)){
                sendResponse(h, 400, "Key для загрузки отсутствует в базе");
            } else {
                sendResponse(h, 200, "Data:\n" + data.get(key));
            }
        } else {
            sendResponse(h, 405, "/load ждёт GET-запрос, а получил: " + h.getRequestMethod());
        }
    }

    private void save(HttpExchange h) throws IOException {
        System.out.println("\n/save");
        if (hasNotAuth(h)) {
            sendResponse(h, 403, "Запрос неавторизован, нужен параметр в query API_TOKEN со значением апи-ключа");
        } else if ("POST".equals(h.getRequestMethod())) {
            String key = h.getRequestURI().getPath().substring("/save/".length());
            String value = readText(h);
            System.out.println(key);

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

    private void register(HttpExchange h) throws IOException {
        System.out.println("\n/register");
        if ("GET".equals(h.getRequestMethod())) {
            sendText(h, apiToken);
        } else {
            sendResponse(h, 405, "/register ждёт GET-запрос, а получил " + h.getRequestMethod());
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
