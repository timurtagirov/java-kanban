package model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TaskToStringConverter {
    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm");

    public static String taskToString(Task task) {
        if (task.getType() == TaskTypes.EPIC) {
            return Integer.toString(task.getId()) + "," + TaskTypes.EPIC + "," + task.getName() + "," +
                    task.getStatus() + "," + task.getDescription();
        } else if (task.getType() == TaskTypes.SUBTASK) {
            return Integer.toString(task.getId()) + "," + TaskTypes.SUBTASK + "," + task.getName() + "," +
                    task.getStatus() + "," + task.getDescription() + "," + task.getDuration().toMinutes() + "," +
                    task.getStartTime().format(formatter) + "," + ((Subtask) task).getEpicId();
        } else {
            return Integer.toString(task.getId()) + "," + TaskTypes.TASK + "," + task.getName() + "," +
                    task.getStatus() + "," + task.getDescription() + "," +
                    task.getDuration().toMinutes() + "," + task.getStartTime().format(formatter);
        }
    }

    public static Task fromString(String value) {
        String[] taskInfo = value.split(",", 8);
        int id = Integer.parseInt(taskInfo[0]);
        String name = taskInfo[2];
        String description = taskInfo[4];
        Status status = Status.valueOf(taskInfo[3]);
        if (TaskTypes.valueOf(taskInfo[1]) == TaskTypes.EPIC) {
            return new Epic(name, description, id, status);
        }
        Duration duration = Duration.ofMinutes(Integer.parseInt(taskInfo[5]));
        LocalDateTime startTime = LocalDateTime.parse(taskInfo[6], formatter);
        if (TaskTypes.valueOf(taskInfo[1]) == TaskTypes.TASK) {
            return new Task(name, description, id, status, duration, startTime);
        } else {
            int epicId = Integer.parseInt(taskInfo[7]);
            return new Subtask(name, description, id, status, epicId, duration, startTime);
        }
    }
}
