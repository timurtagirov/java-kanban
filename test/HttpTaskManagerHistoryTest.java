import httptaskserver.adapters.DurationAdapter;
import httptaskserver.HttpTaskServer;
import httptaskserver.adapters.LocalDateTimeAdapter;
import com.google.gson.*;
import manager.InMemoryTaskManager;
import manager.TaskManager;
import model.Status;
import model.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HttpTaskManagerHistoryTest {
    // создаём экземпляр InMemoryTaskManager
    TaskManager manager = new InMemoryTaskManager();
    // передаём его в качестве аргумента в конструктор HttpTaskServer.HttpTaskServer
    HttpTaskServer taskServer = new HttpTaskServer(manager);
    Gson gson = new GsonBuilder().registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .setPrettyPrinting()
            .create();

    public HttpTaskManagerHistoryTest() throws IOException {
    }

    @BeforeEach
    public void setUp() throws IOException {
        manager.removeTasks();
        manager.removeSubtasks();
        manager.removeEpics();
        taskServer.start();
    }

    @AfterEach
    public void shutDown() {
        taskServer.stop();
    }

    @Test
    public void testGetHistory() throws IOException, InterruptedException {
        // создаём задачу
        Task task1 = new Task("Test 1", "Testing task 1",
                Status.NEW, Duration.ofMinutes(5), LocalDateTime.of(2025, 5, 12, 12, 30));
        Task task2 = new Task("Test 2", "Testing task 2",
                Status.NEW, Duration.ofMinutes(5), LocalDateTime.of(2025, 5, 13, 12, 30));
        Task task3 = new Task("Test 3", "Testing task 3",
                Status.NEW, Duration.ofMinutes(5), LocalDateTime.of(2025, 5, 14, 12, 30));
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");

        String taskJson = gson.toJson(task1);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        taskJson = gson.toJson(task2);
        request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        taskJson = gson.toJson(task3);
        request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Создаём GET запросы для истории
        url = URI.create("http://localhost:8080/tasks/3");
        request = HttpRequest.newBuilder().uri(url).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        url = URI.create("http://localhost:8080/tasks/2");
        request = HttpRequest.newBuilder().uri(url).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        url = URI.create("http://localhost:8080/tasks/1");
        request = HttpRequest.newBuilder().uri(url).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        List<Task> tasksHistory = manager.getHistory();

        assertNotNull(tasksHistory, "Задачи не возвращаются");
        assertEquals(3, tasksHistory.size(), "Некорректное количество задач");
        assertEquals("Test 3", tasksHistory.getFirst().getName(), "Некорректное имя задачи");
        assertEquals("Test 2", tasksHistory.get(1).getName(), "Некорректное имя задачи");
        assertEquals("Test 1", tasksHistory.get(2).getName(), "Некорректное имя задачи");


        // проверяем историю, полученную через GET запрос
        url = URI.create("http://localhost:8080/history");
        request = HttpRequest.newBuilder().uri(url).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        JsonArray tasksFromManager = JsonParser.parseString(response.body()).getAsJsonArray();

        assertEquals(3, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Test 3", tasksFromManager.get(0).getAsJsonObject().get("name").getAsString(), "Некорректное имя задачи");
        assertEquals("Test 2", tasksFromManager.get(1).getAsJsonObject().get("name").getAsString(), "Некорректное имя задачи");
        assertEquals("Test 1", tasksFromManager.get(2).getAsJsonObject().get("name").getAsString(), "Некорректное имя задачи");
    }
}