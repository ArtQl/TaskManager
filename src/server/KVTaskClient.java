package server;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class KVTaskClient {
    private final HttpClient httpClient;
    private final String apiToken;
    private final String HOST;
    private final static String requestTemplate = "%s/%s/%s?API_TOKEN=%s";

    public KVTaskClient(String uri) {
        HOST = uri;
        httpClient = HttpClient.newHttpClient();
        apiToken = registerRequest();
    }

    public String getApiToken() {
        return apiToken;
    }

    private URI getURI(String endpoint, String key) {
        return URI.create(String.format(requestTemplate, HOST, endpoint, key, apiToken));
    }

    public String registerRequest() {
        HttpRequest httpRequest = HttpRequest.newBuilder().GET()
                .uri(URI.create(HOST + "/register")).build();
        return sendRequestForResponse(httpRequest, "Ошибка регистрации клиента");
    }

    public void save(String key, String json) {
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .uri(getURI("save", key)).build();
        sendRequest(httpRequest, "Ошибка сохранения данные");
    }

    public String load(String key) {
        HttpRequest httpRequest = HttpRequest.newBuilder().GET()
                .uri(getURI("load", key)).build();
        return sendRequestForResponse(httpRequest, "Ошибка при загрузке данных");
    }

    public void sendRequest(HttpRequest httpRequest, String message) {
        try {
            httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        } catch (InterruptedException | IOException e) {
            handleException(e, message);
        }
    }

    public String sendRequestForResponse(HttpRequest httpRequest, String message) {
        try {
            HttpResponse<String> httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            return httpResponse.body();
        } catch (InterruptedException | IOException e) {
            handleException(e, message);
        }
        return "";
    }

    public void handleException(Exception e, String message) {
        System.out.println(message);
        System.out.println(e.getMessage());
    }
}
