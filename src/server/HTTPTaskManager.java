package server;

import managers.FileStorageManager;
import managers.backed.FileBackedTaskManager;

import java.io.File;
import java.net.URI;

public class HTTPTaskManager extends FileBackedTaskManager {
    KVTaskClient httpClient;

    public HTTPTaskManager(FileStorageManager fileStorageManager, URI uri) {
        super(fileStorageManager);
        this.httpClient = new KVTaskClient(uri);
    }
}
