package manager;

import static org.junit.jupiter.api.Assertions.*;

import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;

abstract class TaskManagerTest<T extends TaskManager> {
    T manager;
    private final Epic epic = new Epic("Epic1", "Epic details", 1);
    private final Subtask subtaskA1 = new Subtask("Подзадача A1", "Детали подзадачи A1", 2, Status.IN_PROGRESS, 1, Duration.ofMinutes(40), LocalDateTime.of(2025, 4, 23, 10, 0));
    private final Subtask subtaskB1 = new Subtask("Подзадача B1", "Детали подзадачи B1", 3, Status.DONE, 1, Duration.ofMinutes(20), LocalDateTime.of(2025, 4, 23, 11, 0));

    abstract T createManager() throws IOException;

    @BeforeEach
    public void beforeEach() throws IOException {
        this.manager = createManager();
    }

    @Test  // Проверка наличия связанного эпика у подзадач
    public void shouldHaveCommonEpic() {
        System.out.println(manager.getTasks().size());
        manager.addEpic(epic);
        manager.addSubtask(subtaskA1);
        manager.addSubtask(subtaskB1);
        assertTrue(manager.getEpics().getFirst().getSubtasksList().size() == 2 &&
                manager.getEpics().getFirst().getSubtasksList().get(0) == 2 &&
                manager.getEpics().getFirst().getSubtasksList().get(1) == 3);
    }

    @Test    // Проверяем, что статусы в эпике проставляются правильно
    public void shouldHaveCorrectStatusOfEpics() {
        manager.addEpic(epic);
        manager.addSubtask(subtaskA1);
        manager.addSubtask(subtaskB1);
        Epic epic2 = new Epic("Epic2", "Epic details", 4);
        Subtask subtaskA2 = new Subtask("Подзадача A2", "Детали подзадачи A2", 5, Status.NEW, 4, Duration.ofMinutes(40), LocalDateTime.of(2025, 4, 24, 10, 0));
        Subtask subtaskB2 = new Subtask("Подзадача B2", "Детали подзадачи B2", 6, Status.NEW, 4, Duration.ofMinutes(20), LocalDateTime.of(2025, 4, 24, 11, 0));
        manager.addEpic(epic2);
        manager.addSubtask(subtaskA2);
        manager.addSubtask(subtaskB2);
        Epic epic3 = new Epic("Epic3", "Epic details", 7);
        Subtask subtaskA3 = new Subtask("Подзадача A3", "Детали подзадачи A3", 8, Status.DONE, 7, Duration.ofMinutes(40), LocalDateTime.of(2025, 4, 25, 10, 0));
        Subtask subtaskB3 = new Subtask("Подзадача B3", "Детали подзадачи B3", 9, Status.DONE, 7, Duration.ofMinutes(20), LocalDateTime.of(2025, 4, 25, 11, 0));
        manager.addEpic(epic3);
        manager.addSubtask(subtaskA3);
        manager.addSubtask(subtaskB3);
        assertTrue(manager.getById(1).getStatus() == Status.IN_PROGRESS &&
                manager.getById(4).getStatus() == Status.NEW &&
                manager.getById(7).getStatus() == Status.DONE);
    }

    @Test   // Проверяем, что время эпика считается правильно и что задачи при пересечении по времени не добавляются
    public void shouldAddOnlyThreeTasks() {
        Epic epic1 = new Epic("Epic1", "Epic details", 1);
        Subtask subtaskA1 = new Subtask("Подзадача A1", "Детали подзадачи A1", 2, Status.NEW, 1, Duration.ofMinutes(120), LocalDateTime.of(2025, 4, 25, 15, 0));
        Subtask subtaskB1 = new Subtask("Подзадача B1", "Детали подзадачи B1", 3, Status.NEW, 1, Duration.ofMinutes(120), LocalDateTime.of(2025, 4, 25, 16, 59));
        Subtask subtaskC1 = new Subtask("Подзадача C1", "Детали подзадачи C1", 4, Status.NEW, 1, Duration.ofMinutes(120), LocalDateTime.of(2025, 4, 25, 17, 0));
        Subtask subtaskD1 = new Subtask("Подзадача D1", "Детали подзадачи D1", 5, Status.NEW, 1, Duration.ofMinutes(120), LocalDateTime.of(2025, 4, 25, 13, 1));
        Subtask subtaskE1 = new Subtask("Подзадача E1", "Детали подзадачи E1", 6, Status.NEW, 1, Duration.ofMinutes(120), LocalDateTime.of(2025, 4, 25, 13, 0));
        manager.addEpic(epic1);
        manager.addSubtask(subtaskA1);
        manager.addSubtask(subtaskB1);
        manager.addSubtask(subtaskC1);
        manager.addSubtask(subtaskD1);
        manager.addSubtask(subtaskE1);
        assertTrue(manager.getEpics().getFirst().getSubtasksList().size() == 3 &&
                manager.getEpics().getFirst().getStartTime().equals(LocalDateTime.of(2025, 4, 25, 13, 0)) &&
                manager.getEpics().getFirst().getEndTime().equals(LocalDateTime.of(2025, 4, 25, 19, 0)));
    }

