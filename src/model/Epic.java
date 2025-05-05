package model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;

public class Epic extends Task {
    private ArrayList<Integer> subtasksList = new ArrayList<>(); // Для хранения ID подзадач и их статусов
    LocalDateTime endTime;

    public Epic(String name, String description) {
        super(name, description);
    }

    public Epic(String name, String description, int id) {
        super(name, description);
        this.id = id;
    }

    public Epic(String name, String description, int id, Status status, Duration duration, LocalDateTime startTime) {
        super(name, description, status, duration, startTime);
        this.id = id;
    }

    public ArrayList<Integer> getSubtasksList() {
        return this.subtasksList;
    }

    public void setSubtasksList(ArrayList<Integer> newSubtasksList) {
        newSubtasksList.removeAll(Arrays.asList((Integer) this.getId())); // Эпик не может быть своей собственной подзадачей
        this.subtasksList = newSubtasksList;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    @Override
    public Epic copy() {
        Epic newEpic = new Epic(this.getName(), this.getDescription(), this.getId());
        newEpic.setStatus(this.getStatus());
        newEpic.setSubtasksList(new ArrayList<>(this.getSubtasksList()));
        return newEpic;
    }

    @Override
    public TaskTypes getType() {
        return TaskTypes.EPIC;
    }
}
