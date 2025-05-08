package manager;

import model.Status;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

abstract class HistoryManagerTest<T extends HistoryManager> {
    T historyManager;

    abstract T createHistoryManager();

    @BeforeEach
    public void beforeEach() {
        this.historyManager = createHistoryManager();
    }

    @Test //Проверка add(), remove() и getHistory() при пустой истории
    public void shouldReturnEmptyList() {
        historyManager.add(new Task("Задача 1", "Детали задачи 1", 1, Status.NEW,
                Duration.ofMinutes(100), LocalDateTime.of(2025, 4, 23, 12, 30)));
        historyManager.remove(1);
        historyManager.remove(1);
        assertEquals(0, historyManager.getHistory().size());
    }

    @Test  //Проверка, что при дублировании одной задачи история сохранит ее только один раз
    public void shouldReturnOnlyOneTask() {
        Task task1 = new Task("Задача 1", "Детали задачи 1", 1, Status.NEW,
                Duration.ofMinutes(100), LocalDateTime.of(2025, 4, 23, 12, 30));
        for (int i = 0; i < 5; i++) {
            historyManager.add(task1);
        }
        assertTrue(historyManager.getHistory().size() == 1 &&
                historyManager.getHistory().getFirst().equals(task1));
    }

    @Test   // Проверка, что при удалении первой задачи история отобразится правильно
    public void shouldReturnTasksWithoutFirstTask() {
        Task task1 = new Task("Задача 1", "Детали задачи 1", 1, Status.NEW,
                Duration.ofMinutes(100), LocalDateTime.of(2025, 4, 23, 12, 30));
        Task task2 = new Task("Задача 2", "Детали задачи 2", 2, Status.NEW,
                Duration.ofMinutes(100), LocalDateTime.of(2025, 4, 24, 12, 30));
        Task task3 = new Task("Задача 3", "Детали задачи 3", 3, Status.NEW,
                Duration.ofMinutes(100), LocalDateTime.of(2025, 4, 25, 12, 30));
        historyManager.add(task3);
        historyManager.add(task2);
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);
        historyManager.remove(1);
        assertTrue(historyManager.getHistory().size() == 2 &&
                historyManager.getHistory().get(0).equals(task2) &&
                historyManager.getHistory().get(1).equals(task3));
    }

    @Test   // Проверка, что при удалении последней задачи история отобразится правильно
    public void shouldReturnTasksWithoutLastTask() {
        Task task1 = new Task("Задача 1", "Детали задачи 1", 1, Status.NEW,
                Duration.ofMinutes(100), LocalDateTime.of(2025, 4, 23, 12, 30));
        Task task2 = new Task("Задача 2", "Детали задачи 2", 2, Status.NEW,
                Duration.ofMinutes(100), LocalDateTime.of(2025, 4, 24, 12, 30));
        Task task3 = new Task("Задача 3", "Детали задачи 3", 3, Status.NEW,
                Duration.ofMinutes(100), LocalDateTime.of(2025, 4, 25, 12, 30));
        historyManager.add(task3);
        historyManager.add(task2);
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);
        historyManager.remove(3);
        assertTrue(historyManager.getHistory().size() == 2 &&
                historyManager.getHistory().get(0).equals(task1) &&
                historyManager.getHistory().get(1).equals(task2));
    }

    @Test    // Проверка, что при удалении задачи посередине история отобразится правильно
    public void shouldReturnTasksWithoutMiddleTask() {
        Task task1 = new Task("Задача 1", "Детали задачи 1", 1, Status.NEW,
                Duration.ofMinutes(100), LocalDateTime.of(2025, 4, 23, 12, 30));
        Task task2 = new Task("Задача 2", "Детали задачи 2", 2, Status.NEW,
                Duration.ofMinutes(100), LocalDateTime.of(2025, 4, 24, 12, 30));
        Task task3 = new Task("Задача 3", "Детали задачи 3", 3, Status.NEW,
                Duration.ofMinutes(100), LocalDateTime.of(2025, 4, 25, 12, 30));
        historyManager.add(task3);
        historyManager.add(task2);
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);
        historyManager.remove(2);
        assertTrue(historyManager.getHistory().size() == 2 &&
                historyManager.getHistory().get(0).equals(task1) &&
                historyManager.getHistory().get(1).equals(task3));
    }
}
