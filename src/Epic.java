public class Epic extends Task {
    public Epic(int id, String name, String description, TaskStatus status) {
        super(id, name, description, TaskType.EPIC, status);
    }
}