package HttpTaskServer;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import manager.NotFoundException;
import manager.TaskManager;
import model.Epic;

import java.io.IOException;

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
            String input = new String(exchange.getRequestBody().readAllBytes(), DEFAULTCHARSET);
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

