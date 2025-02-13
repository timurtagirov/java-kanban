package manager;

import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;

import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {
    private int id = 0;
    HashMap<Integer, Task> tasks = new HashMap<>();
    HashMap<Integer, Epic> epics = new HashMap<>();
    HashMap<Integer, Subtask> subtasks = new HashMap<>();

    public TaskManager() {
    }

    public void addTask(Task task) {
        task.setId(++id);
        tasks.put(id, task);
    }

    public void addEpic(Epic epic) {
        epic.setId(++id);
        epics.put(id, epic);
    }

    public void addSubtask(Subtask subtask) {
        if (!epics.containsKey(subtask.getEpicId())) {
            System.out.println("Эпика с таким идентификатором нет. Попробуйте ещё раз.");
            return;
        }
        subtask.setId(++id);
        subtasks.put(id, subtask);
        epics.get(subtask.getEpicId()).getSubtasksList().add(id);
        checkEpicStatus(subtask.getEpicId());
    }

    public ArrayList<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    public ArrayList<Epic> getEpics() {
        return new ArrayList<Epic>(epics.values());
    }

    public ArrayList<Subtask> getSubtasks() {
        return new ArrayList<Subtask>(subtasks.values());
    }

    public void removeTasks() {
        tasks.clear();
    }

    public void removeEpics() {
        epics.clear();
        subtasks.clear();
    }

    public void removeSubtasks() {
        subtasks.clear();
        for (Epic epic : epics.values()) {
            epic.getSubtasksList().clear();
            epic.setStatus(Status.NEW);
        }
    }

    public Task getById(int id) {
        if (tasks.containsKey(id)) return tasks.get(id);
        if (epics.containsKey(id)) return epics.get(id);
        if (subtasks.containsKey(id)) return subtasks.get(id);
        return null;
    }

    public void removeById(int id) {
        if (tasks.containsKey(id)) {
            tasks.remove(id);
            return;
        }
        if (epics.containsKey(id)) {
            for (int subtasksId : epics.get(id).getSubtasksList()) { //удаляем все подзадачи этого эпика
                subtasks.remove(subtasksId);
            }
            epics.remove(id);
            return;
        }
        if (subtasks.containsKey(id)) {
            /*ArrayList<Integer> newSubtasksList = epics.get(subtasks.get(id).getEpicId()).getSubtasksList();
            newSubtasksList.remove(id);*/
            epics.get(subtasks.get(id).getEpicId()).getSubtasksList().remove(id);
            int epicId = subtasks.get(id).getEpicId();
            checkEpicStatus(epicId);
        }
    }

    public void updateTask(Task task) {
        if (!tasks.containsKey(task.getId())) {
            System.out.println("Задачи с таким идентификатором не существует.");
            return;
        }
        tasks.put(task.getId(), task);
    }

    public void updateEpic(Epic epic) {
        if (!epics.containsKey(epic.getId())) {
            System.out.println("Эпика с таким идентификатором не существует.");
            return;
        }
        epics.get(epic.getId()).setName(epic.getName());
        epics.get(epic.getId()).setDescription(epic.getDescription());
    }

    public void updateSubtask(Subtask subtask) {
        if (!subtasks.containsKey(subtask.getId())) {
            System.out.println("Подзадачи с таким идентификатором не существует.");
            return;
        }
        int epicId = subtasks.get(subtask.getId()).getEpicId(); // следующие 3 строки кода убираем подзадачу из старого эпика
        epics.get(epicId).getSubtasksList().remove(Integer.valueOf(subtask.getId()));
        checkEpicStatus(epicId);
        subtasks.put(subtask.getId(), subtask); // добавляем подзадачу в список подзадач
        epicId = subtasks.get(subtask.getId()).getEpicId(); // следующие 3 строки кода добавляем подзадачу в новый эпик
        epics.get(epicId).getSubtasksList().add(subtask.getId());
        checkEpicStatus(epicId);
    }

    private void checkEpicStatus(int epicId) {
        ArrayList<Integer> subtasksList = epics.get(epicId).getSubtasksList();
        if (subtasksList.isEmpty()) {
            epics.get(epicId).setStatus(Status.NEW);
            return;
        }
        int newTaskCount = 0;
        for (int subtaskId : subtasksList) {
            if (subtasks.get(subtaskId).getStatus() == Status.IN_PROGRESS) {
                epics.get(epicId).setStatus(Status.IN_PROGRESS);
                return;
            }
            if (subtasks.get(subtaskId).getStatus() == Status.NEW) newTaskCount++;
        }
        //Если дошло до этого места, значит в подзадачах не было статусов IN_PROGRESS - были только DONE и NEW
        if (newTaskCount == subtasksList.size()) {
            epics.get(epicId).setStatus(Status.NEW);
        } else if (newTaskCount == 0) {
            epics.get(epicId).setStatus(Status.DONE);
        } else {
            epics.get(epicId).setStatus(Status.IN_PROGRESS);
        }
    }
}