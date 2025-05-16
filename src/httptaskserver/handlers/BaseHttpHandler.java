package httptaskserver.handlers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import httptaskserver.adapters.DurationAdapter;
import httptaskserver.adapters.LocalDateTimeAdapter;
import manager.TaskManager;
import model.Epic;
import model.Subtask;
import model.Task;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;

class BaseHttpHandler {
    protected final String fileName = "tasks.csv";
    final Charset defaultcharset = StandardCharsets.UTF_8;
    protected Gson gson = createGson();
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
            exchange.getResponseBody().write(text.getBytes(defaultcharset));
            exchange.close();
        } catch (IOException e) {
            System.out.println("Something went wrong");
        }
    }

    protected void sendNotFound(HttpExchange exchange) throws IOException {
        exchange.sendResponseHeaders(404, 0);
        exchange.getResponseBody().write("A task with such id doesn't exist".getBytes(defaultcharset));
        exchange.close();
    }

    protected void sendHasInteractions(HttpExchange exchange) throws IOException {
        exchange.sendResponseHeaders(406, 0);
        exchange.getResponseBody().write("The task is overlapping with an existing task".getBytes(defaultcharset));
        exchange.close();
    }

    private Gson createGson() {
        Gson newGson = new GsonBuilder().registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .setPrettyPrinting()
                .create();
        return newGson;
    }

    public void getAllTasks(HttpExchange exchange, String method, String taskType) throws IOException {
        String text;
        switch (taskType) {
            case "task":
                text = gson.toJson(manager.getTasks());
                break;
            case "epic":
                text = gson.toJson(manager.getEpics());
                break;
            case "subtask":
                text = gson.toJson(manager.getSubtasks());
                break;
            default:
                return;
        }
        sendText(exchange, text, method);
    }

    public void getCertainTask(HttpExchange exchange, String method, String taskIdString) throws IOException {
        int id = Integer.parseInt(taskIdString);
        String text = gson.toJson(manager.getById(id));
        sendText(exchange, text, method);
    }

    public void postTask(HttpExchange exchange, String taskType) throws IOException {
        String input = new String(exchange.getRequestBody().readAllBytes(), defaultcharset);
        switch (taskType) {
            case "task":
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
                return;
            case "epic":
                Epic epic = gson.fromJson(input, Epic.class);
                if (epic.getId() == 0) {
                    manager.addEpic(epic);
                    sendText(exchange, "Epic has been successfully added", "POST");
                } else {
                    manager.updateEpic(epic);
                    sendText(exchange, "Epic has been successfully added", "POST");
                }
                return;
            case "subtask":
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
        }
    }

    public void deleteTask(HttpExchange exchange, String method, String taskIdString) throws IOException {
        int id = Integer.parseInt(taskIdString);
        manager.removeById(id);
        String text = "The task has been deleted";
        sendText(exchange, text, method);
    }


}
