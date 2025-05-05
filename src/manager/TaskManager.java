package manager;

import model.Epic;
import model.Subtask;
import model.Task;
import java.util.ArrayList;
import java.util.TreeSet;

public interface TaskManager {
    void addTask(Task task);

    void addEpic(Epic epic);

    void addSubtask(Subtask subtask);

    ArrayList<Task> getTasks();

    ArrayList<Epic> getEpics();

    ArrayList<Subtask> getSubtasks();

    void removeTasks();

    void removeEpics();

    void removeSubtasks();

    Task getById(int id);

    void removeById(int id);

    void updateTask(Task task);

    void updateEpic(Epic epic);

    void updateSubtask(Subtask subtask);

    ArrayList<Task> getHistory();

    TreeSet<Task> getPrioritizedTasks();
}
