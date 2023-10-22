import tasks.enums.TaskStatus;
import tasks.enums.TaskType;
import tasks.models.Epic;
import tasks.models.Subtask;
import tasks.models.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TaskManager {
    private int nextId = 1;
    private Map<Integer, Task> tasks = new HashMap<>();

    public Task createTask(String name, String description, TaskType type, int epicId) {
        Task task;
        int taskId = nextId++;

        switch (type) {
            case EPIC:
                task = new Epic(taskId, name, description, TaskStatus.NEW);
                break;
            case SUBTASK:
                Epic epic = (Epic) tasks.get(epicId);

                if (epic != null) {
                    task = new Subtask(taskId, name, description, epicId);
                    epic.addSubtaskId(taskId);
                } else {
                    System.out.println("Эпик с указанным ID не найден. Пожалуйста, создайте эпик сначала.");
                    return null;
                }
                break;
            default:
                task = new Task(taskId, name, description, TaskType.NORMAL, TaskStatus.NEW);
                break;
        }

        tasks.put(taskId, task);
        return task;
    }

    public void updateTask(int id, String name, String description, TaskStatus status) {
        Task task = tasks.get(id);

        if (task != null) {
            task.setName(name);
            task.setDescription(description);

            if (task.getType() != TaskType.EPIC) {

                if (status == TaskStatus.NEW || status == TaskStatus.IN_PROGRESS || status == TaskStatus.DONE) {
                    task.setStatus(status);
                } else {
                    System.out.println("Недопустимый статус. Допустимые значения: NEW, IN_PROGRESS, DONE.");
                }
            } else {
                Epic epic = (Epic) task;
                updateEpicStatus(id, status);
            }
        }
    }

    public void deleteTask(int id) {
        Task task = tasks.get(id);

        if (task != null) {
            tasks.remove(id);

            if (task.getType() == TaskType.EPIC) {
                Epic epic = (Epic) task;

                List<Integer> subtaskIds = epic.getSubtaskIds();

                for (int subtaskId : subtaskIds) {
                    tasks.remove(subtaskId);
                }
            }
        } else {
            System.out.println("Задача с указанным ID не найдена.");
        }
    }

    public Task findTaskById(int id) {
        Task task = tasks.get(id);

        if (task == null) {
            System.out.println("Задача с указанным ID не найдена.");
        }
        return task;
    }

    public ArrayList<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    public void updateEpicStatus(int epicId, TaskStatus status) {
        Task epic = tasks.get(epicId);

        if (epic != null && epic.getType() == TaskType.EPIC) {
            Epic epicTask = (Epic) epic;
            epicTask.setStatus(status);

            if (status == TaskStatus.DONE) {
                List<Integer> subtaskIds = epicTask.getSubtaskIds();
                for (int subtaskId : subtaskIds) {
                    Task subtask = tasks.get(subtaskId);
                    if (subtask != null) {
                        subtask.setStatus(TaskStatus.DONE);
                    }
                }
            }

            if (status == TaskStatus.IN_PROGRESS) {
                List<Integer> subtaskIds = epicTask.getSubtaskIds();
                for (int subtaskId : subtaskIds) {
                    Task subtask = tasks.get(subtaskId);
                    if (subtask != null) {
                        subtask.setStatus(TaskStatus.IN_PROGRESS);
                    }
                }
            }

            if (status == TaskStatus.NEW) {
                List<Integer> subtaskIds = epicTask.getSubtaskIds();
                for (int subtaskId : subtaskIds) {
                    Task subtask = tasks.get(subtaskId);
                    if (subtask != null) {
                        subtask.setStatus(TaskStatus.NEW);
                    }
                }
            }
        } else {
            System.out.println("Эпик с указанным ID не найден или не является типом EPIC.");
        }
    }

    public List<Task> getSubtasksForEpic(int epicId) {
        List<Task> subtasks = new ArrayList<>();

        for (Task task : tasks.values()) {
            if (task.getType() == TaskType.SUBTASK && ((Subtask) task).getEpicId() == epicId) {
                subtasks.add(task);
            }
        }
        return subtasks;
    }
}