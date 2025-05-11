package model;

import java.time.Duration;
import java.time.LocalDateTime;

public class Subtask extends Task {
    private int epicId;

    public Subtask(String name, String description, Status status, int epicId, Duration duration, LocalDateTime startTime) {
        super(name, description, status, duration, startTime);
        if (epicId != id) {
            this.epicId = epicId;
        } else {
            System.out.println("Подзадачу нельзя добавлять саму в себя");
        }
    }

    public Subtask(String name, String description, int id, Status status, int epicId, Duration duration, LocalDateTime startTime) {
        super(name, description, id, status, duration, startTime);
        if (epicId != id) this.epicId = epicId;
    }

    @Override
    public String toString() {
        return super.toString().replace('}', ',') +
                "epicId=" + epicId +
                '}';
    }

    public int getEpicId() {
        return this.epicId;
    }

    public void setEpicId(int epicId) {
        if (id != epicId) this.epicId = epicId;
    }

    @Override
    public Subtask copy() {
        Subtask newSubtask = new Subtask(this.getName(), this.getDescription(), this.getId(), this.getStatus(),
                this.getEpicId(), this.getDuration(), this.getStartTime());
        return newSubtask;
    }

    @Override
    public TaskTypes getType() {
        return TaskTypes.SUBTASK;
    }
}