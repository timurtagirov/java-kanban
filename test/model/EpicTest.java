package model;

import static org.junit.jupiter.api.Assertions.*;

import manager.InMemoryTaskManager;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;

class EpicTest {
    private final Epic epic = new Epic("Epic1", "Epic details", 1);

    @Test  // Проверка, что экземпляры класса Epic равны друг другу, если равен их id
    public void shouldBeEqualEpicsWhenSameId() {
        Epic anotherEpic = new Epic("Epic1", "Epic details", 1);
        assertEquals(epic, anotherEpic);
    }

    @Test   // Проверка, что объект Epic нельзя добавить в самого себя в виде подзадачи
    public void shouldNotHaveItselfAsSubtask() {
        InMemoryTaskManager taskManager = new InMemoryTaskManager();
        taskManager.addEpic(epic);
        ArrayList<Integer> newSubtasks = new ArrayList<>();
        newSubtasks.add(epic.getId());
        taskManager.getEpics().getFirst().setSubtasksList(newSubtasks);
        assertEquals(0, taskManager.getEpics().getFirst().getSubtasksList().size());
    }

    @Test   // Проверка, что внутри эпиков не должно оставаться неактуальных id подзадач
    public void shouldNotHaveRemovedSubtask() throws IOException {
        InMemoryTaskManager taskManager = new InMemoryTaskManager();
        taskManager.addEpic(epic);
        Subtask subtaskA1 = new Subtask("Подзадача A1", "Детали подзадачи A1", 2, Status.IN_PROGRESS, 1, Duration.ofMinutes(40), LocalDateTime.of(2025, 4, 23, 10, 0));
        Subtask subtaskB1 = new Subtask("Подзадача B1", "Детали подзадачи B1", 3, Status.DONE, 1, Duration.ofMinutes(20), LocalDateTime.of(2025, 4, 23, 11, 0));
        taskManager.addSubtask(subtaskA1);
        taskManager.addSubtask(subtaskB1);
        taskManager.removeById(2);
        assertEquals(1, taskManager.getEpics().getFirst().getSubtasksList().size());
    }

    @Test  // Проверка, что если все сабтаски NEW, то и эпик тоже NEW
    public void shouldHaveStatusNew() {
        InMemoryTaskManager taskManager = new InMemoryTaskManager();
        taskManager.addEpic(epic);
        Subtask subtaskA1 = new Subtask("Подзадача A1", "Детали подзадачи A1", 2, Status.NEW, 1, Duration.ofMinutes(40), LocalDateTime.of(2025, 4, 23, 10, 0));
        Subtask subtaskB1 = new Subtask("Подзадача B1", "Детали подзадачи B1", 3, Status.NEW, 1, Duration.ofMinutes(20), LocalDateTime.of(2025, 4, 23, 11, 0));
        taskManager.addSubtask(subtaskA1);
        taskManager.addSubtask(subtaskB1);
        assertEquals(Status.NEW, taskManager.getEpics().getFirst().getStatus());
    }

    @Test  // Проверка, что если все сабтаски DONE, то и эпик тоже DONE
    public void shouldHaveStatusDone() {
        InMemoryTaskManager taskManager = new InMemoryTaskManager();
        taskManager.addEpic(epic);
        Subtask subtaskA1 = new Subtask("Подзадача A1", "Детали подзадачи A1", 2, Status.DONE, 1, Duration.ofMinutes(40), LocalDateTime.of(2025, 4, 23, 10, 0));
        Subtask subtaskB1 = new Subtask("Подзадача B1", "Детали подзадачи B1", 3, Status.DONE, 1, Duration.ofMinutes(20), LocalDateTime.of(2025, 4, 23, 11, 0));
        taskManager.addSubtask(subtaskA1);
        taskManager.addSubtask(subtaskB1);
        assertEquals(Status.DONE, taskManager.getEpics().getFirst().getStatus());
    }

    @Test  // Проверка, что если сабтаски NEW и DONE, то статус эпика IN_PROGRESS
    public void shouldHaveStatusInProgress() {
        InMemoryTaskManager taskManager = new InMemoryTaskManager();
        taskManager.addEpic(epic);
        Subtask subtaskA1 = new Subtask("Подзадача A1", "Детали подзадачи A1", 2, Status.NEW, 1, Duration.ofMinutes(40), LocalDateTime.of(2025, 4, 23, 10, 0));
        Subtask subtaskB1 = new Subtask("Подзадача B1", "Детали подзадачи B1", 3, Status.DONE, 1, Duration.ofMinutes(20), LocalDateTime.of(2025, 4, 23, 11, 0));
        taskManager.addSubtask(subtaskA1);
        taskManager.addSubtask(subtaskB1);
        assertEquals(Status.IN_PROGRESS, taskManager.getEpics().getFirst().getStatus());
    }

    @Test  // Проверка, что если все сабтаски IN_PROGRESS, то и эпик тоже IN_PROGRESS
    public void shouldHaveStatusInProgressWhenSubtasksInProgress() {
        InMemoryTaskManager taskManager = new InMemoryTaskManager();
        taskManager.addEpic(epic);
        Subtask subtaskA1 = new Subtask("Подзадача A1", "Детали подзадачи A1", 2, Status.IN_PROGRESS, 1, Duration.ofMinutes(40), LocalDateTime.of(2025, 4, 23, 10, 0));
        Subtask subtaskB1 = new Subtask("Подзадача B1", "Детали подзадачи B1", 3, Status.IN_PROGRESS, 1, Duration.ofMinutes(20), LocalDateTime.of(2025, 4, 23, 11, 0));
        taskManager.addSubtask(subtaskA1);
        taskManager.addSubtask(subtaskB1);
        assertEquals(Status.IN_PROGRESS, taskManager.getEpics().getFirst().getStatus());
    }
}