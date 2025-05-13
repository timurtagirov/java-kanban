import HttpTaskServer.DurationAdapter;
import HttpTaskServer.HttpTaskServer;
import HttpTaskServer.LocalDateTimeAdapter;
import com.google.gson.*;
import manager.InMemoryTaskManager;
import manager.TaskManager;
import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class HttpTaskManagerSubtasksTest {
    // создаём экземпляр InMemoryTaskManager
    TaskManager manager = new InMemoryTaskManager();
    // передаём его в качестве аргумента в конструктор HttpTaskServer.HttpTaskServer
    HttpTaskServer taskServer = new HttpTaskServer(manager);
    Gson gson = new GsonBuilder().registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .setPrettyPrinting()
            .create();

    public HttpTaskManagerSubtasksTest() throws IOException {
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
    public void testAddSubtask() throws IOException, InterruptedException {
        // создаём задачу
        Epic epic = new Epic("Test 1", "Testing task 1");
        Subtask subtask = new Subtask("Test 2", "Testing task 2",
                Status.NEW, 1, Duration.ofMinutes(5), LocalDateTime.now());

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        String taskJson = gson.toJson(epic);
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());

        taskJson = gson.toJson(subtask);
        url = URI.create("http://localhost:8080/subtasks");
        request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());


        // отправляем повторно, чтобы было пересечение с имеющейся задачей и возникла ошибка 406
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(406, response.statusCode());

        // проверяем, что создалась одна задача с корректным именем
        List<Subtask> subtasksFromManager = manager.getSubtasks();

        assertNotNull(subtasksFromManager, "Задачи не возвращаются");
        assertEquals(1, subtasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Test 2", subtasksFromManager.getFirst().getName(), "Некорректное имя задачи");
    }


    @Test
    public void testGetSubtasks() throws IOException, InterruptedException {
        // создаём задачу
        Epic epic = new Epic("Test 1", "Testing task 1");
        Subtask subtask1 = new Subtask("Test 2", "Testing task 2",
                Status.NEW, 1, Duration.ofMinutes(5), LocalDateTime.of(2025, 5, 12, 12, 30));
        Task subtask2 = new Subtask("Test 3", "Testing task 3",
                Status.NEW, 1, Duration.ofMinutes(5), LocalDateTime.of(2025, 5, 13, 12, 30));
        HttpClient client = HttpClient.newHttpClient();

        URI url = URI.create("http://localhost:8080/epics");
        String taskJson = gson.toJson(epic);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());

        url = URI.create("http://localhost:8080/subtasks");
        taskJson = gson.toJson(subtask1);
        request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());

        taskJson = gson.toJson(subtask2);
        request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());

        // получаем массив задач
        request = HttpRequest.newBuilder().uri(url).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        JsonArray tasksFromManager = JsonParser.parseString(response.body()).getAsJsonArray();

        // Проверяем, что возвращает 2 задачи, добавленные раньше
        assertEquals(2, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Test 2", tasksFromManager.get(0).getAsJsonObject().get("name").getAsString(), "Некорректное имя задачи");
        assertEquals("Test 3", tasksFromManager.get(1).getAsJsonObject().get("name").getAsString(), "Некорректное имя задачи");
    }

    @Test
    public void testGetCertainSubtask() throws IOException, InterruptedException {
        // создаём задачу
        Epic epic = new Epic("Test 1", "Testing task 1");
        Subtask subtask = new Subtask("Test 2", "Testing task 2",
                Status.NEW, 1, Duration.ofMinutes(5), LocalDateTime.of(2025, 5, 12, 12, 30));
        HttpClient client = HttpClient.newHttpClient();

        URI url = URI.create("http://localhost:8080/epics");
        String taskJson = gson.toJson(epic);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        url = URI.create("http://localhost:8080/subtasks");
        taskJson = gson.toJson(subtask);
        request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        //проверяем GET запрос на несуществующий таск
        url = URI.create("http://localhost:8080/subtasks/23");
        request = HttpRequest.newBuilder().uri(url).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode());

        //проверяем GET запрос на существующий таск
        url = URI.create("http://localhost:8080/subtasks/2");
        request = HttpRequest.newBuilder().uri(url).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        JsonObject taskFromManager = JsonParser.parseString(response.body()).getAsJsonObject();
        assertEquals("Test 2", taskFromManager.get("name").getAsString(), "Некорректное имя задачи");
    }

    @Test
    public void testDeleteSubtask() throws IOException, InterruptedException {
        // создаём задачу
        Epic epic = new Epic("Test 1", "Testing task 1");
        Subtask subtask1 = new Subtask("Test 2", "Testing task 2",
                Status.NEW, 1, Duration.ofMinutes(5), LocalDateTime.of(2025, 5, 12, 12, 30));
        Task subtask2 = new Subtask("Test 3", "Testing task 3",
                Status.NEW, 1, Duration.ofMinutes(5), LocalDateTime.of(2025, 5, 13, 12, 30));
        HttpClient client = HttpClient.newHttpClient();

        URI url = URI.create("http://localhost:8080/epics");
        String taskJson = gson.toJson(epic);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());

        url = URI.create("http://localhost:8080/subtasks");
        taskJson = gson.toJson(subtask1);
        request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());

        taskJson = gson.toJson(subtask2);
        request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());

        //проверяем DELETE запрос на несуществующий таск
        url = URI.create("http://localhost:8080/subtasks/23");
        request = HttpRequest.newBuilder().uri(url).DELETE().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode());

        //проверяем GET запрос на существующий таск
        url = URI.create("http://localhost:8080/subtasks/2");
        request = HttpRequest.newBuilder().uri(url).DELETE().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        List<Subtask> tasks = manager.getSubtasks();
        assertEquals(1, tasks.size());
        assertEquals("Test 3", tasks.getFirst().getName(), "Некорректное имя задачи");
    }
}