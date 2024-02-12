package tasks.models;

import tasks.enums.TaskStatus;
import tasks.enums.TaskType;

import java.time.LocalDateTime;

public class Subtask extends Task {
    private int epicId;

    public Subtask(int id, String name, String description, int epicId) {
        super(id, name, description, TaskType.SUBTASK, TaskStatus.NEW);
        this.epicId = epicId;
    }

    public Subtask(String name, String description, int epicId) {
        super(name, description, TaskType.SUBTASK, TaskStatus.NEW);
        this.epicId = epicId;
    }

    public Subtask(String name, String description, int epicId, TaskStatus status) {
        super(name, description, TaskType.SUBTASK, status);
        this.epicId = epicId;
    }

    public Subtask(int id, String name, String description, int epicId, TaskStatus status) {
        super(id, name, description, TaskType.SUBTASK, status);
        this.epicId = epicId;
    }
    public Subtask(int id, String name, String description, int epicId, int duration, LocalDateTime startTime) {
        super(id, name, description, TaskType.SUBTASK, TaskStatus.NEW, duration, startTime);
        this.epicId = epicId;
    }

    public Subtask(String name, String description, int epicId, int duration, LocalDateTime startTime) {
        super(name, description, TaskType.SUBTASK, TaskStatus.NEW, duration, startTime);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }
}