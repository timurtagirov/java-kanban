package manager;

import model.Epic;
import model.Subtask;
import model.Task;

import java.util.ArrayList;

public class InMemoryHistoryManager implements HistoryManager {
    ArrayList<Task> taskHistory = new ArrayList<>();

    @Override
    public void add(Task task) {
        if (task instanceof Epic epic) {
            Epic newEpic = new Epic(epic.getName(), epic.getDescription(), epic.getId());
            newEpic.setStatus(epic.getStatus());
            newEpic.setSubtasksList(new ArrayList<>(epic.getSubtasksList()));
            taskHistory.add(newEpic);
            if (taskHistory.size() == 11) taskHistory.removeFirst();
            return;
        } else if (task instanceof Subtask subtask) {
            Subtask newSubtask = new Subtask(subtask.getName(), subtask.getDescription(), subtask.getId(),
                    subtask.getStatus(), subtask.getEpicId());
            taskHistory.add(newSubtask);
            if (taskHistory.size() == 11) taskHistory.removeFirst();
            return;
        } else {
            taskHistory.add(task);
            if (taskHistory.size() == 11) taskHistory.removeFirst();
        }
    }

    @Override
    public ArrayList<Task> getHistory() {
        return taskHistory;
    }
}
