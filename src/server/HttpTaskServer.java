package server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import managers.Managers;
import managers.TaskManager;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

public class HttpTaskServer {
    HttpServer httpServer;
    TaskManager taskManager;

    public HttpTaskServer() {
        taskManager = Managers.getDefault();
    }

    public void createHttpServer() {
        try {
            httpServer = HttpServer.create(new InetSocketAddress(8080), 0);
            httpServer.createContext("/tasks", (exchange) -> {
                exchange.sendResponseHeaders(200, 0);
            });
            httpServer.start();
        } catch (IOException e) {
            System.out.println("Ошибка создания сервера");
        }
    }
}
