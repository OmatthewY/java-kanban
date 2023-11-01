import tasks.enums.TaskStatus;
import tasks.enums.TaskType;
import tasks.models.Epic;
import tasks.models.Task;
import tasks.models.Subtask;

import java.util.ArrayList;
import java.util.List;

public class InMemoryTaskManager implements TaskManager {
    private int nextId = 1;
    private List<Task> tasks = new ArrayList<>();
    private static HistoryManager historyManager = Managers.getDefaultHistory();

    @Override
    public Task createEpic(String name, String description) {
        int taskId = nextId++;
        Task epic = new Epic(taskId, name, description, TaskStatus.NEW);
        tasks.add(epic);


        return epic;
    }

    @Override
    public Task createSubtask(String name, String description, int epicId) {
        Task epic = findTaskById(epicId);

        if (epic != null && epic.getType() == TaskType.EPIC) {
            int taskId = nextId++;

            Task subtask = new Subtask(taskId, name, description, epicId);
            tasks.add(subtask);


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
        tasks.add(normalTask);


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
        Task task = findTaskById(id);

        if (task != null && task.getType() == TaskType.EPIC) {
            tasks.remove(task);


            List<Integer> subtaskIds = ((Epic) task).getSubtaskIds();

            for (int subtaskId : subtaskIds) {
                Task subtask = findTaskById(subtaskId);
                if (subtask != null) {
                    tasks.remove(subtask);


                }
            }
        } else {
            System.out.println("Эпик с указанным ID не найден.");
        }
    }

    @Override
    public void deleteSubtask(int id) {
        Task task = findTaskById(id);

        if (task != null && task.getType() == TaskType.SUBTASK) {
            tasks.remove(task);


        } else {
            System.out.println("Подзадача с указанным ID не найдена.");
        }
    }

    @Override
    public void deleteNormalTask(int id) {
        Task task = findTaskById(id);

        if (task != null && task.getType() == TaskType.NORMAL) {
            tasks.remove(task);


        } else {
            System.out.println("Задача с указанным ID не найдена.");
        }
    }

    @Override
    public Task findTaskById(int id) {
        for (Task task : tasks) {
            if (task.getId() == id) {
                return task;
            }
        }
        System.out.println("Задача с указанным ID не найдена.");
        return null;
    }

    @Override
    public List<Task> getAllEpics() {
        List<Task> epics = new ArrayList<>();

        for (Task task : tasks) {
            if (task.getType() == TaskType.EPIC) {
                epics.add(task);
                updateViewedTasks(task);
            }
        }
        return epics;
    }

    @Override
    public List<Task> getAllSubtasks() {
        List<Task> subtasks = new ArrayList<>();

        for (Task task : tasks) {
            if (task.getType() == TaskType.SUBTASK) {
                subtasks.add(task);
                updateViewedTasks(task);
            }
        }
        return subtasks;
    }

    @Override
    public List<Task> getAllNormalTasks() {
        List<Task> normalTasks = new ArrayList<>();

        for (Task task : tasks) {
            if (task.getType() == TaskType.NORMAL) {
                normalTasks.add(task);
                updateViewedTasks(task);
            }
        }
        return normalTasks;
    }

    @Override
    public List<Task> getSubtasksForEpic(int epicId) {
        List<Task> subtasks = new ArrayList<>();

        for (Task task : tasks) {
            if (task.getType() == TaskType.SUBTASK && ((Subtask) task).getEpicId() == epicId) {
                subtasks.add(task);
            }
        }
        return subtasks;
    }

    private void updateViewedTasks(Task task) {

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
