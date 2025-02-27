package manager;

import model.Epic;
import model.Subtask;
import model.Task;

import java.util.ArrayList;

public class InMemoryHistoryManager implements HistoryManager {
    ArrayList<Task> taskHistory = new ArrayList<>();
    public static final int MAX_HISTORY_SIZE = 10;

    @Override
    public void add(Task task) {
        if (task == null) return;
        if (task instanceof Epic epic) {
            Epic newEpic = epic.copy();
            taskHistory.add(newEpic);
            if (taskHistory.size() > MAX_HISTORY_SIZE) taskHistory.removeFirst();
            return;
        } else if (task instanceof Subtask subtask) {
            Subtask newSubtask = subtask.copy();
            taskHistory.add(newSubtask);
            if (taskHistory.size() > MAX_HISTORY_SIZE) taskHistory.removeFirst();
            return;
        } else {
            Task newTask = task.copy();
            taskHistory.add(newTask);
            if (taskHistory.size() > MAX_HISTORY_SIZE) taskHistory.removeFirst();
        }
    }

    @Override
    public ArrayList<Task> getHistory() {
        return new ArrayList<>(taskHistory);
    }
}
