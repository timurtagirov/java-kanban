public class Subtask extends Task {
    int epicId;

    public Subtask(String name, String description, Status status, int epicId) {
        super(name, description, status);
        this.epicId = epicId;
    }

    @Override
    public String toString() {
        return super.toString().replace('}', ',') +
                "epicId=" + epicId +
                '}';
    }
}