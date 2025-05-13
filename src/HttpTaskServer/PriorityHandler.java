package HttpTaskServer;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import manager.TaskManager;

import java.io.IOException;

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
