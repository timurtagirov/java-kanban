package model;

import java.util.ArrayList;

public class Epic extends Task {
    private ArrayList<Integer> subtasksList = new ArrayList<>(); // Для хранения ID подзадач и их статусов

    public Epic(String name, String description) {
        super(name, description);
    }

    public Epic(String name, String description, int id) {
        super(name, description);
    }

    public ArrayList<Integer> getSubtasksList() {
        return this.subtasksList;
    }

    public void setSubtasksList(ArrayList<Integer> newSubtasksList) {
        this.subtasksList = newSubtasksList;
    }
}
