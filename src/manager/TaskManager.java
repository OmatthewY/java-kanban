package manager;

import tasks.models.Epic;
import tasks.models.Subtask;
import tasks.models.Task;

import java.util.List;
import java.util.ArrayList;

public interface TaskManager {
    Task createEpic(Epic epic);

    Task createSubtask(Subtask subtask);

    Task createNormalTask(Task task);

    void updateEpic(Epic epic);

    Subtask updateSubtask(Subtask subtask);

    Task updateNormalTask(Task task);

    void deleteEpic(int id);

    void deleteSubtask(int id);

    void deleteAllEpics();

    void deleteAllSubtasks();

    void deleteAllNormalTasks();

    void deleteNormalTask(int id);

    Task findSubtaskById(int id);

    Task findEpicById(int id);

    Task findNormalTaskById(int id);

    List<Epic> getAllEpics();

    List<Subtask> getAllSubtasks();

    List<Task> getAllNormalTasks();

    List<Subtask> getSubtasksForEpic(int epicId);

    Epic getEpic(int id);

    Subtask getSubtask(int id);

    Task getNormalTask(int id);

    List<Task> getHistory();

    ArrayList<Task> getPrioritizedTasks();

    void getEpicTime(Epic epic);
}