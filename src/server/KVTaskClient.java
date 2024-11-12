package server;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class KVTaskClient {
    private String apiToken;

    public KVTaskClient(URI uri) {
        HttpRequest httpRequest = HttpRequest
                .newBuilder()
                .GET()
                .uri(uri)
                .version(HttpClient.Version.HTTP_2)
                .build();

        try (HttpClient httpClient = HttpClient.newHttpClient()) {
            HttpResponse<String> httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            apiToken = httpResponse.body();
        } catch (InterruptedException | IOException e) {
            System.out.println("Ошибка отправки запроса");
            System.out.println(e.getMessage());
        }
    }

    public void put(String key, String json) {
         HttpRequest httpRequest = HttpRequest
                .newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .uri(URI.create("http://localhost:8080/save/" + key + "?API_TOKEN=" + apiToken))
                .version(HttpClient.Version.HTTP_2)
                .build();
        try (HttpClient httpClient = HttpClient.newHttpClient()) {
            httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        } catch (InterruptedException | IOException e) {
            System.out.println("Ошибка отправки запроса: " + e);
        }
    }

    public String load(String key) {
        String json = "";
        HttpRequest httpRequest = HttpRequest
                .newBuilder()
                .GET()
                .uri(URI.create("http://localhost:8080/load/" + key + "?API_TOKEN=" + apiToken))
                .version(HttpClient.Version.HTTP_2)
                .build();
        try (HttpClient httpClient = HttpClient.newHttpClient()) {
            HttpResponse<String> httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            json = httpResponse.body();
        } catch (InterruptedException | IOException e) {
            System.out.println("Ошибка отправки запроса: " + e);
        }
        return json;
    }
}
