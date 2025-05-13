package manager;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {

    private final InMemoryTaskManager taskManager = new InMemoryTaskManager();

    @Override
    InMemoryTaskManager createManager() {
        return new InMemoryTaskManager();
    }

    @BeforeEach  // очищаем список задач перед новым тестом
    public void clearAll() {
        taskManager.removeTasks();
        taskManager.removeEpics();
        taskManager.removeSubtasks();
    }

    @Test      // Проверка, что InMemoryTaskManager действительно добавляет задачи разного типа и может найти их по id
    public void shouldAddAndGetTasks()  throws IOException {
        Task task1 = new Task("Задача 1", "Детали задачи 1", 1, Status.NEW, Duration.ofMinutes(120), LocalDateTime.of(2025, 4, 23, 15, 0));
        Epic epicA = new Epic("Эпик A", "Детали эпика A", 2);
        Subtask subtaskA1 = new Subtask("Подзадача A1", "Детали подзадачи A1", 3, Status.NEW, 2, Duration.ofMinutes(100), LocalDateTime.of(2025, 4, 23, 12, 30));
        taskManager.addTask(task1);
        taskManager.addEpic(epicA);
        taskManager.addSubtask(subtaskA1);
        assertEquals(task1, taskManager.getById(1));
        assertEquals(epicA, taskManager.getById(2));
        assertEquals(subtaskA1, taskManager.getById(3));
    }

    @Test   // Проверка, что задачи с заданным id и сгенерированным id не конфликтуют внутри менеджера
    public void shouldChangeIdWhenSuchIdAlreadyExists() {
        Task task1 = new Task("Задача 1", "Детали задачи 1", 1, Status.NEW, Duration.ofMinutes(100), LocalDateTime.of(2025, 4, 23, 12, 30));
        Task newTask = new Task("Новая задача", "Новые детали задачи", 1, Status.NEW, Duration.ofMinutes(100), LocalDateTime.of(2025, 4, 23, 14, 30));
        Epic epicA = new Epic("Эпик A", "Детали эпика A", 1);
        taskManager.addTask(task1);
        taskManager.addTask(newTask);
        taskManager.addEpic(epicA);
        assertNotEquals(taskManager.getTasks().get(0).getId(), taskManager.getTasks().get(1).getId());
        assertNotEquals(taskManager.getTasks().get(0).getId(), taskManager.getEpics().getFirst().getId());
    }

    @Test   // Проверка, что после добавления задачи в менеджер, ее поля не меняются
    // (кроме списка подзадач для эпика, он должен меняться при добавлении сабтаска, поэтому это поле не проверяем)
    public void shouldNotChangeTasksAfterAddingToTaskManager()  throws IOException {
        Task task1 = new Task("Задача 1", "Детали задачи 1", 1, Status.NEW, Duration.ofMinutes(100), LocalDateTime.of(2025, 4, 23, 12, 30));
        Epic epicA = new Epic("Эпик A", "Детали эпика A", 2);
        Subtask subtaskA1 = new Subtask("Подзадача A1", "Детали подзадачи A1", 3, Status.NEW, 2, Duration.ofMinutes(20), LocalDateTime.of(2025, 4, 23, 16, 0));
        taskManager.addTask(task1);
        taskManager.addEpic(epicA);
        taskManager.addSubtask(subtaskA1);
        Task task2 = new Task("Задача 1", "Детали задачи 1", 1, Status.NEW, Duration.ofMinutes(100), LocalDateTime.of(2025, 4, 23, 12, 30));
        Epic epicB = new Epic("Эпик A", "Детали эпика A", 2);
        Subtask subtaskA2 = new Subtask("Подзадача A1", "Детали подзадачи A1", 3, Status.NEW, 2, Duration.ofMinutes(20), LocalDateTime.of(2025, 4, 23, 16, 0));
        assertTrue(equalsByFields(task2, taskManager.getById(1)));
        assertTrue(equalsByFields(epicB, taskManager.getById(2)));
        int check = taskManager.getSubtasks().size();
        assertTrue(equalsByFields(subtaskA2, taskManager.getById(3)));
    }


    // Метод для сравнения двух тасков по всем полям (обычный equals у нас проверяет только по id)
    public static <T extends Task, V extends Task> boolean equalsByFields(T task1, V task2) {
        if (task1 == null || task2 == null) return false;
        if (task1.getClass() != task2.getClass()) return false;
        if (task1 instanceof Subtask subtask1 && task2 instanceof Subtask subtask2) {
            if (subtask1.getEpicId() != subtask2.getEpicId()) return false;
        }
        return (task1.getName().equals(task2.getName()) &&
                (task1.getDescription().equals(task2.getDescription())) &&
                (task1.getId() == task2.getId()) &&
                (task1.getStatus().equals(task2.getStatus())));
    }
}

