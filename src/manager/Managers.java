package manager;

public class Managers {
    TaskManager taskManager = new InMemoryTaskManager();

    public static TaskManager getDefault() {
        return new FileBackedTaskManager("Task Manager");
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
