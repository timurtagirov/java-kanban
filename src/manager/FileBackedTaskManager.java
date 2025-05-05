package manager;

import model.*;

import java.io.*;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.nio.file.Files;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.io.BufferedReader;
import java.io.FileReader;

public class FileBackedTaskManager extends InMemoryTaskManager {
    String fileName;
    Path path;

    public FileBackedTaskManager(String fileName) {
        this.fileName = fileName;
        this.path = Paths.get(System.getProperty("user.dir"), fileName);
    }

    public static FileBackedTaskManager loadFromFile(File file) throws ManagerSaveException {
        FileBackedTaskManager taskManager = new FileBackedTaskManager(file.getName());
        int maxId = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(file.getName(), StandardCharsets.UTF_8))) {
            String newTaskString;
            if (br.ready()) newTaskString = br.readLine();
            while (br.ready()) {
                newTaskString = br.readLine();
                Task task = TaskToStringConverter.fromString(newTaskString);
                if (maxId < task.getId()) maxId = task.getId();
                if (task.getType() == TaskTypes.EPIC) {
                    taskManager.epics.put(task.getId(), (Epic) task);
                } else if (task.getType() == TaskTypes.SUBTASK) {
                    taskManager.subtasks.put(task.getId(), (Subtask) task);
                } else {
                    taskManager.tasks.put(task.getId(), task);
                }
                taskManager.tasksInOrder.add(task);
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Something is wrong");
        }
        taskManager.id = maxId + 1;
        return taskManager;
    }

    public void save() throws ManagerSaveException {
        try {
            boolean deleted = Files.deleteIfExists(path);
            Files.createFile(path);
        } catch (IOException e) {
            throw new ManagerSaveException("Something is wrong");
        }
        try (FileWriter fileWriter = new FileWriter(fileName, StandardCharsets.UTF_8, true)) {
            fileWriter.write("id,type,name,status,description,duration,startTime,epic\n");
            for (Task task : tasks.values()) {
                fileWriter.write(TaskToStringConverter.taskToString(task) + "\n");
            }
            for (Epic epic : epics.values()) {
                fileWriter.write(TaskToStringConverter.taskToString(epic) + "\n");
            }
            for (Subtask subtask : subtasks.values()) {
                fileWriter.write(TaskToStringConverter.taskToString(subtask) + "\n");
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Something is wrong");
        }
    }

    @Override
    public void addTask(Task task) {
        super.addTask(task);
        save();
    }

    @Override
    public void addEpic(Epic epic) {
        super.addEpic(epic);
        save();
    }

    @Override
    public void addSubtask(Subtask subtask) {
        super.addSubtask(subtask);
        save();
    }

    @Override
    public void removeTasks() {
        super.removeTasks();
        save();
    }

    @Override
    public void removeEpics() {
        super.removeEpics();
        save();
    }

    @Override
    public void removeSubtasks() {
        super.removeSubtasks();
        save();
    }

    @Override
    public void removeById(int id) {
        super.removeById(id);
        save();
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
    }
}
