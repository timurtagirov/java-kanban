package manager;

import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;

class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {

    @Override
    FileBackedTaskManager createManager() throws IOException {
        try {
            File file = File.createTempFile("test", ".csv");
            return new FileBackedTaskManager(file.getName());
        }catch (IOException e) {
            throw new ManagerSaveException("Something is wrong");
        }
    }

    @Test
    public void shouldSaveEmpltyFile() throws IOException {
        try {
            File file = File.createTempFile("test", ".csv");
            FileBackedTaskManager manager = new FileBackedTaskManager(file.getName());
            Task task1 = new Task("Задача 1", "Детали задачи 1", Status.NEW, Duration.ofMinutes(120), LocalDateTime.of(2025, 4, 23, 15, 0));
            manager.addTask(task1);
            manager.removeById(1);
            assertTrue(file.exists());
        } catch (IOException e) {
            throw new ManagerSaveException("Something is wrong");
        }
    }

    @Test
    public void shouldLoadFromEmptyFile() throws IOException {
        try {
            File file = File.createTempFile("test", ".csv");
            FileBackedTaskManager manager = new FileBackedTaskManager(file.getName());
            Task task1 = new Task("Задача 1", "Детали задачи 1", Status.NEW, Duration.ofMinutes(120), LocalDateTime.of(2025, 4, 23, 15, 0));
            manager.addTask(task1);
            manager.removeById(1);
            FileBackedTaskManager newManager = FileBackedTaskManager.loadFromFile(file);
            assertTrue(file.exists() && newManager.getTasks().isEmpty() && newManager.getEpics().isEmpty() &&
                    newManager.getSubtasks().isEmpty());
        } catch (IOException e) {
            throw new ManagerSaveException("Something is wrong");
        }
    }

    @Test
    public void shouldSaveAndLoadSeveralTasks() throws IOException {
        try {
            File file = File.createTempFile("test", ".csv");
            FileBackedTaskManager manager = new FileBackedTaskManager(file.getName());
            Task task1 = new Task("Задача 1", "Детали задачи 1", Status.NEW, Duration.ofMinutes(100), LocalDateTime.of(2025, 4, 23, 12, 30));
            Task task2 = new Task("Задача 2", "Детали задачи 2", Status.IN_PROGRESS, Duration.ofMinutes(120), LocalDateTime.of(2025, 4, 23, 15, 0));
            Epic epicA = new Epic("Эпик A", "Детали эпика A");
            Subtask subtaskA1 = new Subtask("Подзадача A1", "Детали подзадачи A1", Status.NEW, 3, Duration.ofMinutes(20), LocalDateTime.of(2025, 4, 23, 11, 0));
            Subtask subtaskA2 = new Subtask("Подзадача A2", "Детали подзадачи A2", Status.NEW, 3, Duration.ofMinutes(30), LocalDateTime.of(2025, 4, 23, 12, 0));
            manager.addTask(task1);
            manager.addTask(task2);
            manager.addEpic(epicA);
            manager.addSubtask(subtaskA1);
            manager.addSubtask(subtaskA2);
            FileBackedTaskManager newManager = FileBackedTaskManager.loadFromFile(file);
            assertTrue(newManager.getById(1).equals(task1) && newManager.getById(2).equals(task2) &&
                    newManager.getById(3).equals(epicA) && newManager.getById(4).equals(subtaskA1) &&
                    newManager.getById(5).equals(subtaskA2));
        } catch (IOException e) {
            throw new ManagerSaveException("Something is wrong");
        }
    }

    @Test
    public void testException() {
        assertThrows(ManagerSaveException.class, () -> {//File file = File.createTempFile("test", ".csv");
            File file = new File("C:\\non-existing file.txt");
            FileBackedTaskManager manager = new FileBackedTaskManager(file.getName());
            manager.loadFromFile(file);}, "Something is wrong");
    }
}