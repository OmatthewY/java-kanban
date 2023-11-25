package tasks.models;

import tasks.enums.TaskStatus;
import tasks.enums.TaskType;

import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    private List<Integer> subtaskIds;

    public Epic(int id, String name, String description, TaskStatus status) {
        super(id, name, description, TaskType.EPIC, status);
        subtaskIds = new ArrayList<>();
    }

    public Epic(String name, String description, TaskStatus status) {
        super(0, name, description, TaskType.EPIC, status);
        subtaskIds = new ArrayList<>();
    }

    public List<Integer> getSubtaskIds() {
        return subtaskIds;
    }

    public void addSubtaskId(int subtaskId) {
        subtaskIds.add(subtaskId);
    }

    public String getName() {
        return super.getName();
    }

    public String getDescription() {
        return super.getDescription();
    }
}