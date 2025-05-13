package httptaskserver;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import manager.TaskManager;

import java.io.IOException;

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
