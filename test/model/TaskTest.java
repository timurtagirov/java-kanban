package model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

class TaskTest {
    private final Task task = new Task("Task1", "Task details", 10, Status.IN_PROGRESS, Duration.ofMinutes(100), LocalDateTime.of(2025, 4, 23, 12, 30));

    @Test  // Проверка, что экземпляры класса Task равны друг другу, если равен их id
    public void shouldBeEqualTasksWhenSameId() {
        Task anotherTask = new Task("Task1", "Task details", 10, Status.IN_PROGRESS, Duration.ofMinutes(100), LocalDateTime.of(2025, 4, 23, 12, 30));
        assertEquals(task, anotherTask);
    }


}