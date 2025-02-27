package model;

public class Subtask extends Task {
    private int epicId;

    public Subtask(String name, String description, Status status, int epicId) {
        super(name, description, status);
        if (epicId != id) {
            this.epicId = epicId;
        } else {
            System.out.println("Подзадачу нельзя добавлять саму в себя");
        }
    }

    public Subtask(String name, String description, int id, Status status, int epicId) {
        super(name, description, id, status);
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
                this.getEpicId());
        return newSubtask;
    }
}