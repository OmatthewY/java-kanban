public class Task {
    private int id;
    private String name;
    private String description;
    private TaskType type;
    private TaskStatus status;

    public Task(int id, String name, String description, TaskType type, TaskStatus status) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.type = type;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TaskType getType() {
        return type;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }
}