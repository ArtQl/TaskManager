package server;

import managers.Managers;
import managers.TaskManager;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {
        KVServer kvServer = new KVServer();
        kvServer.start();
        TaskManager taskManager = Managers.getDefault();
    }

}