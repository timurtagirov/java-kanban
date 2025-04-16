package manager;

import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;

import java.util.ArrayList;
import java.util.HashMap;

public class InMemoryTaskManager implements TaskManager {
    protected int id = 0;
    protected HashMap<Integer, Task> tasks = new HashMap<>();
    protected HashMap<Integer, Epic> epics = new HashMap<>();
    protected HashMap<Integer, Subtask> subtasks = new HashMap<>();
    protected HistoryManager historyManager = Managers.getDefaultHistory();

    public InMemoryTaskManager() {
    }

    @Override
    public void addTask(Task task) {
        task.setId(++id);
        tasks.put(id, task);
    }

    @Override
    public void addEpic(Epic epic) {
        epic.setId(++id);
        epics.put(id, epic);
    }

    @Override
    public void addSubtask(Subtask subtask) {
        if (!epics.containsKey(subtask.getEpicId())) {
            System.out.println("Эпика с таким идентификатором нет. Попробуйте ещё раз.");
            return;
        }
        subtask.setId(++id);
        if (subtask.getId() == subtask.getEpicId()) return;
        subtasks.put(id, subtask);
        epics.get(subtask.getEpicId()).getSubtasksList().add(id);
        checkEpicStatus(subtask.getEpicId());
    }

    @Override
    public ArrayList<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public ArrayList<Epic> getEpics() {
        return new ArrayList<Epic>(epics.values());
    }

    @Override
    public ArrayList<Subtask> getSubtasks() {
        return new ArrayList<Subtask>(subtasks.values());
    }

    @Override
    public void removeTasks() {
        for (Task task : tasks.values()) {
            historyManager.remove(task.getId());
        }
        tasks.clear();
    }

    @Override
    public void removeEpics() {
        for (Epic epic : epics.values()) {
            historyManager.remove(epic.getId());
        }
        epics.clear();
        for (Subtask subtask : subtasks.values()) {
            historyManager.remove(subtask.getId());
        }
        subtasks.clear();
    }

    @Override
    public void removeSubtasks() {
        for (Subtask subtask : subtasks.values()) {
            historyManager.remove(subtask.getId());
        }
        subtasks.clear();
        for (Epic epic : epics.values()) {
            epic.getSubtasksList().clear();
            epic.setStatus(Status.NEW);
        }
    }

    @Override
    public Task getById(int id) {
        if (tasks.containsKey(id)) {
            historyManager.add(tasks.get(id));
            return tasks.get(id);
        }
        if (epics.containsKey(id)) {
            historyManager.add(epics.get(id));
            return epics.get(id);
        }
        if (subtasks.containsKey(id)) {
            historyManager.add(subtasks.get(id));
            return subtasks.get(id);
        }
        return null;
    }

    @Override
    public void removeById(int id) {
        if (tasks.containsKey(id)) {
            tasks.remove(id);
        } else if (epics.containsKey(id)) {
            for (int subtasksId : epics.get(id).getSubtasksList()) { //удаляем все подзадачи этого эпика
                subtasks.remove(subtasksId);
                historyManager.remove(subtasksId);
            }
            epics.remove(id);
        } else if (subtasks.containsKey(id)) {
            int epicId = subtasks.get(id).getEpicId();
            epics.get(epicId).getSubtasksList().remove(Integer.valueOf(id));
            checkEpicStatus(epicId);
            subtasks.remove(id);
        }
        historyManager.remove(id);
    }

    @Override
    public void updateTask(Task task) {
        if (!tasks.containsKey(task.getId())) {
            System.out.println("Задачи с таким идентификатором не существует.");
            return;
        }
        tasks.put(task.getId(), task);
    }

    @Override
    public void updateEpic(Epic epic) {
        if (!epics.containsKey(epic.getId())) {
            System.out.println("Эпика с таким идентификатором не существует.");
            return;
        }
        epics.get(epic.getId()).setName(epic.getName());
        epics.get(epic.getId()).setDescription(epic.getDescription());
    }

    @Override
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

    @Override
    public ArrayList<Task> getHistory() {
        return historyManager.getHistory();
    }
}