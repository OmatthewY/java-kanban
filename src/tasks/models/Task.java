package tasks.models;

import tasks.enums.TaskStatus;
import tasks.enums.TaskType;

import java.time.LocalDateTime;

public class Task {
    private int id;
    private String name;
    private String description;
    private TaskType type;
    private TaskStatus status;
    protected int duration;
    protected LocalDateTime startTime;

    public Task(int id, String name, String description, TaskType type, TaskStatus status) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.type = type;
        this.status = status;
    }

    public Task(String name, String description, TaskType type, TaskStatus status) {
        this.name = name;
        this.description = description;
        this.type = type;
        this.status = status;
    }

    public Task(int id, String name, String description, TaskType type, TaskStatus status, int duration,
                LocalDateTime startTime) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.type = type;
        this.status = status;
        this.duration = duration;
        this.startTime = startTime;
    }

    public Task(String name, String description, TaskType type, TaskStatus status, int duration,
                LocalDateTime startTime) {
        this.name = name;
        this.description = description;
        this.type = type;
        this.status = status;
        this.duration = duration;
        this.startTime = startTime;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public int getDuration() {
        return duration;
    }

    public LocalDateTime getEndTime() {
        if (startTime != null) {
            return startTime.plusMinutes(duration);
        } else {
            return null;
        }
    }

    @Override
    public String toString() {
        return "ID: " + id + ", Name: " + name + ", Description: " + description + ", Type: " + type
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

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public TaskStatus getStatus() {
        return status;
    }
}