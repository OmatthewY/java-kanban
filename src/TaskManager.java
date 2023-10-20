import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import java.util.Comparator;

public class TaskManager {
    private int nextId = 1;
    private List<Task> tasks = new ArrayList<>();
    private List<Epic> epics = new ArrayList<>();
    private List<Subtask> subtasks = new ArrayList<>();

    public Task createTask(String name, String description, TaskType type, int epicId) {
        Task task;

        switch (type) {
            case EPIC:
                task = new Epic(nextId, name, description, TaskStatus.NEW);
                epics.add((Epic) task);
                break;
            case SUBTASK:
                task = new Subtask(nextId, name, description, epicId);
                subtasks.add((Subtask) task);
                break;
            default:
                task = new Task(nextId, name, description, TaskType.NORMAL, TaskStatus.NEW);
                break;
        }
        tasks.add(task);

        nextId++;

        return task;
    }

    public void updateTask(Task task, String name, String description, TaskStatus status) {
        task.setName(name);
        task.setDescription(description);
        task.setStatus(status);
    }

    public void deleteTask(Task task) {
        tasks.remove(task);

        if (task.getType() == TaskType.EPIC) {
            epics.remove(task);

            List<Subtask> subtasksToRemove = new ArrayList<>();

            for (Subtask subtask : subtasks) {
                if (subtask.getEpicId() == task.getId()) {
                    subtasksToRemove.add(subtask);
                }
            }
            subtasks.removeAll(subtasksToRemove);
        } else if (task.getType() == TaskType.SUBTASK) {
            subtasks.remove(task);
        }
    }

    public Task findTaskById(int id) {
        for (Task task : tasks) {
            if (task.getId() == id) {
                return task;
            }
        }
        return null;
    }

    public List<Task> getAllTasks() {
        List<Task> allTasks = new ArrayList<>(tasks);

        Collections.sort(allTasks, new TaskTypeComparator().thenComparing(new TaskStatusComparator()));

        return allTasks;
    }

    public List<Subtask> getSubtasksByEpicId(int epicId) {
        List<Subtask> epicSubtasks = new ArrayList<>();

        for (Subtask subtask : subtasks) {
            if (subtask.getEpicId() == epicId) {
                epicSubtasks.add(subtask);
            }
        }
        return epicSubtasks;
    }

    public List<Epic> getAllEpics() {
        List<Epic> allEpics = new ArrayList<>(epics);

        Collections.sort(allEpics, new TaskStatusComparator());

        return allEpics;
    }

    public List<Task> getTasksByStatus(TaskStatus status) {
        List<Task> tasksByStatus = new ArrayList<>();

        for (Task task : tasks) {
            if (task.getStatus() == status) {
                tasksByStatus.add(task);
            }
        }
        Collections.sort(tasksByStatus, new TaskTypeComparator());

        return tasksByStatus;
    }
    public Epic findEpicById(int id) {
        for (Epic epic : epics) {
            if (epic.getId() == id) {
                return epic;
            }
        }
        return null;
    }

    private static class TaskTypeComparator implements Comparator<Task> {
        @Override
        public int compare(Task task1, Task task2) {
            return task1.getType().compareTo(task2.getType());
        }
    }

    private static class TaskStatusComparator implements Comparator<Task> {
        @Override
        public int compare(Task task1, Task task2) {
            return task1.getStatus().compareTo(task2.getStatus());
        }
    }
}