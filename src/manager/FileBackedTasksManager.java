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
import java.util.Arrays;
import java.util.List;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;

public class FileBackedTasksManager extends InMemoryTaskManager {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final File saveFile;

    public FileBackedTasksManager(File saveFile) {
        this.saveFile = saveFile;
    }

    private static void addTaskToManager(Task task) {
        final int id = task.getId();

        switch (task.getType()) {
            case NORMAL:
                normalTasksMap.put(id, task);
                break;
            case SUBTASK:
                subtasksMap.put(id, (Subtask) task);
                break;
            case EPIC:
                epicsMap.put(id, (Epic) task);
                break;
        }
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

                    addTaskToManager(task);

                } else {
                    List<Integer> taskIds = historyFromString(line);

                    historyIds.addAll(taskIds);
                }
            }
            manager.nextId = maxId + 1;

            for (int taskId : historyIds) {
                Task task = normalTasksMap.get(taskId);
                Epic epic = epicsMap.get(taskId);
                Subtask subtask = subtasksMap.get(taskId);

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
        //save();
        return createdEpic;
    }

    @Override
    public Subtask createSubtask(Subtask subtask) {
        Subtask createdSubtask = super.createSubtask(subtask);
        //save();
        return createdSubtask;
    }

    @Override
    public Task createNormalTask(Task task) {
        Task createdTask = super.createNormalTask(task);
        //save();
        return createdTask;
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        //save();
    }

    @Override
    public Subtask updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        //save();
        return subtask;
    }

    @Override
    public Task updateNormalTask(Task task) {
        super.updateNormalTask(task);
        //save();
        return task;
    }

    @Override
    public void deleteEpic(int id) {
        super.deleteEpic(id);
        //save();
    }

    @Override
    public void deleteSubtask(int id) {
        super.deleteSubtask(id);
        //save();
    }

    @Override
    public void deleteNormalTask(int id) {
        super.deleteNormalTask(id);
        //save();
    }
    
    public void save() {
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
            writer.println(historyToString(historyManager.getHistory()));
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
        int duration = Integer.parseInt(parts[5]);
        LocalDateTime startTime = LocalDateTime.parse(parts[6], formatter);

        if (type == TaskType.EPIC) {
            Epic epic = new Epic(id, name, description, status, duration, startTime);

            String subtaskIdsStr = parts[7].replaceAll("\\[|\\]", "");
            String[] subtaskIdsArray = subtaskIdsStr.split(", ");

            for (String subtaskId : subtaskIdsArray) {
                if (!subtaskId.isEmpty()) {
                    epic.addSubtaskId(Integer.parseInt(subtaskId));
                }
            }

            return epic;
        } else if (type == TaskType.SUBTASK) {
            int epicId = Integer.parseInt(parts[7]);

            return new Subtask(id, name, description, epicId, duration, startTime);
        } else {
            return new Task(id, name, description, type, status, duration, startTime);
        }
    }

    private String toString(Task task) {
        if (task.getType() == TaskType.EPIC) {
            Epic epic = (Epic) task;

            return String.format("%d,%s,%s,%s,%s,%d,%s,%s", task.getId(), task.getType(), task.getName(),
                    task.getStatus(), task.getDescription(), task.getDuration(), task.getStartTime().format(formatter),
                    epic.getSubtaskIds());
        } else if (task.getType() == TaskType.SUBTASK) {
            Subtask subtask = (Subtask) task;

            return String.format("%d,%s,%s,%s,%s,%d,%s,%d", task.getId(), task.getType(), task.getName(),
                    task.getStatus(), task.getDescription(), task.getDuration(), task.getStartTime().format(formatter),
                    subtask.getEpicId());
        } else {
            return String.format("%d,%s,%s,%s,%s,%d,%s", task.getId(), task.getType(), task.getName(),
                    task.getStatus(), task.getDescription(), task.getDuration(), task.getStartTime().format(formatter));
        }
    }

    private static String historyToString(List<Task> taskList) {
        List<String> historyList = new ArrayList<>();
        for (Task task : taskList) {
            historyList.add(String.valueOf(task.getId()));
        }
        return String.join(",", historyList);
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

        Epic epic1 = new Epic(1, "Epic 1", "Description of Epic 1", TaskStatus.NEW);

        Subtask subtask1 = new Subtask(1, "Subtask 1", "Description of Subtask 1", epic1.getId());
        subtask1.setStatus(TaskStatus.DONE);

        Task normalTask1 = new Task(1, "Task 1", "Description of Task 1",
                TaskType.NORMAL, TaskStatus.NEW);

        FileBackedTasksManager tasksManager =
                new FileBackedTasksManager(new File("./resources/savedTasks.csv"));

        tasksManager.createEpic(epic1);
        tasksManager.createSubtask(subtask1);
        tasksManager.createNormalTask(normalTask1);

        tasksManager.save();

        List<Task> history = tasksManager.getHistory();
        System.out.println("History: " + history);

        System.out.println("All Epics: " + tasksManager.getAllEpics());
        System.out.println("All Subtasks: " + tasksManager.getAllSubtasks());
        System.out.println("All Normal Tasks: " + tasksManager.getAllNormalTasks());
    }
}