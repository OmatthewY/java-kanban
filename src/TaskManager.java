import tasks.enums.TaskStatus;
import tasks.models.Task;

import java.util.List;

public interface TaskManager {
    Task createEpic(String name, String description);

    Task createSubtask(String name, String description, int epicId);

    Task createNormalTask(String name, String description);

    void updateEpic(int id, String name, String description, TaskStatus status);

    void updateSubtask(int id, String name, String description, TaskStatus status);

    void updateNormalTask(int id, String name, String description, TaskStatus status);

    void deleteEpic(int id);

    void deleteSubtask(int id);

    void deleteNormalTask(int id);

    Task findTaskById(int id);

    List<Task> getAllEpics();

    List<Task> getAllSubtasks();

    List<Task> getAllNormalTasks();

    List<Task> getSubtasksForEpic(int epicId);

    List<Task> getHistory();
}