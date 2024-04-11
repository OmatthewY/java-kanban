import client.KVTaskClient;
import exceptions.ManagerSaveException;
import server.HttpTaskServer;
import server.KVServer;

import java.io.IOException;
import java.net.URI;

public class Main {
    public static void main(String[] args) throws ManagerSaveException, IOException, InterruptedException {
        KVServer kvServer = new KVServer();
        kvServer.stop();
        HttpTaskServer httpTaskServer = new HttpTaskServer();
        httpTaskServer.stop();
        KVTaskClient kvTaskClient = new KVTaskClient(URI.create("http://localhost:8078"));
    }
}