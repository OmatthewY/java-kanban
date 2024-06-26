package http;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import client.KVTaskClient;
import manager.Managers;
import manager.FileBackedTasksManager;
import tasks.models.Epic;
import tasks.models.Subtask;
import tasks.models.Task;

import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.TreeSet;

public class HttpTaskManager extends FileBackedTasksManager {
    private KVTaskClient client;
    private final Gson gson;

    public HttpTaskManager(URI uri) throws IOException, InterruptedException {
        super(null);
        client = new KVTaskClient(uri);
        gson = Managers.getGson();
        load();
    }

    @Override
    protected void save() {
        String jsonTasks = gson.toJson(normalTasksMap);
        client.put("tasks", jsonTasks);

        String jsonEpics = gson.toJson(epicsMap);
        client.put("epics", jsonEpics);

        String jsonSubtasks = gson.toJson(subtasksMap);
        client.put("subtasks", jsonSubtasks);

        String jsonHistory = gson.toJson(getHistory());
        client.put("history", jsonHistory);

        String jsonPrioritizedTasks = gson.toJson(getPrioritizedTasks());
        client.put("prioritizedTasks", jsonPrioritizedTasks);
    }

    private void load() {
        try {
            String taskFromJson = client.load("tasks");
            if (taskFromJson != null && !taskFromJson.isBlank()) {
                normalTasksMap = gson.fromJson(taskFromJson, new TypeToken<HashMap<Integer, Task>>() {
                }.getType());
            }

            String epicFromJson = client.load("epics");
            if (epicFromJson != null && !epicFromJson.isBlank()) {
                epicsMap = gson.fromJson(taskFromJson, new TypeToken<HashMap<Integer, Epic>>() {
                }.getType());
            }

            String subsFromJson = client.load("epics");
            if (subsFromJson != null && !subsFromJson.isBlank()) {
                subtasksMap = gson.fromJson(taskFromJson, new TypeToken<HashMap<Integer, Subtask>>() {
                }.getType());
            }

            String historyFromJson = client.load("history");
            if (historyFromJson != null && !historyFromJson.isBlank()) {
                List<Task> history = gson.fromJson(historyFromJson, new TypeToken<List<Task>>() {
                }.getType());
                for (Task task : history) {
                    historyManager.add(task);
                }
            }

            String prioritizedTasksFromJson = client.load("prioritizedTasks");
            if (prioritizedTasksFromJson != null && !prioritizedTasksFromJson.isBlank()) {
                tasksTreeSet = gson.fromJson(prioritizedTasksFromJson, new TypeToken<TreeSet<Task>>() {
                }.getType());
            }
        } catch (NullPointerException e) {
            System.out.println("Пока что нечего загружать");
        }

    }
}
