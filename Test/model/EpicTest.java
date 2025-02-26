package model;

import static org.junit.jupiter.api.Assertions.*;

import manager.InMemoryTaskManager;
import org.junit.jupiter.api.Test;

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
}