package httptaskserver;

import com.sun.net.httpserver.HttpServer;
import httptaskserver.handlers.*;
import manager.*;
import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;

public class HttpTaskServer {
    private static final int PORT = 8080;
    private static final FileBackedTaskManager taskManager = (FileBackedTaskManager) Managers.getDefault();
    private static final String fileName = "tasks.csv";
    static File file = new File(fileName);
    static TaskManager manager;
    static HttpServer server;

    public HttpTaskServer(TaskManager manager) throws IOException {
        this.manager = manager;

        server = HttpServer.create();
        server.bind(new InetSocketAddress(PORT), 0);

        server.createContext("/tasks", new TaskHandler(manager));
        server.createContext("/epics", new EpicHandler(manager));
        server.createContext("/subtasks", new SubtaskHandler(manager));
        server.createContext("/history", new HistoryHandler(manager));
        server.createContext("/priority", new PriorityHandler(manager));
    }

    public void start() throws IOException {
            server.start();
    }

    public void stop() {
        server.stop(0);
    }

    public static void main(String[] args) throws IOException {
        try {
            if (file.exists()) {
                manager = FileBackedTaskManager.loadFromFile(file);
            } else {
                manager = (FileBackedTaskManager) Managers.getDefault();
            }
        } catch (NotFoundException e) {
            System.out.println("Something is wrong");;
        }
        server = HttpServer.create();
        server.bind(new InetSocketAddress(PORT), 0);

        server.createContext("/tasks", new TaskHandler(manager));
        server.createContext("/epics", new EpicHandler(manager));
        server.createContext("/subtasks", new SubtaskHandler(manager));
        server.createContext("/history", new HistoryHandler(manager));
        server.createContext("/priority", new PriorityHandler(manager));
        server.start();
    }
}




