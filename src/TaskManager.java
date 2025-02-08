import java.util.HashMap;

public class TaskManager {
    private int id = 0;
    HashMap<Integer, Task> tasks = new HashMap<>();
    HashMap<Integer, Epic> epics = new HashMap<>();
    HashMap<Integer, Subtask> subtasks = new HashMap<>();

    public TaskManager() {
    }

    public void addTask(Task task) {
        id++;
        tasks.put(id, task);
        tasks.get(id).setId(id);
    }

    public void addEpic(Epic epic) {
        id++;
        epics.put(id, epic);
        epics.get(id).setId(id);
    }

    public void addSubtask(Subtask subtask) {
        if (!epics.containsKey(subtask.epicId)) {
            System.out.println("Эпика с таким идентификатором нет. Попробуйте ещё раз.");
            return;
        }
        id++;
        subtasks.put(id, subtask);
        subtasks.get(id).setId(id);
        epics.get(subtask.epicId).subtasksList.put(id, subtask.status);
        epics.get(subtask.epicId).checkStatus();
    }

    public void printAll() {
        System.out.println("Список задач:");
        for (Task task : tasks.values()) System.out.println(task);
        System.out.println("Список эпиков:");
        for (Epic epic : epics.values()) System.out.println(epic);
        System.out.println("Список подзадач:");
        for (Subtask subtask : subtasks.values()) System.out.println(subtask);
    }

    public void removeAll() {
        id = 0;
        tasks.clear();
        epics.clear();
        subtasks.clear();
    }

    public Object getById(int id) {
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
            for (int subtasksId : epics.get(id).subtasksList.keySet()) { //удаляем все подзадачи этого эпика
                subtasks.remove(subtasksId);
            }
            epics.remove(id);
            return;
        }
        if (subtasks.containsKey(id)) {
            subtasks.remove(id);
        }
    }

    public void updateTask(int id, Task task) {
        if (!tasks.containsKey(id)) {
            System.out.println("Задачи с таким идентификатором не существует.");
            return;
        }
        tasks.put(id, task);
        tasks.get(id).setId(id);
    }

    public void updateEpic(int id, Epic epic) {
        if (!epics.containsKey(id)) {
            System.out.println("Эпика с таким идентификатором не существует.");
            return;
        }
        epics.put(id, epic);
        epics.get(id).setId(id);
    }

    public void updateSubtask(int id, Subtask subtask) {
        if (!subtasks.containsKey(id)) {
            System.out.println("Подзадачи с таким идентификатором не существует.");
            return;
        }
        removeById(id);
        addSubtask(subtask);
    }
}