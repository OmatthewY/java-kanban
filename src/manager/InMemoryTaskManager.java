package manager;

import tasks.enums.TaskStatus;
import tasks.models.Epic;
import tasks.models.Task;
import tasks.models.Subtask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryTaskManager implements TaskManager {
    private int nextId = 1;
    private final Map<Integer, Epic> epicsMap = new HashMap<>();
    private final Map<Integer, Subtask> subtasksMap = new HashMap<>();
    private final Map<Integer, Task> normalTasksMap = new HashMap<>();
    protected final HistoryManager historyManager = Managers.getDefaultHistory();

    @Override
    public Epic createEpic(Epic epic) {
        final int taskId = nextId++;
        epic.setId(taskId);

        epicsMap.put(taskId, epic);
        updateEpicStatus(epic.getId());

        return epic;
    }

    @Override
    public Subtask createSubtask(Subtask subtask) {
        final int taskId = nextId++;
        subtask.setId(taskId);

        Epic epic = (Epic) findEpicById(subtask.getEpicId());

        if (epic != null) {
            subtasksMap.put(taskId, subtask);
            epic.addSubtaskId(taskId);
            updateEpicStatus(subtask.getEpicId());

            return subtask;
        } else {
            System.out.println("Эпик с указанным ID не найден. Пожалуйста, создайте эпик сначала.");
            return null;
        }
    }

    @Override
    public Task createNormalTask(Task task) {
        final int taskId = nextId++;
        task.setId(taskId);

        normalTasksMap.put(taskId, task);

        return task;
    }

    @Override
    public void updateEpic(Epic epic) {
        Epic savedEpic = (Epic) findEpicById(epic.getId());

        if (savedEpic != null) {
            savedEpic.setName(epic.getName());
            savedEpic.setDescription(epic.getDescription());
            savedEpic.setStatus(epic.getStatus());
        } else {
            System.out.println("Эпик с указанным ID не найден.");
        }
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        final int id = subtask.getId();
        final int epicId = subtask.getEpicId();

        Subtask savedSubtask = (Subtask) findSubtaskById(id);

        if (savedSubtask != null) {
            savedSubtask.setName(subtask.getName());
            savedSubtask.setDescription(subtask.getDescription());
            savedSubtask.setStatus(subtask.getStatus());

            Epic epic = (Epic) findEpicById(epicId);

            if (epic != null) {
                updateEpicStatus(epicId);
            } else {
                System.out.println("Эпик с указанным ID не найден.");
            }
        } else {
            System.out.println("Подзадача с указанным ID не найдена.");
        }
    }

    @Override
    public void updateNormalTask(Task task) {
        final int id = task.getId();
        final Task savedTask = normalTasksMap.get(id);

        if (savedTask != null) {
            savedTask.setName(task.getName());
            savedTask.setDescription(task.getDescription());
            savedTask.setStatus(task.getStatus());
        } else {
            System.out.println("Задача с указанным ID не найдена.");
        }
    }

    @Override
    public void deleteEpic(int id) {
        Epic epic = (Epic) findEpicById(id);

        if (epic != null) {
            epicsMap.remove(id);

            List<Integer> subtaskIds = epic.getSubtaskIds();

            for (int subtaskId : subtaskIds) {
                deleteSubtask(subtaskId);
            }

            historyManager.remove(id);
        } else {
            System.out.println("Эпик с указанным ID не найден.");
        }
    }

    @Override
    public void deleteSubtask(int id) {
        Subtask subtask = subtasksMap.get(id);

        if (subtask != null) {
            subtasksMap.remove(id);
            updateEpicStatus(((Subtask) subtask).getEpicId());
            historyManager.remove(id);
        } else {
            System.out.println("Подзадача с указанным ID не найдена.");
        }
    }

    @Override
    public void deleteNormalTask(int id) {
        Task normalTask = normalTasksMap.get(id);

        if (normalTask != null) {
            normalTasksMap.remove(id);
            historyManager.remove(id);
        } else {
            System.out.println("Задача с указанным ID не найдена.");
        }
    }

    @Override
    public Subtask findSubtaskById(int id) {
        Subtask subtask = subtasksMap.get(id);

        if (subtask != null) {
            historyManager.add(subtask);
        }

        return subtask;
    }

    @Override
    public Epic findEpicById(int id) {
        Epic epic = epicsMap.get(id);

        if (epic != null) {
            historyManager.add(epic);
        }

        return epic;
    }

    @Override
    public Task findNormalTaskById(int id) {
        Task normalTask = normalTasksMap.get(id);

        if (normalTask != null) {
            historyManager.add(normalTask);
        }

        return normalTask;
    }

    @Override
    public List<Epic> getAllEpics() {
        return new ArrayList<>(epicsMap.values());
    }

    @Override
    public List<Subtask> getAllSubtasks() {
        return new ArrayList<>(subtasksMap.values());
    }

    @Override
    public List<Task> getAllNormalTasks() {
        return new ArrayList<>(normalTasksMap.values());
    }

    @Override
    public List<Subtask> getSubtasksForEpic(int epicId) {
        List<Subtask> subtasks = new ArrayList<>();

        Epic epic = (Epic) findEpicById(epicId);

        if (epic != null) {
            List<Integer> subtaskIds = epic.getSubtaskIds();

            for (int subtaskId : subtaskIds) {
                Subtask subtask = subtasksMap.get(subtaskId);
                if (subtask != null) {
                    subtasks.add(subtask);
                } else {
                    System.out.println("Подзадача с ID " + subtaskId + " не найдена.");
                }
            }
        } else {
            System.out.println("Эпик с указанным ID не найден.");
        }

        return subtasks;
    }

    private void updateEpicStatus(int epicId) {
        Epic epic = (Epic) findEpicById(epicId);

        if (epic != null) {
            List<Integer> subs = epic.getSubtaskIds();

            if (subs.isEmpty()) {
                epic.setStatus(TaskStatus.NEW);

                return;
            }
            TaskStatus status = null;

            for (int id : subs) {
                final Subtask subtask = (Subtask) findSubtaskById(id);

                if (status == null) {
                    status = subtask.getStatus();
                    continue;
                }
                if (status == subtask.getStatus() && status != TaskStatus.IN_PROGRESS) {
                    continue;
                }
                epic.setStatus(TaskStatus.IN_PROGRESS);

                return;
            }
            epic.setStatus(status);
        } else {
            System.out.println("Эпик с указанным ID не найден.");
        }
    }

    public List<Task> getHistory() {
        return historyManager.getHistory();
    }
}