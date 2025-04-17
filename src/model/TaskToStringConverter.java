package model;

public class TaskToStringConverter {
    public static String taskToString(Task task) {
        if (task.getType() == TaskTypes.EPIC) {
            return Integer.toString(task.getId()) + "," + TaskTypes.EPIC + "," + task.getName() + "," +
                    task.getStatus() + "," + task.getDescription();
        } else if (task.getType() == TaskTypes.SUBTASK) {
            return Integer.toString(task.getId()) + "," + TaskTypes.SUBTASK + "," + task.getName() + "," +
                    task.getStatus() + "," + task.getDescription() + "," + ((Subtask) task).getEpicId();
        } else {
            return Integer.toString(task.getId()) + "," + TaskTypes.TASK + "," + task.getName() + "," +
                    task.getStatus() + "," + task.getDescription();
        }
    }

    public static Task fromString(String value) {
        String[] taskInfo = value.split(",", 6);
        int id = Integer.parseInt(taskInfo[0]);
        String name = taskInfo[2];
        String description = taskInfo[4];
        Status status = Status.valueOf(taskInfo[3]);
        if (TaskTypes.valueOf(taskInfo[1]) == TaskTypes.TASK) {
            return new Task(name, description, id, status);
        } else if (TaskTypes.valueOf(taskInfo[1]) == TaskTypes.EPIC) {
            return new Epic(name, description, id);
        } else {
            int epicId = Integer.parseInt(taskInfo[5]);
            return new Subtask(name, description, id, status, epicId);
        }
    }
}
