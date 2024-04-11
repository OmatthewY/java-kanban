package manager;

import comparator.TasksComparator;
import exceptions.VerificationException;
import tasks.enums.TaskStatus;
import tasks.models.Epic;
import tasks.models.Task;
import tasks.models.Subtask;

import java.util.*;
import java.time.LocalDateTime;

public class InMemoryTaskManager implements TaskManager {
    protected int nextId = 1;
    protected static Map<Integer, Epic> epicsMap = new HashMap<>();
    protected static Map<Integer, Subtask> subtasksMap = new HashMap<>();
    protected static Map<Integer, Task> normalTasksMap = new HashMap<>();
    protected final HistoryManager historyManager = Managers.getDefaultHistory();
    protected static TreeSet<Task> tasksTreeSet = new TreeSet<>(new TasksComparator());

    @Override
    public Epic createEpic(Epic epic) {
        try {
            final int taskId = nextId++;
            epic.setId(taskId);

            epicsMap.put(taskId, epic);
            updateEpicStatus(epic.getId());
            addTask(epic);

            return epic;
        } catch (VerificationException e) {
            System.out.println("Ошибка верификации: " + e.getMessage());
            return null;
        }
    }

    @Override
    public Subtask createSubtask(Subtask subtask) {
        try {
            final int taskId = nextId++;
            subtask.setId(taskId);

            Epic epic = (Epic) findEpicById(subtask.getEpicId());

            if (epic != null) {
                subtasksMap.put(taskId, subtask);
                epic.addSubtaskId(taskId);
                updateEpicStatus(subtask.getEpicId());
                addTask(subtask);

                return subtask;
            } else {
                System.out.println("Эпик с указанным ID не найден. Пожалуйста, создайте эпик сначала.");
                return null;
            }
        } catch (VerificationException e) {
            System.out.println("Ошибка верификации: " + e.getMessage());
            return null;
        }
    }

    @Override
    public Task createNormalTask(Task task) {
        try {
            final int taskId = nextId++;
            task.setId(taskId);

            normalTasksMap.put(taskId, task);
            addTask(task);
            historyManager.add(task);

            return task;
        } catch (VerificationException e) {
            System.out.println("Ошибка верификации: " + e.getMessage());
            return null;
        }
    }

    @Override
    public Epic updateEpic(Epic epic) {
        Epic savedEpic = (Epic) findEpicById(epic.getId());

        if (savedEpic != null) {
            savedEpic.setName(epic.getName());
            savedEpic.setDescription(epic.getDescription());
            savedEpic.setStatus(epic.getStatus());
            return savedEpic;
        } else {
            System.out.println("Эпик с указанным ID не найден.");
            return null;
        }
    }

    @Override
    public Subtask updateSubtask(Subtask subtask) {
        try {
            final int id = subtask.getId();
            final int epicId = subtask.getEpicId();

            Subtask savedSubtask = (Subtask) findSubtaskById(id);

            tasksTreeSet.remove(subtask);

            if (savedSubtask != null) {
                savedSubtask.setName(subtask.getName());
                savedSubtask.setDescription(subtask.getDescription());
                savedSubtask.setStatus(subtask.getStatus());

                Epic epic = (Epic) findEpicById(epicId);

                if (epic != null) {
                    addTask(subtask);
                    updateEpicStatus(epicId);
                } else {
                    System.out.println("Эпик с указанным ID не найден.");
                }
            } else {
                System.out.println("Подзадача с указанным ID не найдена.");
            }
            return savedSubtask;
        } catch (VerificationException e) {
            System.out.println("Ошибка верификации: " + e.getMessage());
            return null;
        }
    }

    @Override
    public Task updateNormalTask(Task task) {
        try {
            final int id = task.getId();
            final Task savedTask = normalTasksMap.get(id);

            tasksTreeSet.remove(task);

            if (savedTask != null) {
                savedTask.setName(task.getName());
                savedTask.setDescription(task.getDescription());
                savedTask.setStatus(task.getStatus());
                addTask(task);
            } else {
                System.out.println("Задача с указанным ID не найдена.");
            }
            return savedTask;
        } catch (VerificationException e) {
            System.out.println("Ошибка верификации: " + e.getMessage());
            return null;
        }
    }

