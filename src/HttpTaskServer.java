import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import manager.*;
import model.Epic;
import model.Subtask;
import model.Task;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class HttpTaskServer {
    private static final int PORT = 8080;
    private static final FileBackedTaskManager taskManager = (FileBackedTaskManager) Managers.getDefault();
    private static final String fileName = "tasks.csv";
    static File file = new File(fileName);
    static TaskManager manager;
    static HttpServer server;

    public HttpTaskServer(TaskManager manager) throws NotFoundException {
        this.manager = manager;
    }

    public void start() throws IOException {
        try {
            server = HttpServer.create();
            server.bind(new InetSocketAddress(PORT), 0);

            server.createContext("/tasks", new TaskHandler(manager));
            server.createContext("/epics", new EpicHandler(manager));
            server.createContext("/subtasks", new SubtaskHandler(manager));
            server.createContext("/history", new HistoryHandler(manager));
            server.createContext("/priority", new PriorityHandler(manager));
            server.start();
        } catch (IOException e) {
            System.out.println("something went wrong");
        }
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

class LocalDateTimeAdapter extends TypeAdapter<LocalDateTime> {
    private static final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @Override
    public void write(final JsonWriter jw, final LocalDateTime dateTime) throws IOException {
        if (dateTime == null) {
            jw.value(LocalDateTime.now().format(dtf));
        } else {
            jw.value(dateTime.format(dtf));
        }
    }

    @Override
    public LocalDateTime read(final JsonReader jr) throws IOException {
        return LocalDateTime.parse(jr.nextString(), dtf);
    }
}

class DurationAdapter extends TypeAdapter<Duration> {
    @Override
    public void write(final JsonWriter jw, final Duration duration) throws IOException {
        if (duration == null) {
            jw.value(Duration.ofMinutes(0).toString());
        } else {
            jw.value(duration.toString());
        }
    }

    @Override
    public Duration read(final JsonReader jr) throws IOException {
        return Duration.parse(jr.nextString());
    }
}

class TaskHandler extends BaseHttpHandler implements HttpHandler {
    public TaskHandler(TaskManager manager) {
        super(manager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String uri = exchange.getRequestURI().getPath();
        String[] uriParts = uri.split("/");
        if (method.equals("GET") && uriParts.length == 2) {
            String text = gson.toJson(manager.getTasks());
            sendText(exchange, text, method);
        } else if (method.equals("GET") && uriParts.length == 3) {
            try {
                int id = Integer.parseInt(uriParts[2]);
                String text = gson.toJson(manager.getById(id));
                sendText(exchange, text, method);
            } catch (NumberFormatException | NotFoundException e) {
                sendNotFound(exchange);
            }
        } else if (method.equals("POST") && uriParts.length == 2) {
            String input = new String(exchange.getRequestBody().readAllBytes(), DEFAULT_CHARSET);
            Task task = gson.fromJson(input, Task.class);
            if (manager.isOverlap(task)) {
                sendHasInteractions(exchange);
                return;
            }
            if (task.getId() == 0) {
                manager.addTask(task);
                sendText(exchange, "Task has been successfully added", "POST");
            } else {
                manager.updateTask(task);
                sendText(exchange, "Task has been successfully added", "POST");
            }
        } else if (method.equals("DELETE") && uriParts.length == 3) {
            try {
                int id = Integer.parseInt(uriParts[2]);
                manager.removeById(id);
                String text = "The task has been deleted";
                sendText(exchange, text, method);
            } catch (NumberFormatException | NotFoundException e) {
                sendNotFound(exchange);
            }
        } else {
            sendNotFound(exchange);
        }
    }
}


class EpicHandler extends BaseHttpHandler implements HttpHandler {
    public EpicHandler(TaskManager manager) {
        super(manager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String uri = exchange.getRequestURI().getPath();
        String[] uriParts = uri.split("/");
        if (method.equals("GET") && uriParts.length == 2) {
            String text = gson.toJson(manager.getEpics());
            sendText(exchange, text, method);
        } else if (method.equals("GET") && uriParts.length == 3) {
            try {
                int id = Integer.parseInt(uriParts[2]);
                String text = gson.toJson(manager.getById(id));
                sendText(exchange, text, method);
            } catch (NumberFormatException | NotFoundException e) {
                sendNotFound(exchange);
            }
        } else if (method.equals("POST") && uriParts.length == 2) {
            String input = new String(exchange.getRequestBody().readAllBytes(), DEFAULT_CHARSET);
            Epic epic = gson.fromJson(input, Epic.class);
            if (epic.getId() == 0) {
                manager.addEpic(epic);
                sendText(exchange, "Epic has been successfully added", "POST");
            } else {
                manager.updateEpic(epic);
                sendText(exchange, "Epic has been successfully added", "POST");
            }
        } else if (method.equals("DELETE") && uriParts.length == 3) {
            try {
                int id = Integer.parseInt(uriParts[2]);
                manager.removeById(id);
                String text = "The epic has been deleted";
                sendText(exchange, text, method);
            } catch (NumberFormatException | NotFoundException e) {
                sendNotFound(exchange);
            }
        } else {
            sendNotFound(exchange);
        }
    }
}


class SubtaskHandler extends BaseHttpHandler implements HttpHandler {
    public SubtaskHandler(TaskManager manager) {
        super(manager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String uri = exchange.getRequestURI().getPath();
        String[] uriParts = uri.split("/");
        if (method.equals("GET") && uriParts.length == 2) {
            String text = gson.toJson(manager.getSubtasks());
            sendText(exchange, text, method);
        } else if (method.equals("GET") && uriParts.length == 3) {
            try {
                int id = Integer.parseInt(uriParts[2]);
                String text = gson.toJson(manager.getById(id));
                sendText(exchange, text, method);
            } catch (NumberFormatException | NotFoundException e) {
                sendNotFound(exchange);
            }
        } else if (method.equals("POST") && uriParts.length == 2) {
            String input = new String(exchange.getRequestBody().readAllBytes(), DEFAULT_CHARSET);
            Subtask subtask = gson.fromJson(input, Subtask.class);
            if (manager.isOverlap(subtask)) {
                sendHasInteractions(exchange);
                return;
            }
            if (subtask.getId() == 0) {
                manager.addSubtask(subtask);
                sendText(exchange, "Task has been successfully added", "POST");
            } else {
                manager.updateSubtask(subtask);
                sendText(exchange, "Task has been successfully added", "POST");
            }
        } else if (method.equals("DELETE") && uriParts.length == 3) {
            try {
                int id = Integer.parseInt(uriParts[2]);
                manager.removeById(id);
                String text = "The task has been deleted";
                sendText(exchange, text, method);
            } catch (NumberFormatException | NotFoundException e) {
                sendNotFound(exchange);
            }
        } else {
            sendNotFound(exchange);
        }
    }
}


class HistoryHandler extends BaseHttpHandler implements HttpHandler {
    public HistoryHandler(TaskManager manager) {
        super(manager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String uri = exchange.getRequestURI().getPath();
        String[] uriParts = uri.split("/");
        if (method.equals("GET") && uriParts.length == 2) {
            String text = gson.toJson(manager.getHistory());
            sendText(exchange, text, method);
        } else {
            sendNotFound(exchange);
        }
    }
}


class PriorityHandler extends BaseHttpHandler implements HttpHandler {
    public PriorityHandler(TaskManager manager) {
        super(manager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String uri = exchange.getRequestURI().getPath();
        String[] uriParts = uri.split("/");
        if (method.equals("GET") && uriParts.length == 2) {
            String text = gson.toJson(manager.getPrioritizedTasks());
            sendText(exchange, text, method);
        } else {
            sendNotFound(exchange);
        }
    }
}



class BaseHttpHandler {
    protected final String fileName = "tasks.csv";
    final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    protected Gson gson = new GsonBuilder().registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .setPrettyPrinting()
            .create();
    protected TaskManager manager;

    public BaseHttpHandler(TaskManager manager) {
        this.manager = manager;
    }

    protected void sendText(HttpExchange exchange, String text, String method) throws IOException {
        try {
            exchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
            int rCode;
            if (method.equals("POST")) {
                rCode = 201;
            } else {
                rCode = 200;
            }
            exchange.sendResponseHeaders(rCode, 0);
            exchange.getResponseBody().write(text.getBytes(DEFAULT_CHARSET));
            exchange.close();
        } catch (IOException e) {
            System.out.println("Something went wrong");
        }
    }

    protected void sendNotFound(HttpExchange exchange) throws IOException {
        exchange.sendResponseHeaders(404, 0);
        exchange.getResponseBody().write("A task with such id doesn't exist".getBytes(DEFAULT_CHARSET));
        exchange.close();
    }

    protected void sendHasInteractions(HttpExchange exchange) throws IOException {
        exchange.sendResponseHeaders(406, 0);
        exchange.getResponseBody().write("The task is overlapping with an existing task".getBytes(DEFAULT_CHARSET));
        exchange.close();
    }
}

/*
InMemoryTaskManager taskManager = new InMemoryTaskManager();
        Task task1 = new Task("Задача 1", "Детали задачи 1", Status.NEW, Duration.ofMinutes(100), LocalDateTime.of(2025, 4, 23, 12, 30));
        Task task2 = new Task("Задача 2", "Детали задачи 2", Status.IN_PROGRESS, Duration.ofMinutes(120), LocalDateTime.of(2025, 4, 23, 15, 0));
        Epic epicA = new Epic("Эпик A", "Детали эпика A");
        Subtask subtaskA1 = new Subtask("Подзадача A1", "Детали подзадачи A1", Status.NEW, 3, Duration.ofMinutes(20), LocalDateTime.of(2025, 4, 23, 16, 0));
        Subtask subtaskA2 = new Subtask("Подзадача A2", "Детали подзадачи A2", Status.NEW, 3, Duration.ofMinutes(30), LocalDateTime.of(2025, 4, 23, 16, 30));
        Epic epicB = new Epic("Эпик B", "Детали эпика B", 6);
        Subtask subtaskB1 = new Subtask("Подзадача B1", "Детали подзадачи B1", Status.IN_PROGRESS, 6, Duration.ofMinutes(50), LocalDateTime.of(2025, 4, 23, 17, 0));

        taskManager.addTask(task1);
        taskManager.addTask(task2);
        taskManager.addEpic(epicA);
        taskManager.addSubtask(subtaskA1);
        taskManager.addSubtask(subtaskA2);
        taskManager.addEpic(epicB);
        taskManager.addSubtask(subtaskB1);

        System.out.println('\n' + "Проверка printTasks(), printEpics(), printSubtasks() и что все создалось правильно");
        System.out.println("Список задач:");
        System.out.println(taskManager.getTasks());
        System.out.println("Список эпиков:");
        System.out.println(taskManager.getEpics());
        System.out.println("Список подзадач:");
        System.out.println(taskManager.getSubtasks());
        System.out.println();

        System.out.println("Проверка getById() и что всё печатается корректно");
        for (int i = 1; i <= 7; i++) {
            System.out.println(taskManager.getById(i));
        }
        System.out.println("Вызовем ещё раз некоторые таски, чтобы проверить в конце InMemoryHistoryManager");
        System.out.println(taskManager.getById(5));
        System.out.println(taskManager.getById(3));
        System.out.println(taskManager.getById(1));
        System.out.println();

        System.out.println("Проверка updateTask(), updateEpic(), updateSubtask(), " +
                "и как обновляются статусы в эпике от обновления подзадач");
        task1 = new Task("Задача 1", "Детали задачи 1", 1, Status.IN_PROGRESS, Duration.ofMinutes(40), LocalDateTime.of(2025, 4, 23, 10, 0));
        epicB = new Epic("Эпик B", "Новые детали эпика B", 6);
        subtaskA1 = new Subtask("Подзадача A1", "Детали подзадачи A1", 4, Status.IN_PROGRESS, 3, Duration.ofMinutes(20), LocalDateTime.of(2025, 4, 23, 11, 0));
        subtaskB1 = new Subtask("Подзадача B1", "Детали подзадачи B1", 7, Status.DONE, 6, Duration.ofMinutes(30), LocalDateTime.of(2025, 4, 23, 12, 0));

        taskManager.updateTask(task1);
        taskManager.updateEpic(epicB);
        taskManager.updateSubtask(subtaskA1);
        taskManager.updateSubtask(subtaskB1);
        System.out.println("Список задач:");
        System.out.println(taskManager.getTasks());
        System.out.println("Список эпиков:");
        System.out.println(taskManager.getEpics());
        System.out.println("Список подзадач:");
        System.out.println(taskManager.getSubtasks());
        System.out.println();

        System.out.println("Проверка removeById(), и что от удаления эпика удаляются также и подзадачи");
        taskManager.removeById(4);
        taskManager.removeById(6);
        System.out.println("Список задач:");
        System.out.println(taskManager.getTasks());
        System.out.println("Список эпиков:");
        System.out.println(taskManager.getEpics());
        System.out.println("Список подзадач:");
        System.out.println(taskManager.getSubtasks());
        System.out.println();

        System.out.println("Проверка removeSubtasks()");
        taskManager.removeSubtasks();
        System.out.println("Список задач:");
        System.out.println(taskManager.getTasks());
        System.out.println("Список эпиков:");
        System.out.println(taskManager.getEpics());
        System.out.println("Список подзадач:");
        System.out.println(taskManager.getSubtasks());

        System.out.println(taskManager.getHistory());

        FileBackedTaskManager taskManager1 = (FileBackedTaskManager) Managers.getDefault();
        taskManager1.addTask(task1);
        taskManager1.addTask(task2);
        taskManager1.addEpic(epicA);
        taskManager1.addSubtask(subtaskA1);
        taskManager1.addSubtask(subtaskA2);
        taskManager1.addEpic(epicB);
        taskManager1.addSubtask(subtaskB1);

        System.out.println(TaskToStringConverter.taskToString(task1));
        System.out.println(TaskToStringConverter.taskToString(epicA));
        System.out.println(TaskToStringConverter.taskToString(subtaskA1));
        System.out.println(TaskToStringConverter.taskToString(TaskToStringConverter.fromString("1,TASK,Задача 1,IN_PROGRESS,Детали задачи 1,40,2025.04.23 10:00")));
        System.out.println(TaskToStringConverter.taskToString(TaskToStringConverter.fromString("3,EPIC,Эпик A,IN_PROGRESS,Детали эпика A,20,2025.04.23 11:00")));
        System.out.println(TaskToStringConverter.taskToString(TaskToStringConverter.fromString("4,SUBTASK,Подзадача A1,IN_PROGRESS,Детали подзадачи A1,20,2025.04.23 11:00,3")));
        System.out.println(System.getProperty("user.dir"));

        FileBackedTaskManager taskManager2 = FileBackedTaskManager.loadFromFile(new File("C:\\Users\\Lenovo\\IdeaProjects\\Sprint4\\java-kanban\\tasks.csv"));
        System.out.println(TaskToStringConverter.taskToString(task1));
        System.out.println(TaskToStringConverter.taskToString(epicA));
        System.out.println(TaskToStringConverter.taskToString(subtaskA1));
 */