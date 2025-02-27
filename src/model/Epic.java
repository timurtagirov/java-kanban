package model;

import java.util.ArrayList;
import java.util.Arrays;

public class Epic extends Task {
    private ArrayList<Integer> subtasksList = new ArrayList<>(); // Для хранения ID подзадач и их статусов

    public Epic(String name, String description) {
        super(name, description);
    }

    public Epic(String name, String description, int id) {
        super(name, description);
        this.id = id;
    }

    public ArrayList<Integer> getSubtasksList() {
        return this.subtasksList;
    }

    public void setSubtasksList(ArrayList<Integer> newSubtasksList) {
        newSubtasksList.removeAll(Arrays.asList((Integer) this.getId())); // Эпик не может быть своей собственной подзадачей
        this.subtasksList = newSubtasksList;
    }

    @Override
    public Epic copy() {
        Epic newEpic = new Epic(this.getName(), this.getDescription(), this.getId());
        newEpic.setStatus(this.getStatus());
        newEpic.setSubtasksList(new ArrayList<>(this.getSubtasksList()));
        return newEpic;
    }
}
