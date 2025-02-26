package manager;

import model.Status;
import model.Task;
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
        assertTrue(taskManager.getHistoryManager().getHistory().get(0).getName().equals(oldTask.getName()) &&
                        taskManager.getHistoryManager().getHistory().get(0).getDescription().equals(oldTask.getDescription()) &&
                        taskManager.getHistoryManager().getHistory().get(0).getId() == oldTask.getId() &&
                        taskManager.getHistoryManager().getHistory().get(0).getStatus().equals(oldTask.getStatus()));
    }

}