package manager;

import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeSet;

public class InMemoryTaskManager implements TaskManager {
    protected int id = 0;
    protected HashMap<Integer, Task> tasks = new HashMap<>();
    protected HashMap<Integer, Epic> epics = new HashMap<>();
    protected HashMap<Integer, Subtask> subtasks = new HashMap<>();
    protected HistoryManager historyManager = Managers.getDefaultHistory();
    protected TreeSet<Task> tasksInOrder = new TreeSet<>((task1, task2) -> {
        if (task1.getStartTime().isAfter(task2.getStartTime())) return 1;
        else if (task1.getStartTime().isBefore(task2.getStartTime())) return -1;
        else return 0;
    });

    public InMemoryTaskManager() {
    }

    @Override
    public void addTask(Task task) {
        if (isOverlap(task)) {
            System.out.println("Пересечение по времени с другим таском");
            return;
        }
        task.setId(++id);
        tasks.put(id, task);
        tasksInOrder.add(task);
    }

    @Override
    public void addEpic(Epic epic) {
        if (isOverlap(epic)) {
            System.out.println("Пересечение по времени с другим таском");
            return;
        }
        epic.setId(++id);
        epics.put(id, epic);
        checkEpicStartTimeAndDuration(epic.getId());
        checkEpicStatus(epic.getId());
        tasksInOrder.add(epic);
    }

    @Override
    public void addSubtask(Subtask subtask) {
        if (isOverlap(subtask)) {
            System.out.println("Пересечение по времени с другим таском");
            return;
        }
        if (!epics.containsKey(subtask.getEpicId())) {
            System.out.println("Эпика с таким идентификатором нет. Попробуйте ещё раз.");
            return;
        }
        subtask.setId(++id);
        if (subtask.getId() == subtask.getEpicId()) return;
        subtasks.put(id, subtask);
        epics.get(subtask.getEpicId()).getSubtasksList().add(id);
        checkEpicStatus(subtask.getEpicId());
        tasksInOrder.remove(epics.get(subtask.getEpicId()));
        checkEpicStartTimeAndDuration(subtask.getEpicId());
        tasksInOrder.add(epics.get(subtask.getEpicId()));
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
            tasksInOrder.remove(task);
        }
        tasks.clear();
    }

    @Override
    public void removeEpics() {
        for (Epic epic : epics.values()) {
            historyManager.remove(epic.getId());
            tasksInOrder.remove(epic);
        }
        epics.clear();
        for (Subtask subtask : subtasks.values()) {
            historyManager.remove(subtask.getId());
            tasksInOrder.remove(subtask);
        }
        subtasks.clear();
    }

    @Override
    public void removeSubtasks() {
        for (Subtask subtask : subtasks.values()) {
            historyManager.remove(subtask.getId());
            tasksInOrder.remove(subtask);
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
        if (getById(id) != null) tasksInOrder.remove(getById(id));
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
            checkEpicStartTimeAndDuration(epicId);
            subtasks.remove(id);
        }
        historyManager.remove(id);
    }

    @Override
    public void updateTask(Task task) {
        if (isOverlap(task)) {
            System.out.println("Пересечение по времени с другим таском");
            return;
        }
        if (!tasks.containsKey(task.getId())) {
            System.out.println("Задачи с таким идентификатором не существует.");
            return;
        }
        tasksInOrder.remove(tasks.get(task.getId()));
        tasksInOrder.add(task);
        tasks.put(task.getId(), task);
    }

    @Override
    public void updateEpic(Epic epic) {
        if (!epics.containsKey(epic.getId())) {
            System.out.println("Эпика с таким идентификатором не существует.");
            return;
        }
        tasksInOrder.remove(epics.get(epic.getId()));
        tasksInOrder.add(epic);
        epics.get(epic.getId()).setName(epic.getName());
        epics.get(epic.getId()).setDescription(epic.getDescription());
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        if (!subtasks.containsKey(subtask.getId())) {
            System.out.println("Подзадачи с таким идентификатором не существует.");
            return;
        }
        tasksInOrder.remove(subtasks.get(subtask.getId()));
        tasksInOrder.add(subtask);
        int epicId = subtasks.get(subtask.getId()).getEpicId(); // следующие 3 строки кода убираем подзадачу из старого эпика
        epics.get(epicId).getSubtasksList().remove(Integer.valueOf(subtask.getId()));
        checkEpicStatus(epicId);
        subtasks.put(subtask.getId(), subtask); // добавляем подзадачу в список подзадач
        epicId = subtasks.get(subtask.getId()).getEpicId(); // следующие 3 строки кода добавляем подзадачу в новый эпик
        epics.get(epicId).getSubtasksList().add(subtask.getId());
        checkEpicStatus(epicId);
        checkEpicStartTimeAndDuration(epicId);
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

    // Проверка времени начала и конца эпика
    private void checkEpicStartTimeAndDuration(int epicId) {
        ArrayList<Integer> subtasksList = epics.get(epicId).getSubtasksList();
        LocalDateTime startTime;
        LocalDateTime endTime;
        if (subtasksList.isEmpty()) {
            startTime = LocalDateTime.of(2099, 12, 31, 0, 0);
            endTime = LocalDateTime.of(2099, 12, 31, 0, 0);
        } else {
            startTime = subtasksList.stream()
                    .map(subtaskId -> subtasks.get(subtaskId))
                    .map(Task::getStartTime)
                    .min(LocalDateTime::compareTo)
                    .orElse(LocalDateTime.of(2099, 12, 31, 0, 0));
            endTime = subtasksList.stream()
                    .map(subtaskId -> subtasks.get(subtaskId))
                    .map(subtask -> subtask.getStartTime().plus(subtask.getDuration()))
                    .max(LocalDateTime::compareTo)
                    .orElse(LocalDateTime.of(2099, 12, 31, 0, 0));
        }
        Duration duration = Duration.between(startTime, endTime);
        epics.get(epicId).setStartTime(startTime);
        epics.get(epicId).setDuration(duration);
        epics.get(epicId).setEndTime(endTime);
    }

    public TreeSet<Task> getPrioritizedTasks() {
        return new TreeSet<>(tasksInOrder);
    }

    public boolean doTasksOverlap(Task task1, Task task2) {
        if (!task1.getStartTime().isBefore(task2.getEndTime()) || !task2.getStartTime().isBefore(task1.getEndTime())) {
            return false;
        } else {
            return true;
        }
    }

    public boolean isOverlap (Task task1) {
        return (task1.getStartTime() != null) &&
                tasksInOrder.stream().anyMatch(someTask -> doTasksOverlap(someTask, task1));
    }

    @Override
    public ArrayList<Task> getHistory() {
        return historyManager.getHistory();
    }
}