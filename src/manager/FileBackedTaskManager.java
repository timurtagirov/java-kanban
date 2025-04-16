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
                Task task = fromString(newTaskString);
                if (maxId < task.getId()) maxId = task.getId();
                if (task instanceof Epic epic) {
                    taskManager.epics.put(epic.getId(), epic);
                } else if (task instanceof Subtask subtask) {
                    taskManager.subtasks.put(subtask.getId(), subtask);
                } else {
                    taskManager.tasks.put(task.getId(), task);
                }
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
            fileWriter.write("id,type,name,status,description,epic\n");
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

    public static String taskToString(Task task) {
        if (task.getType() == TaskTypes.EPIC) {
            return Integer.toString(task.getId()) + "," + TaskTypes.EPIC + "," + task.getName() + "," +
                    task.getStatus() + "," + task.getDescription();
        } else if (task.getType() == TaskTypes.SUBTASK) {
            return Integer.toString(task.getId()) + "," + TaskTypes.SUBTASK + "," + task.getName() + "," +
                    task.getStatus() + "," + task.getDescription() + "," + ((Subtask) task).getEpicId();
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
        Status status = Status.valueOf(taskInfo[3]);
        if (TaskTypes.valueOf(taskInfo[1]) == TaskTypes.TASK) {
            return new Task(name, description, id, status);
        } else if (TaskTypes.valueOf(taskInfo[1]) == TaskTypes.EPIC) {
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
