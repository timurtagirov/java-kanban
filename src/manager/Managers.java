package manager;

public class Managers {
    TaskManager taskManager = new InMemoryTaskManager();

    public static TaskManager getDefault() {
        return new FileBackedTaskManager("tasks.csv");
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
