package tasks.models;

import tasks.enums.TaskStatus;
import tasks.enums.TaskType;

public class Subtask extends Task {
    private int epicId;

    public Subtask(int id, String name, String description, int epicId) {
        super(id, name, description, TaskType.SUBTASK, TaskStatus.NEW);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }
}