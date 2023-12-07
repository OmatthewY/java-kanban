package manager;

import tasks.enums.TaskStatus;
import tasks.models.Epic;
import tasks.models.Subtask;
import tasks.models.Task;

import java.util.List;

public interface TaskManager {
    Task createEpic(Epic epic);

    Task createSubtask(Subtask subtask);

    Task createNormalTask(Task task);

    void updateEpic(Epic epic);

    void updateSubtask(Subtask subtask);

    void updateNormalTask(Task task);

    void deleteEpic(int id);

    void deleteSubtask(int id);

    void deleteNormalTask(int id);

    Task findSubtaskById(int id);

    Task findEpicById(int id);

    Task findNormalTaskById(int id);

    List<Epic> getAllEpics();

    List<Subtask> getAllSubtasks();

    List<Task> getAllNormalTasks();

    List<Subtask> getSubtasksForEpic(int epicId);

    List<Task> getHistory();
}