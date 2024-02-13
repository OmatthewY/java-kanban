package manager;

import tasks.models.Task;

import java.util.List;

public interface HistoryManager {
    void add(Task task);

    void remove(int id);

    void removeAllTasksOfType(Class<? extends Task> type);
    List<Task> getHistory();
}