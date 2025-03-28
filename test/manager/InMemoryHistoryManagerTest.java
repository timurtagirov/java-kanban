package manager;

import model.Status;
import model.Task;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {
    private final InMemoryTaskManager taskManager = new InMemoryTaskManager();
    private final InMemoryHistoryManager historyManager = new InMemoryHistoryManager();

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

    @Test // Проверка, что возвращает 1 задачу 1 раз, несмотря на множество обращений к ней
    public void shouldReturnTaskOneTime() {
        Task task = new Task("Задача 1", "Детали задачи 1", 1, Status.NEW);
        taskManager.addTask(task);
        for (int i = 0; i < 12; i++) {
            taskManager.getById(1);
            int check1 = taskManager.getHistory().size();
        }
        assertEquals(1, taskManager.getHistory().size());
    }

    @Test // Проверка, что вернет таски в правильном порядке
    public void shouldReturnTasksInRightOrder() {
        taskManager.addTask(new Task("Задача 1", "Детали задачи 1", 1, Status.NEW));
        taskManager.addTask(new Task("Задача 2", "Детали задачи 2", 2, Status.NEW));
        taskManager.addTask(new Task("Задача 3", "Детали задачи 3", 3, Status.NEW));
        taskManager.getById(1);
        taskManager.getById(2);
        taskManager.getById(3);
        taskManager.getById(2);
        taskManager.getById(1);
        assertTrue(taskManager.getHistory().get(0).getId() == 3 &&
                taskManager.getHistory().get(1).getId() == 2 &&
                taskManager.getHistory().get(2).getId() == 1);
    }

    @Test  // Проверка, метода add(Task task) у InMemoryHistoryManager
    public void shouldAddTasks() {
        historyManager.add(new Task("Задача 1", "Детали задачи 1", 1, Status.NEW));
        historyManager.add(new Task("Задача 2", "Детали задачи 2", 2, Status.NEW));
        historyManager.add(new Task("Задача 3", "Детали задачи 3", 3, Status.NEW));
        historyManager.add(new Task("Задача 2", "Детали задачи 2", 2, Status.NEW));
        assertTrue(historyManager.getTasks().get(0).getId() == 1 &&
                historyManager.getTasks().get(1).getId() == 3 &&
                historyManager.getTasks().get(2).getId() == 2 &&
                historyManager.getTasks().size() == 3);
    }

    @Test  // Проверка, метода remove(int id) у InMemoryHistoryManager
    public void shouldRemoveTasks() {
        historyManager.add(new Task("Задача 1", "Детали задачи 1", 1, Status.NEW));
        historyManager.add(new Task("Задача 2", "Детали задачи 2", 2, Status.NEW));
        historyManager.add(new Task("Задача 3", "Детали задачи 3", 3, Status.NEW));
        historyManager.add(new Task("Задача 2", "Детали задачи 2", 2, Status.NEW));
        historyManager.remove(3);
        assertTrue(historyManager.getTasks().get(0).getId() == 1 &&
                historyManager.getTasks().get(1).getId() == 2 &&
                historyManager.getTasks().size() == 2);
    }

    @Test
    public void shouldRemoveTasksFromHistoryManagerAfterRemovalFromTaskManager() {
        taskManager.addTask(new Task("Задача 1", "Детали задачи 1", 1, Status.NEW));
        taskManager.addTask(new Task("Задача 2", "Детали задачи 2", 2, Status.NEW));
        taskManager.addTask(new Task("Задача 3", "Детали задачи 3", 3, Status.NEW));
        taskManager.getById(1);
        taskManager.getById(2);
        taskManager.getById(3);
        taskManager.removeById(1);
        assertTrue(taskManager.getHistory().get(0).getId() == 2 &&
                taskManager.getHistory().get(1).getId() == 3 &&
                taskManager.getHistory().size() == 2);
    }
}