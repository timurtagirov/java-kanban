package manager;

import model.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileBackedTaskManager extends InMemoryTaskManager {
    String fileName;
    Path path;

    public FileBackedTaskManager(String fileName) {
        this.fileName = fileName;
        this.path = Paths.get(System.getProperty("user.dir"), fileName);
    }

    public static FileBackedTaskManager loadFromFile(File file) throws ManagerSaveException, NotFoundException {
        FileBackedTaskManager taskManager = new FileBackedTaskManager(file.getName());
        boolean fileExists = true;
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
                    continue;
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
        taskManager.id = maxId;
        // добавил этот кусок, чтобы после загрузки из файла у эпика обновлялись статус и время
        for (Subtask subtask : taskManager.getSubtasks()) {
            Epic epic = (Epic) taskManager.getById(subtask.getEpicId());
            epic.getSubtasksList().add(subtask.getId());
            taskManager.historyManager.remove(epic.getId());
            taskManager.historyManager.remove(subtask.getId());
        }
        taskManager.checkAllEpics();
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

    public String getFileName() {
        return this.fileName;
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
    public void removeById(int id) throws NotFoundException {
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
