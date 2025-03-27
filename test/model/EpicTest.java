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

    @Test   // Проверка, что внутри эпиков не должно оставаться неактуальных id подзадач
    public void shouldNotHaveRemovedSubtask() {
        InMemoryTaskManager taskManager = new InMemoryTaskManager();
        taskManager.addEpic(epic);
        Subtask subtaskA1 = new Subtask("Подзадача A1", "Детали подзадачи A1", 2, Status.IN_PROGRESS, 1);
        Subtask subtaskB1 = new Subtask("Подзадача B1", "Детали подзадачи B1", 3, Status.DONE, 1);
        taskManager.addSubtask(subtaskA1);
        taskManager.addSubtask(subtaskB1);
        taskManager.removeById(2);
        assertEquals(1, taskManager.getEpics().getFirst().getSubtasksList().size());
    }
}