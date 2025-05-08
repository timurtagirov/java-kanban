package model;

import manager.InMemoryTaskManager;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class SubtaskTest {
    private Subtask subtask = new Subtask("Subtask1", "Subtask description", 10, Status.DONE, 1, Duration.ofMinutes(20), LocalDateTime.of(2025, 4, 23, 16, 0));

    @Test  // Проверка, что экземпляры класса Subtask равны друг другу, если равен их id
    public void shouldBeEqualSubtasksWhenSameId() {
        Subtask anotherSubtask = new Subtask("Subtask1", "Subtask description", 10, Status.DONE, 1, Duration.ofMinutes(20), LocalDateTime.of(2025, 4, 23, 16, 0));
        assertEquals(subtask, anotherSubtask);
    }

    @Test // Проверка, что объект Subtask нельзя сделать своим же эпиком
    public void SubtasksCantBeEpicsForThemselves() {
        InMemoryTaskManager taskManager = new InMemoryTaskManager();
        Subtask subtaskA1 = new Subtask("Подзадача A1", "Детали подзадачи A1", 1, Status.NEW, 1, Duration.ofMinutes(30), LocalDateTime.of(2025, 4, 23, 16, 30));
        taskManager.addSubtask(subtaskA1);
        assertEquals(0, taskManager.getSubtasks().size());
    }
}