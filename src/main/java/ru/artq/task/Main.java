package ru.artq.task;

import ru.artq.task.managers.Managers;
import ru.artq.task.managers.server.HttpTaskServer;
import ru.artq.task.managers.server.KVServer;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {
        KVServer kvServer = new KVServer();
        kvServer.start();
        HttpTaskServer httpTaskServer = new HttpTaskServer(Managers.getDefault());
    }

}