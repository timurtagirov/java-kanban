package model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class TaskTest {
    private final Task task = new Task("Task1", "Task details", 10, Status.IN_PROGRESS);

    @Test  // Проверка, что экземпляры класса Task равны друг другу, если равен их id
    public void shouldBeEqualTasksWhenSameId() {
        Task anotherTask = new Task("Task1", "Task details", 10, Status.IN_PROGRESS);
        assertEquals(task, anotherTask);
    }


}