    @Test   // Проверяем, что getPrioritizedTask() работает правильно
    public void shouldReturnTasksInRightOrder() {
        manager.addEpic(epic);          // 10:00-11:20          23.04.2025
        manager.addSubtask(subtaskA1);  //10:00 - 10:40         23.04.2025
        manager.addSubtask(subtaskB1);  //11:00 - 11:20         23.04.2025
        Epic epic2 = new Epic("Epic2", "Epic details", 4);   // 10:00-11:20          24.04.2025
        Subtask subtaskA2 = new Subtask("Подзадача A2", "Детали подзадачи A2", 5, Status.NEW, 4, Duration.ofMinutes(40), LocalDateTime.of(2025, 4, 25, 10, 0));
        Subtask subtaskB2 = new Subtask("Подзадача B2", "Детали подзадачи B2", 6, Status.NEW, 4, Duration.ofMinutes(20), LocalDateTime.of(2025, 4, 25, 11, 0));
        manager.addEpic(epic2);
        manager.addSubtask(subtaskA2);
        manager.addSubtask(subtaskB2);
        Epic epic3 = new Epic("Epic3", "Epic details", 7);
        Subtask subtaskA3 = new Subtask("Подзадача A3", "Детали подзадачи A3", 8, Status.DONE, 7, Duration.ofMinutes(40), LocalDateTime.of(2025, 4, 24, 10, 0));
        Subtask subtaskB3 = new Subtask("Подзадача B3", "Детали подзадачи B3", 9, Status.DONE, 7, Duration.ofMinutes(20), LocalDateTime.of(2025, 4, 24, 11, 0));
        manager.addEpic(epic3);
        manager.addSubtask(subtaskA3);
        manager.addSubtask(subtaskB3);
        assertTrue(manager.getPrioritizedTasks().size() == 6 &&
                manager.getPrioritizedTasks().getFirst().equals(manager.getById(2)) &&
                manager.getPrioritizedTasks().getLast().equals(manager.getById(6)));
    }

    @Test
    public void shouldReturnThreeTasksOnlyInRightOrder() {
        Task task1 = new Task("Task 1", "1", 1, Status.NEW, Duration.ofMinutes(120), LocalDateTime.of(2025, 4, 23, 15, 0));
        // Вторая задача позже первой без пересечений
        Task task2 = new Task("Task 2", "2", 2, Status.NEW, Duration.ofMinutes(120), LocalDateTime.of(2025, 4, 23, 18, 0));
        //Третья задача раньше первой без пересечений
        Task task3 = new Task("Task 3", "3", 3, Status.NEW, Duration.ofMinutes(120), LocalDateTime.of(2025, 4, 23, 13, 0));
        //Четвертая задача заканчивается на отрезке времени третьей
        Task task4 = new Task("Task 4", "4", 4, Status.NEW, Duration.ofMinutes(120), LocalDateTime.of(2025, 4, 23, 12, 0));
        //Пятая задача начинается на отрезке времени второй
        Task task5 = new Task("Task 5", "5", 5, Status.NEW, Duration.ofMinutes(120), LocalDateTime.of(2025, 4, 23, 19, 0));
        //Шестая задача целиком находится на отрезке времени первой
        Task task6 = new Task("Task 6", "6", 6, Status.NEW, Duration.ofMinutes(60), LocalDateTime.of(2025, 4, 23, 15, 30));
        // Седьмая задача начинается раньше второй и заканчивается позже второй
        Task task7 = new Task("Task 7", "7", 7, Status.NEW, Duration.ofMinutes(240), LocalDateTime.of(2025, 4, 23, 17, 30));
        manager.addTask(task1);
        manager.addTask(task2); // должно добавиться
        manager.addTask(task3); // должно добавиться
        manager.addTask(task4); // не должно добавиться
        manager.addTask(task5); // не должно добавиться
        manager.addTask(task6); // не должно добавиться
        manager.addTask(task7); // не должно добавиться
        assertTrue(manager.getPrioritizedTasks().size() == 3 &&
                manager.getPrioritizedTasks().getFirst().equals(manager.getById(3)) &&
                manager.getPrioritizedTasks().getLast().equals(manager.getById(2)) &&
                manager.getPrioritizedTasks().get(1).equals(manager.getById(1)));
    }
}
