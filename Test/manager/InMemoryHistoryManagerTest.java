package manager;

import model.Status;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {
    private final InMemoryTaskManager taskManager = new InMemoryTaskManager();

    @Test   // Проверка, что в HistoryManager остаётся старая версия задачи даже после её обновления в менеджере
    public void shouldKeepOldVersionsOfTasks() {
        Task task = new Task("Задача 1", "Детали задачи 1", 1, Status.NEW);
        Task oldTask = new Task("Задача 1", "Детали задачи 1", 1, Status.NEW);
        taskManager.addTask(task);               // добавляем первую версию задачи
        taskManager.getById(1);                  // обращаемся к задаче, чтобы она попала в историю
        Task newTask = new Task("Новая задача", "Новые детали", 1, Status.DONE);
        taskManager.updateTask(newTask);         // обновляем задачу
        assertTrue(taskManager.getHistory().get(0).getName().equals(oldTask.getName()) &&
                        taskManager.getHistory().get(0).getDescription().equals(oldTask.getDescription()) &&
                        taskManager.getHistory().get(0).getId() == oldTask.getId() &&
                        taskManager.getHistory().get(0).getStatus().equals(oldTask.getStatus()));
    }

    @Test // Проверка, что возвращает ровно 10 задач
    public void shouldReturn10LastTasks() {
        Task task = new Task("Задача 1", "Детали задачи 1", 1, Status.NEW);
        taskManager.addTask(task);
        for (int i = 0; i < 12; i++) {
            taskManager.getById(1);
            int check1 = taskManager.getHistory().size();
        }
        assertEquals(10, taskManager.getHistory().size());
    }
}