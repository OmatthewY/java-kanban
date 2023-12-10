package manager;

import tasks.enums.TaskStatus;
import tasks.enums.TaskType;
import tasks.models.Epic;
import tasks.models.Subtask;
import tasks.models.Task;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class FileBackedTasksManager extends InMemoryTaskManager {

    private final File saveFile;

    public FileBackedTasksManager(File saveFile) {
        this.saveFile = saveFile;
    }

    public static FileBackedTasksManager loadFromFile(File file) {
        FileBackedTasksManager manager = new FileBackedTasksManager(file);

        try {
            String fileContent = Files.readString(Path.of(file.getPath()));
            String[] lines = fileContent.split("\n");

            int maxId = 0;

            List<Integer> historyIds = new ArrayList<>();

            for (String line : lines) {
                if (!line.isEmpty()) {
                    Task task = fromString(line);

                    if (task.getId() > maxId) {
                        maxId = task.getId();
                    }

                    if (task.getType() == TaskType.EPIC) {
                        Epic epic = manager.epicsMap.get(task.getId());

                        if (epic != null) {
                            manager.epicsMap.put(task.getId(), epic);
                            manager.historyManager.add(epic);
                        }
                    } else if (task.getType() == TaskType.SUBTASK) {
                        Subtask subtask = manager.subtasksMap.get(task.getId());

                        if (subtask != null) {
                            manager.subtasksMap.put(task.getId(), subtask);
                            manager.historyManager.add(subtask);
                        }
                    } else {
                        Task normalTask = manager.normalTasksMap.get(task.getId());

                        if (normalTask != null) {
                            manager.normalTasksMap.put(task.getId(), normalTask);
                            manager.historyManager.add(normalTask);
                        }
                    }
                } else {
                    List<Integer> taskIds = historyFromString(line);

                    historyIds.addAll(taskIds);
                }
            }
            manager.nextId = maxId + 1;

            for (int taskId : historyIds) {
                Task task = manager.normalTasksMap.get(taskId);
                Epic epic = manager.epicsMap.get(taskId);
                Subtask subtask = manager.subtasksMap.get(taskId);

                if (task != null) {
                    manager.historyManager.add(task);
                } else if (epic != null) {
                    manager.historyManager.add(epic);
                } else if (subtask != null) {
                    manager.historyManager.add(subtask);
                }
            }
        } catch (IOException e) {
            System.out.println("Failed to load manager state from file" + e.getMessage());
        }

        return manager;
    }

    @Override
    public Epic createEpic(Epic epic) {
        Epic createdEpic = super.createEpic(epic);
        save();
        return createdEpic;
    }

    @Override
    public Subtask createSubtask(Subtask subtask) {
        Subtask createdSubtask = super.createSubtask(subtask);
        save();
        return createdSubtask;
    }

    @Override
    public Task createNormalTask(Task task) {
        Task createdTask = super.createNormalTask(task);
        save();
        return createdTask;
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
    }

    @Override
    public void updateNormalTask(Task task) {
        super.updateNormalTask(task);
        save();
    }

    @Override
    public void deleteEpic(int id) {
        super.deleteEpic(id);
        save();
    }

    @Override
    public void deleteSubtask(int id) {
        super.deleteSubtask(id);
        save();
    }

    @Override
    public void deleteNormalTask(int id) {
        super.deleteNormalTask(id);
        save();
    }
    
    private void save() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(saveFile))) {

            for (Epic epic : getAllEpics()) {
                writer.println(toString(epic));
            }
            for (Subtask subtask : getAllSubtasks()) {
                writer.println(toString(subtask));
            }
            for (Task task : getAllNormalTasks()) {
                writer.println(toString(task));
            }

            writer.println();
            writer.println(historyToString(historyManager));
        } catch (IOException e) {
            System.out.println("Failed to save manager state to file" + e.getMessage());
        }
    }

    private static Task fromString(String value) {
        String[] parts = value.split(",");

        int id = Integer.parseInt(parts[0]);
        TaskType type = TaskType.valueOf(parts[1]);
        String name = parts[2];
        TaskStatus status = TaskStatus.valueOf(parts[3]);
        String description = parts[4];

        if (type == TaskType.EPIC) {
            Epic epic = new Epic(id, name, description, status);

            String subtaskIdsStr = parts[5].replaceAll("\\[|\\]", "");
            String[] subtaskIdsArray = subtaskIdsStr.split(", ");

            for (String subtaskId : subtaskIdsArray) {
                if (!subtaskId.isEmpty()) {
                    epic.addSubtaskId(Integer.parseInt(subtaskId));
                }
            }

            return epic;
        } else if (type == TaskType.SUBTASK) {
            int epicId = Integer.parseInt(parts[5]);

            return new Subtask(id, name, description, epicId);
        } else {
            return new Task(id, name, description, type, status);
        }
    }

    private String toString(Task task) {
        if (task.getType() == TaskType.EPIC) {
            Epic epic = (Epic) task;

            return String.format("%d,%s,%s,%s,%s,%s", task.getId(), task.getType(), task.getName(),
                    task.getStatus(), task.getDescription(), epic.getSubtaskIds().toString());
        } else if (task.getType() == TaskType.SUBTASK) {
            Subtask subtask = (Subtask) task;

            return String.format("%d,%s,%s,%s,%s,%d", task.getId(), task.getType(), task.getName(),
                    task.getStatus(), task.getDescription(), subtask.getEpicId());
        } else {
            return String.format("%d,%s,%s,%s,%s,", task.getId(), task.getType(), task.getName(),
                    task.getStatus(), task.getDescription());
        }
    }

    private static String historyToString(HistoryManager manager) {
        List<Task> history = manager.getHistory();
        StringBuilder builder = new StringBuilder();
        for (Task task : history) {
            builder.append(task.getId()).append(",");
        }
        return builder.toString();
    }

    private static List<Integer> historyFromString(String value) {
        String[] parts = value.split(",");
        List<Integer> history = new ArrayList<>();
        for (String part : parts) {
            if (!part.isEmpty()) {
                history.add(Integer.parseInt(part));
            }
        }
        return history;
    }

    public static void main(String[] args) {

        Epic epic1 = new Epic(2,"Epic 1", "Description of Epic 1", TaskStatus.NEW);

        Subtask subtask1 = new Subtask(1, "Subtask 1", "Description of Subtask 1", epic1.getId());
        subtask1.setStatus(TaskStatus.DONE);

        Task normalTask1 = new Task(1,"Task 1", "Description of Task 1",
                TaskType.NORMAL, TaskStatus.NEW);

        FileBackedTasksManager tasksManager =
                new FileBackedTasksManager(new File("./resources/savedTasks.csv"));

        tasksManager.createNormalTask(normalTask1);
        tasksManager.createEpic(epic1);
        tasksManager.createSubtask(subtask1);

        tasksManager.save();

        List<Task> history = tasksManager.getHistory();
        System.out.println("History: " + history);

        System.out.println("All Epics: " + tasksManager.getAllEpics());
        System.out.println("All Subtasks: " + tasksManager.getAllSubtasks());
        System.out.println("All Normal Tasks: " + tasksManager.getAllNormalTasks());
    }
}