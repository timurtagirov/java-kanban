package httptaskserver.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import manager.NotFoundException;
import manager.TaskManager;

import java.io.IOException;

public class EpicHandler extends BaseHttpHandler implements HttpHandler {
    final String taskType = "epic";
    public EpicHandler(TaskManager manager) {
        super(manager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String uri = exchange.getRequestURI().getPath();
        String[] uriParts = uri.split("/");
        if (method.equals("GET") && uriParts.length == 2) {
            getAllTasks(exchange, method, taskType);
        } else if (method.equals("GET") && uriParts.length == 3) {
            try {
                getCertainTask(exchange, method, uriParts[2]);
            } catch (NumberFormatException | NotFoundException e) {
                sendNotFound(exchange);
            }
        } else if (method.equals("POST") && uriParts.length == 2) {
            postTask(exchange, taskType);
        } else if (method.equals("DELETE") && uriParts.length == 3) {
            try {
                deleteTask(exchange, method, uriParts[2]);
            } catch (NumberFormatException | NotFoundException e) {
                sendNotFound(exchange);
            }
        } else {
            sendNotFound(exchange);
        }
    }
}

