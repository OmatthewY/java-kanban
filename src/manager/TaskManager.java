package manager;

import tasks.models.Epic;
import tasks.models.Subtask;
import tasks.models.Task;

import java.util.List;
import java.util.ArrayList;

public interface TaskManager {
    Task createEpic(Epic epic) throws Exception;

    Task createSubtask(Subtask subtask) throws Exception;

    Task createNormalTask(Task task) throws Exception;

    void updateEpic(Epic epic);

    Subtask updateSubtask(Subtask subtask) throws Exception;

    Task updateNormalTask(Task task) throws Exception;

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