import manager.InMemoryTaskManager;
import manager.TaskManager;
import manager.InMemoryHistoryManager;
import manager.HistoryManager;

public class Managers {
    InMemoryTaskManager taskManager = new InMemoryTaskManager();

    public InMemoryTaskManager getDefault() {
        return taskManager;
    }

    public HistoryManager getDefaultHistory() {
        return taskManager.getHistoryManager();
    }
}
