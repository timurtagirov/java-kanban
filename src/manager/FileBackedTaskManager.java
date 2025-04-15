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
import java.util.ArrayList;

public class FileBackedTaskManager extends InMemoryTaskManager {
    String fileName;
    Path path;

    public FileBackedTaskManager(String fileName) {
        this.fileName = fileName;
        this.path = Paths.get(System.getProperty("user.dir"), fileName);
    }

    public static FileBackedTaskManager loadFromFile(File file) throws ManagerSaveException {
        FileBackedTaskManager taskManager = new FileBackedTaskManager(file.getName());
        ArrayList<Task> tasks = new ArrayList<>();
        ArrayList<Epic> epics = new ArrayList<>();
        ArrayList<Subtask> subtasks = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(file.getName(), StandardCharsets.UTF_8))) {
            String newTaskString;
            if (br.ready()) newTaskString = br.readLine();
            while (br.ready()) {
                newTaskString = br.readLine();
                Task task = fromString(newTaskString);
                if (task instanceof Epic epic) {
                    epics.add(epic);
                } else if (task instanceof Subtask subtask) {
                    subtasks.add(subtask);
                } else {
                    tasks.add(task);
                }
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Something is wrong");
        }
        for (Task task : tasks) {
            taskManager.addTask(task, task.getId());
        }
        for (Epic epic : epics) {
            taskManager.addEpic(epic, epic.getId());
        }
        for (Subtask subtask : subtasks) {
            taskManager.addSubtask(subtask, subtask.getId());
        }
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
            fileWriter.write("id,type,name,status,description,epic\n");
            for (Task task : tasks.values()) {
                fileWriter.write(toString(task) + "\n");
            }
            for (Epic epic : epics.values()) {
                fileWriter.write(toString(epic) + "\n");
            }
            for (Subtask subtask : subtasks.values()) {
                fileWriter.write(toString(subtask) + "\n");
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Something is wrong");
        }
    }

    public static String toString(Task task) {
        if (task instanceof Epic) {
            return Integer.toString(task.getId()) + "," + TaskTypes.EPIC + "," + task.getName() + "," +
                    task.getStatus() + "," + task.getDescription();
        } else if (task instanceof Subtask subtask) {
            return Integer.toString(subtask.getId()) + "," + TaskTypes.SUBTASK + "," + subtask.getName() + "," +
                    subtask.getStatus() + "," + subtask.getDescription() + "," + subtask.getEpicId();
        } else {
            return Integer.toString(task.getId()) + "," + TaskTypes.TASK + "," + task.getName() + "," +
                    task.getStatus() + "," + task.getDescription();
        }
    }

    public static Task fromString(String value) {
        String[] taskInfo = value.split(",", 6);
        int id = Integer.parseInt(taskInfo[0]);
        String name = taskInfo[2];
        String description = taskInfo[4];
        Status status;
        switch (taskInfo[3]) {
            case "NEW":
                status = Status.NEW;
                break;
            case "IN_PROGRESS":
                status = Status.IN_PROGRESS;
                break;
            default:
                status = Status.DONE;
        }
        if (taskInfo[1].equals("TASK")) {
            return new Task(name, description, id, status);
        } else if (taskInfo[1].equals("EPIC")) {
            return new Epic(name, description, id);
        } else {
            int epicId = Integer.parseInt(taskInfo[5]);
            return new Subtask(name, description, id, status, epicId);
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