    @Override
    public void deleteEpic(int id) {
        Epic epic = (Epic) findEpicById(id);

        if (epic != null) {
            tasksTreeSet.remove(epic);
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
            tasksTreeSet.remove(subtask);
            updateEpicStatus(((Subtask) subtask).getEpicId());
            subtasksMap.remove(id);
            historyManager.remove(id);
        } else {
            System.out.println("Подзадача с указанным ID не найдена.");
        }
    }

    @Override
    public void deleteNormalTask(int id) {
        Task normalTask = normalTasksMap.get(id);

        if (normalTask != null) {
            tasksTreeSet.remove(normalTask);
            normalTasksMap.remove(id);
            historyManager.remove(id);
        } else {
            System.out.println("Задача с указанным ID не найдена.");
        }
    }

    @Override
    public void deleteAllEpics() {
        epicsMap.clear();
        subtasksMap.clear();

        historyManager.removeAllTasksOfType(Epic.class);

        tasksTreeSet.removeIf(task -> task instanceof Epic);
    }

    @Override
    public void deleteAllSubtasks() {
        subtasksMap.clear();

        if (!epicsMap.isEmpty()) {

            for (Epic epic : epicsMap.values()) {
                epic.deleteAllSubtaskIds();
                updateEpicStatus(epic.getId());
            }
        }
        historyManager.removeAllTasksOfType(Subtask.class);

        tasksTreeSet.removeIf(task -> task instanceof Subtask);
    }

    @Override
    public void deleteAllNormalTasks() {
        normalTasksMap.clear();

        historyManager.removeAllTasksOfType(Task.class);

        tasksTreeSet.removeIf(task -> task instanceof Task);
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

        Epic epic = epicsMap.get(epicId);

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

    @Override
    public Epic getEpic(int id) {
        historyManager.add(epicsMap.get(id));
        return epicsMap.get(id);
    }

    @Override
    public Subtask getSubtask(int id) {
        historyManager.add(subtasksMap.get(id));
        return subtasksMap.get(id);
    }

    @Override
    public Task getNormalTask(int id) {
        historyManager.add(normalTasksMap.get(id));
        return normalTasksMap.get(id);
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

    @Override
    public ArrayList<Task> getPrioritizedTasks() {
        return new ArrayList<>(tasksTreeSet);
    }

    public void getEpicTime(Epic epic) {
        if (!epic.getSubtaskIds().isEmpty()) {
            LocalDateTime subEndTime = getSubtasksForEpic(epic.getId()).get(0).getEndTime();
            LocalDateTime subStartTime = getSubtasksForEpic(epic.getId()).get(0).getStartTime();
            int duration = 0;

            for (Subtask subtask : getSubtasksForEpic(epic.getId())) {
                if (subtask.getEndTime() == null || subtask.getStartTime() == null) {
                    continue;
                }
                if (subtask.getEndTime().isAfter(subEndTime)) {
                    subEndTime = subtask.getEndTime();
                }
                if (subtask.getStartTime().isBefore(subStartTime)) {
                    subStartTime = subtask.getStartTime();
                }
                duration += subtask.getDuration();
            }
            epic.setDuration(duration);
            epic.setStartTime(subStartTime);
            epic.setEndTime(subEndTime);
        }
    }

    private void addTask(Task task) throws VerificationException {
        for (Task expTask : tasksTreeSet) {
            if (Objects.equals(task.getId(), expTask.getId())) {
                continue;
            }
            if (expTask.getStartTime() == null || task.getStartTime() == null) {
                break;
            }
            if (task.getEndTime().isBefore(expTask.getStartTime()) ||
                    task.getEndTime().equals(expTask.getStartTime()) ||
                    task.getStartTime().isAfter(expTask.getEndTime()) ||
                    task.getStartTime().equals(expTask.getEndTime())) {
            } else {
                throw new VerificationException("Задача не добавлена!");
            }
        }
        tasksTreeSet.add(task);
    }
}