package model;

public class TaskToStringConverter {
    public static String taskToString(Task task) {
        if (task instanceof Epic) {
            return Integer.toString(task.getId()) + "," + TaskTypes.EPIC + "," + task.getName() + "," +
                    task.getStatus() + "," + task.getDescription();
        } else if (task instanceof Subtask subtask) {
            return Integer.toString(subtask.getId()) + "," + TaskTypes.SUBTASK + "," + subtask.getName() + "," +
                    subtask.getStatus() + "," + subtask.getDescription() + "," + subtask.getEpicId();
        } else {
            return Integer.toString(task.getId()) + "," + TaskTypes.TASK + "," + task.getName() + "," +
                    task.getStatus() + "," + task.getDescription();
        }
    }
}
