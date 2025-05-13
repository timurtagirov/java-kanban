package httptaskserver;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import manager.TaskManager;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;

class BaseHttpHandler {
    protected final String fileName = "tasks.csv";
    final Charset defaultcharset = StandardCharsets.UTF_8;
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
}
