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

    public Task(String name, String description, TaskType type, TaskStatus status) {
        this(0, name, description, type, status);
    }

    @Override
    public String toString() {
        return "Task ID: " + id + ", Name: " + name + ", Description: " + description + ", Type: " + type
                + ", Status: " + status;
    }

    public int getId() {
        return id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TaskType getType() {
        return type;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }
}