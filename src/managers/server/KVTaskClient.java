package managers.server;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class KVTaskClient {
    private final String apiToken;
    private final String HOST;
    private final static String requestTemplate = "%s/%s/%s?API_TOKEN=%s";

    public KVTaskClient(String uri) {
        HOST = uri;
        apiToken = registerRequest();
    }

    private String getApiToken() {
        return apiToken;
    }

    private URI getURI(String endpoint, String key) {
        return URI.create(String.format(requestTemplate, HOST, endpoint, key, apiToken));
    }

    private String registerRequest() {
        HttpRequest httpRequest = HttpRequest.newBuilder().GET()
                .uri(URI.create(HOST + "/register")).build();
        return sendRequestForResponse(httpRequest, "Ошибка регистрации клиента");
    }

    public void save(String key, String json) {
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .uri(getURI("save", key)).build();
        sendRequest(httpRequest, "Ошибка сохранения данных");
    }

    public void remove(String key) {
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .DELETE().uri(getURI("delete", key)).build();
        sendRequest(httpRequest, "Ошибка удаления данных");
    }

    public String load(String key) {
        HttpRequest httpRequest = HttpRequest.newBuilder().GET()
                .uri(getURI("load", key)).build();
        return sendRequestForResponse(httpRequest, "Ошибка при загрузке данных");
    }

    private void sendRequest(HttpRequest httpRequest, String message) {
        try(HttpClient httpClient = HttpClient.newHttpClient()) {
            httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        } catch (InterruptedException | IOException e) {
            handleException(e, message);
        }
    }

    private String sendRequestForResponse(HttpRequest httpRequest, String message) {
        try(HttpClient httpClient = HttpClient.newHttpClient()) {
            HttpResponse<String> httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            if (httpResponse.statusCode() == 200) return httpResponse.body();
        } catch (InterruptedException | IOException e) {
            handleException(e, message);
        }
        return "";
    }

    private void handleException(Exception e, String message) {
        System.out.println(message);
        System.out.println(e.getMessage());
    }
}
