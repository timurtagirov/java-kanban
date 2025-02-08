import java.util.HashMap;

public class Epic extends Task {
    HashMap<Integer, Status> subtasksList = new HashMap<>(); // Для хранения ID подзадач и их статусов

    public Epic(String name, String description) {
        super(name, description);
    }

    public void checkStatus() {
        if (subtasksList.isEmpty()) {
            status = Status.NEW;
            return;
        }
        int doneTaskCount = 0;
        int newTaskCount = 0;
        for (Status subtasksStatus : subtasksList.values()) {
            if (subtasksStatus == Status.NEW) newTaskCount++;
            if (subtasksStatus == Status.DONE) doneTaskCount++;
        }
        if (newTaskCount == subtasksList.size()) {
            this.status = Status.NEW;
        } else if (doneTaskCount == subtasksList.size()) {
            this.status = Status.DONE;
        } else {
            this.status = Status.IN_PROGRESS;
        }
    }
}
