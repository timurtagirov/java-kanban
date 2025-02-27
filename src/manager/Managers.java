package manager;

public class Managers {
    TaskManager taskManager = new InMemoryTaskManager();

    public static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
