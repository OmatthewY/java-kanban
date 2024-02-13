package tasks.models;

import manager.InMemoryTaskManager;
import tasks.enums.TaskStatus;
import tasks.enums.TaskType;

import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;

public class Epic extends Task {
    private List<Integer> subtaskIds;
    private LocalDateTime endTime;

    InMemoryTaskManager taskManager = new InMemoryTaskManager();

    public Epic(int id, String name, String description, TaskStatus status) {
        super(id, name, description, TaskType.EPIC, status);
        subtaskIds = new ArrayList<>();
    }

    public Epic(String name, String description, TaskStatus status) {
        super(0, name, description, TaskType.EPIC, status);
        subtaskIds = new ArrayList<>();
    }

    public Epic(int id, String name, String description, TaskStatus status, int duration, LocalDateTime startTime) {
        super(id, name, description, TaskType.EPIC, status, duration, startTime);
        subtaskIds = new ArrayList<>();
        this.endTime = startTime;
    }

    @Override
    public LocalDateTime getStartTime() {
        return startTime;
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

    public List<Integer> getSubtaskIds() {
        return subtaskIds;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public void deleteAllSubtaskIds() {
        subtaskIds.clear();
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