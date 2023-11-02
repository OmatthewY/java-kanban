import tasks.enums.TaskStatus;
import tasks.enums.TaskType;
import tasks.models.Epic;
import tasks.models.Task;
import tasks.models.Subtask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryTaskManager implements TaskManager {
    private int nextId = 1;
    private Map<Integer, Task> epicsMap = new HashMap<>();
    private Map<Integer, Task> subtasksMap = new HashMap<>();
    private Map<Integer, Task> normalTasksMap = new HashMap<>();
    private static HistoryManager historyManager = Managers.getDefaultHistory();

    @Override
    public Task createEpic(String name, String description) {
        int taskId = nextId++;
        Task epic = new Epic(taskId, name, description, TaskStatus.NEW);
        epicsMap.put(taskId, epic);

        return epic;
    }

    @Override
    public Task createSubtask(String name, String description, int epicId) {
        Task epic = epicsMap.get(epicId);

        if (epic != null && epic.getType() == TaskType.EPIC) {
            int taskId = nextId++;

            Task subtask = new Subtask(taskId, name, description, epicId);
            subtasksMap.put(taskId, subtask);


            ((Epic) epic).addSubtaskId(taskId);
            return subtask;
        } else {
            System.out.println("Эпик с указанным ID не найден. Пожалуйста, создайте эпик сначала.");
            return null;
        }
    }

    @Override
    public Task createNormalTask(String name, String description) {
        int taskId = nextId++;

        Task normalTask = new Task(taskId, name, description, TaskType.NORMAL, TaskStatus.NEW);
        normalTasksMap.put(taskId, normalTask);

        return normalTask;
    }

    @Override
    public void updateEpic(int id, String name, String description, TaskStatus status) {
        Task task = findTaskById(id);

        if (task != null && task.getType() == TaskType.EPIC) {
            task.setName(name);
            task.setDescription(description);
            task.setStatus(status);


            List<Task> subtasks = getSubtasksForEpic(id);

            for (Task subtask : subtasks) {
                subtask.setStatus(status);


            }
        } else {
            System.out.println("Эпик с указанным ID не найден.");
        }
    }

    @Override
    public void updateSubtask(int id, String name, String description, TaskStatus status) {
        Task task = findTaskById(id);

        if (task != null && task.getType() == TaskType.SUBTASK) {
            task.setName(name);
            task.setDescription(description);
            task.setStatus(status);


        } else {
            System.out.println("Подзадача с указанным ID не найдена.");
        }
    }

    @Override
    public void updateNormalTask(int id, String name, String description, TaskStatus status) {
        Task task = findTaskById(id);

        if (task != null && task.getType() == TaskType.NORMAL) {
            task.setName(name);
            task.setDescription(description);
            task.setStatus(status);


        } else {
            System.out.println("Задача с указанным ID не найдена.");
        }
    }

    @Override
    public void deleteEpic(int id) {
        Task epic = epicsMap.get(id);

        if (epic != null) {
            epicsMap.remove(id);

            List<Integer> subtaskIds = ((Epic) epic).getSubtaskIds();

            for (int subtaskId : subtaskIds) {
                Task subtask = subtasksMap.get(subtaskId);
                if (subtask != null) {
                    subtasksMap.remove(subtaskId);
                }
            }
        } else {
            System.out.println("Эпик с указанным ID не найден.");
        }
    }

    @Override
    public void deleteSubtask(int id) {
        Task subtask = subtasksMap.get(id);

        if (subtask != null) {
            subtasksMap.remove(id);
        } else {
            System.out.println("Подзадача с указанным ID не найдена.");
        }
    }

    @Override
    public void deleteNormalTask(int id) {
        Task normalTask = normalTasksMap.get(id);

        if (normalTask != null) {
            normalTasksMap.remove(id);
        } else {
            System.out.println("Задача с указанным ID не найдена.");
        }
    }

    @Override
    public Task findTaskById(int id) {
        Task task = epicsMap.get(id);
        if (task == null) {
            task = subtasksMap.get(id);
        }
        if (task == null) {
            task = normalTasksMap.get(id);
        }

        if (task == null) {
            System.out.println("Задача с указанным ID не найдена.");
        }
        return task;
    }

    @Override
    public List<Task> getAllEpics() {
        List<Task> epics = new ArrayList<>(epicsMap.values());

        for (Task epic : epics) {
            updateViewedTasks(epic);
        }
        return epics;
    }

    @Override
    public List<Task> getAllSubtasks() {
        List<Task> subtasks = new ArrayList<>(subtasksMap.values());

        for (Task subtask : subtasks) {
            updateViewedTasks(subtask);
        }
        return subtasks;
    }

    @Override
    public List<Task> getAllNormalTasks() {
        List<Task> normalTasks = new ArrayList<>(normalTasksMap.values());

        for (Task normalTask : normalTasks) {
            updateViewedTasks(normalTask);
        }
        return normalTasks;
    }

    @Override
    public List<Task> getSubtasksForEpic(int epicId) {
        List<Task> subtasks = new ArrayList<>();

        for (Task task : subtasksMap.values()) {
            if (task.getType() == TaskType.SUBTASK && ((Subtask) task).getEpicId() == epicId) {
                subtasks.add(task);
            }
        }
        return subtasks;
    }

    public void updateViewedTasks(Task task) {

        if (!historyManager.getHistory().contains(task)) {
            historyManager.add(task);

            if (historyManager.getHistory().size() > 10) {
                historyManager.getHistory().remove(0);
            }
        }
    }

    public List<Task> getHistory() {
        return historyManager.getHistory();
    }
}
