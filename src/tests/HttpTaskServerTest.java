package tests;

import com.google.gson.Gson;
import manager.InMemoryHistoryManager;
import manager.Managers;
import manager.TaskManager;
import server.HttpTaskServer;
import server.KVServer;
import tasks.enums.TaskType;
import tasks.models.Epic;
import tasks.models.Subtask;
import tasks.models.Task;
import tasks.enums.TaskStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HttpTaskServerTest {

    private InMemoryHistoryManager historyManager;
    private HttpTaskServer taskServer;
    private KVServer kvServer;
    private HttpClient client;
    private Gson gson;
    private TaskManager taskManager;


    @BeforeEach
    void beforeEach() throws IOException, InterruptedException {
        kvServer = new KVServer();
        kvServer.start();
        taskServer = new HttpTaskServer();
        taskServer.start();
        client = HttpClient.newHttpClient();
        gson = Managers.getGson();
        taskManager = Managers.getDefault();
    }

    @AfterEach
    void afterEach() {
        taskServer.stop();
        kvServer.stop();
        taskManager.deleteAllSubtasks();
        taskManager.deleteAllNormalTasks();
        taskManager.deleteAllEpics();
    }

    @Test
    void createNormalTask_Return201StatusCode() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:5000/tasks");
        Task task = new Task(1, "TaskName", "TaskDescription", TaskType.NORMAL ,TaskStatus.NEW);
        String jsonTask = gson.toJson(task);

        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(jsonTask);
        HttpRequest request = HttpRequest.newBuilder()
                .POST(body)
                .uri(url)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());
    }

    @Test
    void createEpic_Return201StatusCode() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:5000/epics");
        Epic epic = new Epic(1, "EpicName", "EpicDescription", TaskStatus.NEW);
        String jsonTask = gson.toJson(epic);

        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(jsonTask);
        HttpRequest request = HttpRequest.newBuilder()
                .POST(body)
                .uri(url)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());
    }

    @Test
    void createSubtask_Return201StatusCode() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:5000/epics");
        Epic epic = new Epic(1, "EpicName", "EpicDescription", TaskStatus.NEW);
        String jsonTask = gson.toJson(epic);

        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(jsonTask);
        HttpRequest request = HttpRequest.newBuilder()
                .POST(body)
                .uri(url)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());

        URI subUrl = URI.create("http://localhost:5000/subtasks");
        Subtask subtask = new Subtask(2,"SubName", "SubDescription", 1);
        String jsonSubtask = gson.toJson(subtask);

        final HttpRequest.BodyPublisher subBody = HttpRequest.BodyPublishers.ofString(jsonSubtask);
        HttpRequest subRequest = HttpRequest.newBuilder()
                .POST(subBody)
                .uri(subUrl)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> subResponse = client.send(subRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, subResponse.statusCode());
    }

    @Test
    void updateNormalTask_Return201StatusCode() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:5000/tasks");
        Task task = new Task(1, "TaskName", "TaskDescription", TaskType.NORMAL, TaskStatus.NEW);
        String jsonTask = gson.toJson(task);

        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(jsonTask);
        HttpRequest request = HttpRequest.newBuilder()
                .POST(body)
                .uri(url)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());

        Task updTask = new Task(task.getId(), "UpdTaskName", "UpdTaskDescription", TaskType.NORMAL,
                TaskStatus.NEW);
        String jsonUpdTask = gson.toJson(updTask);

        URI updUrl = URI.create("http://localhost:5000/tasks/?id=" + updTask.getId());

        final HttpRequest.BodyPublisher updBody = HttpRequest.BodyPublishers.ofString(jsonUpdTask);
        HttpRequest updRequest = HttpRequest.newBuilder()
                .POST(updBody)
                .uri(updUrl)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> updResponse = client.send(updRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, updResponse.statusCode());
    }

    @Test
    void updateSubtask_Return201StatusCode() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:5000/epics");
        Epic epic = new Epic(1,"EpicName", "EpicDescription", TaskStatus.NEW);
        String jsonTask = gson.toJson(epic);

        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(jsonTask);
        HttpRequest request = HttpRequest.newBuilder()
                .POST(body)
                .uri(url)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());

        URI subUrl = URI.create("http://localhost:5000/subtasks");
        Subtask subtask = new Subtask(2,"SubName", "SubDescription", 1);
        String jsonSubtask = gson.toJson(subtask);

        final HttpRequest.BodyPublisher subBody = HttpRequest.BodyPublishers.ofString(jsonSubtask);
        HttpRequest subRequest = HttpRequest.newBuilder()
                .POST(subBody)
                .uri(subUrl)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> subResponse = client.send(subRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, subResponse.statusCode());


        Subtask updSubtask = new Subtask(2,"UpdSubtaskName", "UpdSubtaskDescription", 1);
        String jsonUpdSubtask = gson.toJson(updSubtask);

        URI updUrl = URI.create("http://localhost:5000/subtasks/?id=" + updSubtask.getId());


        final HttpRequest.BodyPublisher updBody = HttpRequest.BodyPublishers.ofString(jsonUpdSubtask);
        HttpRequest updRequest = HttpRequest.newBuilder()
                .POST(updBody)
                .uri(updUrl)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> updResponse = client.send(updRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, updResponse.statusCode());
    }

    @Test
    void getAllNormalTasks_Return200StatusCode() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:5000/tasks");
        Task task = new Task(1,"TaskName", "TaskDescription", TaskType.NORMAL, TaskStatus.NEW);
        String jsonTask = gson.toJson(task);

        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(jsonTask);
        HttpRequest request = HttpRequest.newBuilder()
                .POST(body)
                .uri(url)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());

        Task task2 = new Task(2, "Task2Name", "Task2Description", TaskType.NORMAL, TaskStatus.NEW);
        String jsonTask2 = gson.toJson(task2);

        final HttpRequest.BodyPublisher body2 = HttpRequest.BodyPublishers.ofString(jsonTask2);
        HttpRequest request2 = HttpRequest.newBuilder()
                .POST(body2)
                .uri(url)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response2.statusCode());

        final HttpRequest getRequest = HttpRequest.newBuilder()
                .GET()
                .uri(url)
                .build();

        HttpResponse<String> getResponse = client.send(getRequest, HttpResponse.BodyHandlers.ofString());

        String tasks = gson.toJson(taskManager.getAllNormalTasks());

        assertEquals(200, getResponse.statusCode());
        assertEquals(tasks, getResponse.body());
    }

    @Test
    void getNormalTaskById_Return200StatusCode() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:5000/tasks");
        Task task = new Task(1, "TaskName", "TaskDescription", TaskType.NORMAL, TaskStatus.NEW);
        String jsonTask = gson.toJson(task);

        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(jsonTask);
        HttpRequest request = HttpRequest.newBuilder()
                .POST(body)
                .uri(url)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());

        Task task2 = new Task(2, "Task2Name", "Task2Description", TaskType.NORMAL, TaskStatus.NEW);
        String jsonTask2 = gson.toJson(task2);

        final HttpRequest.BodyPublisher body2 = HttpRequest.BodyPublishers.ofString(jsonTask2);
        HttpRequest request2 = HttpRequest.newBuilder()
                .POST(body2)
                .uri(url)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response2.statusCode());

        URI uri = URI.create("http://localhost:5000/tasks/?id=2");

        final HttpRequest getRequest = HttpRequest.newBuilder()
                .GET()
                .uri(uri)
                .build();

        HttpResponse<String> getResponse = client.send(getRequest, HttpResponse.BodyHandlers.ofString());

        String ExpectedTask = gson.toJson(task2);

        assertEquals(200, getResponse.statusCode());
        assertEquals(ExpectedTask, getResponse.body());
    }

    @Test
    void getAllEpics_Return200StatusCode() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:5000/epics");
        Epic epic = new Epic(1, "EpicName", "EpicDescription", TaskStatus.NEW);
        String jsonEpic = gson.toJson(epic);

        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(jsonEpic);
        HttpRequest request = HttpRequest.newBuilder()
                .POST(body)
                .uri(url)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());

        Epic epic2 = new Epic(2, "Epic2Name", "Epic2Description", TaskStatus.NEW);
        String jsonEpic2 = gson.toJson(epic2);

        final HttpRequest.BodyPublisher body2 = HttpRequest.BodyPublishers.ofString(jsonEpic2);
        HttpRequest request2 = HttpRequest.newBuilder()
                .POST(body2)
                .uri(url)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response2.statusCode());

        final HttpRequest getRequest = HttpRequest.newBuilder()
                .GET()
                .uri(url)
                .build();

        HttpResponse<String> getResponse = client.send(getRequest, HttpResponse.BodyHandlers.ofString());

        String tasks = gson.toJson(taskManager.getAllEpics());

        assertEquals(200, getResponse.statusCode());
        assertEquals(tasks, getResponse.body());
    }

    @Test
    void getEpicById_Return200StatusCode() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:5000/epics");
        Epic epic = new Epic(1, "EpicName", "EpicDescription", TaskStatus.NEW);
        String jsonEpic = gson.toJson(epic);

        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(jsonEpic);
        HttpRequest request = HttpRequest.newBuilder()
                .POST(body)
                .uri(url)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());

        Epic epic2 = new Epic(2, "Epic2Name", "Epic2Description", TaskStatus.NEW);
        String jsonEpic2 = gson.toJson(epic2);

        final HttpRequest.BodyPublisher body2 = HttpRequest.BodyPublishers.ofString(jsonEpic2);
        HttpRequest request2 = HttpRequest.newBuilder()
                .POST(body2)
                .uri(url)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response2.statusCode());

        URI uri = URI.create("http://localhost:5000/epics/?id=" + epic.getId());

        final HttpRequest getRequest = HttpRequest.newBuilder()
                .GET()
                .uri(uri)
                .build();

        HttpResponse<String> getResponse = client.send(getRequest, HttpResponse.BodyHandlers.ofString());

        String expectedEpic = gson.toJson(taskManager.getEpic(epic.getId()));

        assertEquals(200, getResponse.statusCode());
        assertEquals(expectedEpic, getResponse.body());
    }

    @Test
    void getAllSubtasks_Return200StatusCode() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:5000/epics");
        Epic epic = new Epic(1, "EpicName", "EpicDescription", TaskStatus.NEW);
        String jsonTask = gson.toJson(epic);

        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(jsonTask);
        HttpRequest request = HttpRequest.newBuilder()
                .POST(body)
                .uri(url)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());

        URI subUrl = URI.create("http://localhost:5000/subtasks");
        Subtask subtask = new Subtask("SubName", "SubDescription", epic.getId());
        String jsonSubtask = gson.toJson(subtask);

        final HttpRequest.BodyPublisher subBody = HttpRequest.BodyPublishers.ofString(jsonSubtask);
        HttpRequest subRequest = HttpRequest.newBuilder()
                .POST(subBody)
                .uri(subUrl)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> subResponse = client.send(subRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, subResponse.statusCode());

        Subtask subtask2 = new Subtask("Sub2Name", "Sub2Description", epic.getId());
        String jsonSubtask2 = gson.toJson(subtask2);

        final HttpRequest.BodyPublisher subBody2 = HttpRequest.BodyPublishers.ofString(jsonSubtask2);
        HttpRequest subRequest2 = HttpRequest.newBuilder()
                .POST(subBody2)
                .uri(subUrl)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> subResponse2 = client.send(subRequest2, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, subResponse2.statusCode());

        final HttpRequest getRequest = HttpRequest.newBuilder()
                .GET()
                .uri(subUrl)
                .build();

        HttpResponse<String> getResponse = client.send(getRequest, HttpResponse.BodyHandlers.ofString());

        String subtasks = gson.toJson(taskManager.getAllSubtasks());

        assertEquals(200, getResponse.statusCode());
        assertEquals(subtasks, getResponse.body());
    }

    @Test
    void getSubtaskById_Return200StatusCode() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:5000/epics");
        Epic epic = new Epic(1, "EpicName", "EpicDescription", TaskStatus.NEW);
        String jsonTask = gson.toJson(epic);

        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(jsonTask);
        HttpRequest request = HttpRequest.newBuilder()
                .POST(body)
                .uri(url)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());

        URI subUrl = URI.create("http://localhost:5000/subtasks");
        Subtask subtask = new Subtask(2, "SubName", "SubDescription", epic.getId());
        String jsonSubtask = gson.toJson(subtask);

        final HttpRequest.BodyPublisher subBody = HttpRequest.BodyPublishers.ofString(jsonSubtask);
        HttpRequest subRequest = HttpRequest.newBuilder()
                .POST(subBody)
                .uri(subUrl)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> subResponse = client.send(subRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, subResponse.statusCode());

        Subtask subtask2 = new Subtask(3, "Sub2Name", "Sub2Description", epic.getId());
        String jsonSubtask2 = gson.toJson(subtask2);

        final HttpRequest.BodyPublisher subBody2 = HttpRequest.BodyPublishers.ofString(jsonSubtask2);
        HttpRequest subRequest2 = HttpRequest.newBuilder()
                .POST(subBody2)
                .uri(subUrl)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> subResponse2 = client.send(subRequest2, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, subResponse2.statusCode());

        URI uri = URI.create("http://localhost:5000/subtasks/?id=" + subtask2.getId());

        final HttpRequest getRequest = HttpRequest.newBuilder()
                .GET()
                .uri(uri)
                .build();

        HttpResponse<String> getResponse = client.send(getRequest, HttpResponse.BodyHandlers.ofString());

        String subtaskJson = gson.toJson(taskManager.getSubtask(subtask2.getId()));

        assertEquals(200, getResponse.statusCode());
        assertEquals(subtaskJson, getResponse.body());
    }

    @Test
    void getEpicSubtasks_Return200StatusCode() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:5000/epics");
        Epic epic = new Epic(1, "EpicName", "EpicDescription", TaskStatus.NEW);
        String jsonTask = gson.toJson(epic);

        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(jsonTask);
        HttpRequest request = HttpRequest.newBuilder()
                .POST(body)
                .uri(url)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());

        URI subUrl = URI.create("http://localhost:5000/subtasks");
        Subtask subtask = new Subtask(2, "SubName", "SubDescription", epic.getId());
        String jsonSubtask = gson.toJson(subtask);

        final HttpRequest.BodyPublisher subBody = HttpRequest.BodyPublishers.ofString(jsonSubtask);
        HttpRequest subRequest = HttpRequest.newBuilder()
                .POST(subBody)
                .uri(subUrl)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> subResponse = client.send(subRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, subResponse.statusCode());

        Subtask subtask2 = new Subtask(3, "Sub2Name", "Sub2Description", epic.getId());
        String jsonSubtask2 = gson.toJson(subtask2);

        final HttpRequest.BodyPublisher subBody2 = HttpRequest.BodyPublishers.ofString(jsonSubtask2);
        HttpRequest subRequest2 = HttpRequest.newBuilder()
                .POST(subBody2)
                .uri(subUrl)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> subResponse2 = client.send(subRequest2, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, subResponse2.statusCode());

        URI uri = URI.create("http://localhost:5000/epics/subtasks/?id=" + epic.getId());

        final HttpRequest getRequest = HttpRequest.newBuilder()
                .GET()
                .uri(uri)
                .build();

        HttpResponse<String> getResponse = client.send(getRequest, HttpResponse.BodyHandlers.ofString());

        String subtaskJson = gson.toJson(taskManager.getSubtasksForEpic(epic.getId()));

        assertEquals(200, getResponse.statusCode());
        assertEquals(subtaskJson, getResponse.body());
    }

    @Test
    void deleteAllNormalTasks_Return200StatusCode() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:5000/tasks");
        Task task = new Task(1, "TaskName", "TaskDescription", TaskType.NORMAL, TaskStatus.NEW);
        String jsonTask = gson.toJson(task);

        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(jsonTask);
        HttpRequest request = HttpRequest.newBuilder()
                .POST(body)
                .uri(url)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());

        Task task2 = new Task(2, "Task2Name", "Task2Description", TaskType.NORMAL, TaskStatus.NEW);
        String jsonTask2 = gson.toJson(task2);

        final HttpRequest.BodyPublisher body2 = HttpRequest.BodyPublishers.ofString(jsonTask2);
        HttpRequest request2 = HttpRequest.newBuilder()
                .POST(body2)
                .uri(url)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response2.statusCode());

        final HttpRequest getRequest = HttpRequest.newBuilder()
                .DELETE()
                .uri(url)
                .build();

        HttpResponse<String> getResponse = client.send(getRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, getResponse.statusCode());
        assertEquals(0, taskManager.getAllNormalTasks().size());
    }

    @Test
    void deleteNormalTaskById_Return200StatusCode() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:5000/tasks");
        Task task = new Task(1, "TaskName", "TaskDescription", TaskType.NORMAL, TaskStatus.NEW);
        String jsonTask = gson.toJson(task);

        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(jsonTask);
        HttpRequest request = HttpRequest.newBuilder()
                .POST(body)
                .uri(url)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());

        Task task2 = new Task(2, "Task2Name", "Task2Description", TaskType.NORMAL, TaskStatus.NEW);
        String jsonTask2 = gson.toJson(task2);

        final HttpRequest.BodyPublisher body2 = HttpRequest.BodyPublishers.ofString(jsonTask2);
        HttpRequest request2 = HttpRequest.newBuilder()
                .POST(body2)
                .uri(url)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response2.statusCode());

        URI uri = URI.create("http://localhost:5000/tasks/?id=" + task2.getId());

        final HttpRequest delRequest = HttpRequest.newBuilder()
                .DELETE()
                .uri(uri)
                .build();

        HttpResponse<String> delResponse = client.send(delRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, delResponse.statusCode());
        assertFalse(taskManager.getAllNormalTasks().contains(task2));
    }

    @Test
    void deleteAllEpics_Return200StatusCode() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:5000/epics");
        Epic epic = new Epic(1, "EpicName", "EpicDescription", TaskStatus.NEW);
        String jsonEpic = gson.toJson(epic);

        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(jsonEpic);
        HttpRequest request = HttpRequest.newBuilder()
                .POST(body)
                .uri(url)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());

        Epic epic2 = new Epic(2, "Epic2Name", "Epic2Description", TaskStatus.NEW);
        String jsonEpic2 = gson.toJson(epic2);

        final HttpRequest.BodyPublisher body2 = HttpRequest.BodyPublishers.ofString(jsonEpic2);
        HttpRequest request2 = HttpRequest.newBuilder()
                .POST(body2)
                .uri(url)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response2.statusCode());

        final HttpRequest getRequest = HttpRequest.newBuilder()
                .DELETE()
                .uri(url)
                .build();

        HttpResponse<String> getResponse = client.send(getRequest, HttpResponse.BodyHandlers.ofString());


        assertEquals(200, getResponse.statusCode());
        assertEquals(0, taskManager.getAllEpics().size());
    }

    @Test
    void deleteEpicById_Return200StatusCode() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:5000/epics");
        Epic epic = new Epic(1, "EpicName", "EpicDescription", TaskStatus.NEW);
        String jsonEpic = gson.toJson(epic);

        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(jsonEpic);
        HttpRequest request = HttpRequest.newBuilder()
                .POST(body)
                .uri(url)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());

        Epic epic2 = new Epic(2, "Epic2Name", "Epic2Description", TaskStatus.NEW);
        String jsonEpic2 = gson.toJson(epic2);

        final HttpRequest.BodyPublisher body2 = HttpRequest.BodyPublishers.ofString(jsonEpic2);
        HttpRequest request2 = HttpRequest.newBuilder()
                .POST(body2)
                .uri(url)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response2.statusCode());

        URI uri = URI.create("http://localhost:5000/epics/?id=" + epic.getId());

        final HttpRequest getRequest = HttpRequest.newBuilder()
                .DELETE()
                .uri(uri)
                .build();

        HttpResponse<String> getResponse = client.send(getRequest, HttpResponse.BodyHandlers.ofString());


        assertEquals(200, getResponse.statusCode());
        assertFalse(taskManager.getAllEpics().contains(epic));
    }

    @Test
    void deleteAllSubtasks_Return200StatusCode() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:5000/epics");
        Epic epic = new Epic(1, "EpicName", "EpicDescription", TaskStatus.NEW);
        String jsonTask = gson.toJson(epic);

        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(jsonTask);
        HttpRequest request = HttpRequest.newBuilder()
                .POST(body)
                .uri(url)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());

        URI subUrl = URI.create("http://localhost:5000/subtasks");
        Subtask subtask = new Subtask("SubName", "SubDescription", epic.getId());
        String jsonSubtask = gson.toJson(subtask);

        final HttpRequest.BodyPublisher subBody = HttpRequest.BodyPublishers.ofString(jsonSubtask);
        HttpRequest subRequest = HttpRequest.newBuilder()
                .POST(subBody)
                .uri(subUrl)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> subResponse = client.send(subRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, subResponse.statusCode());

        Subtask subtask2 = new Subtask("Sub2Name", "Sub2Description", epic.getId());
        String jsonSubtask2 = gson.toJson(subtask2);

        final HttpRequest.BodyPublisher subBody2 = HttpRequest.BodyPublishers.ofString(jsonSubtask2);
        HttpRequest subRequest2 = HttpRequest.newBuilder()
                .POST(subBody2)
                .uri(subUrl)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> subResponse2 = client.send(subRequest2, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, subResponse2.statusCode());

        final HttpRequest getRequest = HttpRequest.newBuilder()
                .DELETE()
                .uri(subUrl)
                .build();

        HttpResponse<String> getResponse = client.send(getRequest, HttpResponse.BodyHandlers.ofString());


        assertEquals(200, getResponse.statusCode());
        assertEquals(0, taskManager.getAllSubtasks().size());
    }

    @Test
    void deleteSubtaskById_Return200StatusCode() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:5000/epics");
        Epic epic = new Epic(1, "EpicName", "EpicDescription", TaskStatus.NEW);
        String jsonTask = gson.toJson(epic);

        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(jsonTask);
        HttpRequest request = HttpRequest.newBuilder()
                .POST(body)
                .uri(url)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());

        URI subUrl = URI.create("http://localhost:5000/subtasks");
        Subtask subtask = new Subtask(2, "SubName", "SubDescription", epic.getId());
        String jsonSubtask = gson.toJson(subtask);

        final HttpRequest.BodyPublisher subBody = HttpRequest.BodyPublishers.ofString(jsonSubtask);
        HttpRequest subRequest = HttpRequest.newBuilder()
                .POST(subBody)
                .uri(subUrl)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> subResponse = client.send(subRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, subResponse.statusCode());

        Subtask subtask2 = new Subtask(3, "Sub2Name", "Sub2Description", epic.getId());
        String jsonSubtask2 = gson.toJson(subtask2);

        final HttpRequest.BodyPublisher subBody2 = HttpRequest.BodyPublishers.ofString(jsonSubtask2);
        HttpRequest subRequest2 = HttpRequest.newBuilder()
                .POST(subBody2)
                .uri(subUrl)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> subResponse2 = client.send(subRequest2, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, subResponse2.statusCode());

        URI deleteSubUrl = URI.create("http://localhost:5000/subtasks/?id=" + subtask2.getId());

        final HttpRequest getRequest = HttpRequest.newBuilder()
                .DELETE()
                .uri(deleteSubUrl)
                .build();

        HttpResponse<String> getResponse = client.send(getRequest, HttpResponse.BodyHandlers.ofString());


        assertEquals(200, getResponse.statusCode());
        assertFalse(taskManager.getAllSubtasks().contains(subtask2));
    }

    @Test
    void deleteEpicSubtasks_Return200StatusCode() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:5000/epics");
        Epic epic = new Epic(1, "EpicName", "EpicDescription", TaskStatus.NEW);
        String jsonTask = gson.toJson(epic);

        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(jsonTask);
        HttpRequest request = HttpRequest.newBuilder()
                .POST(body)
                .uri(url)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());

        URI subUrl = URI.create("http://localhost:5000/subtasks");
        Subtask subtask = new Subtask(2, "SubName", "SubDescription", epic.getId());
        String jsonSubtask = gson.toJson(subtask);

        final HttpRequest.BodyPublisher subBody = HttpRequest.BodyPublishers.ofString(jsonSubtask);
        HttpRequest subRequest = HttpRequest.newBuilder()
                .POST(subBody)
                .uri(subUrl)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> subResponse = client.send(subRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, subResponse.statusCode());

        Subtask subtask2 = new Subtask(3, "Sub2Name", "Sub2Description", epic.getId());
        String jsonSubtask2 = gson.toJson(subtask2);

        final HttpRequest.BodyPublisher subBody2 = HttpRequest.BodyPublishers.ofString(jsonSubtask2);
        HttpRequest subRequest2 = HttpRequest.newBuilder()
                .POST(subBody2)
                .uri(subUrl)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> subResponse2 = client.send(subRequest2, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, subResponse2.statusCode());

        URI uri = URI.create("http://localhost:5000/epics/subtasks?id=" + epic.getId());

        final HttpRequest getRequest = HttpRequest.newBuilder()
                .DELETE()
                .uri(uri)
                .build();

        HttpResponse<String> getResponse = client.send(getRequest, HttpResponse.BodyHandlers.ofString());


        assertEquals(200, getResponse.statusCode());
        assertEquals(0, taskManager.getEpic(epic.getId()).getSubtaskIds().size());
    }

    @Test
    void getPrioritizedTasks() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:5000/tasks");
        Task task = new Task(1, "TaskName", "TaskDescription", TaskType.NORMAL, TaskStatus.NEW,
                30, LocalDateTime.of(2022, 1, 1, 1, 1, 1));
        String jsonTask = gson.toJson(task);

        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(jsonTask);
        HttpRequest request = HttpRequest.newBuilder()
                .POST(body)
                .uri(url)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Task task2 = new Task(2, "TaskName2", "TaskDescription2", TaskType.NORMAL, TaskStatus.NEW,
                30, LocalDateTime.of(2021, 2, 1, 1, 1, 1));
        String jsonTask2 = gson.toJson(task2);

        final HttpRequest.BodyPublisher body2 = HttpRequest.BodyPublishers.ofString(jsonTask2);
        HttpRequest request2 = HttpRequest.newBuilder()
                .POST(body2)
                .uri(url)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());

        URI priorityUrl = URI.create("http://localhost:5000/prioritized");
        HttpRequest priorityRequest = HttpRequest.newBuilder()
                .GET()
                .uri(priorityUrl)
                .build();
        HttpResponse<String> priorityResponse = client.send(priorityRequest, HttpResponse.BodyHandlers.ofString());

        List<Task> actual = new ArrayList<>();
        actual.add(task2);
        actual.add(task);

        String expectedTasks = gson.toJson(actual);


        assertEquals(200, priorityResponse.statusCode());
        assertEquals(expectedTasks, priorityResponse.body());
    }

    @Test
    void getTasksHistory() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:5000/tasks");
        Task task = new Task(1, "TaskName", "TaskDescription", TaskType.NORMAL, TaskStatus.NEW);
        String jsonTask = gson.toJson(task);

        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(jsonTask);
        HttpRequest request = HttpRequest.newBuilder()
                .POST(body)
                .uri(url)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Task task2 = new Task(2, "TaskName2", "TaskDescription2", TaskType.NORMAL, TaskStatus.NEW);
        String jsonTask2 = gson.toJson(task2);

        final HttpRequest.BodyPublisher body2 = HttpRequest.BodyPublishers.ofString(jsonTask2);
        HttpRequest request2 = HttpRequest.newBuilder()
                .POST(body2)
                .uri(url)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());

        URI priorityUrl = URI.create("http://localhost:5000/history");
        HttpRequest priorityRequest = HttpRequest.newBuilder()
                .GET()
                .uri(priorityUrl)
                .build();

        HttpResponse<String> historyResponse = client.send(priorityRequest, HttpResponse.BodyHandlers.ofString());

        List<Task> actual = new ArrayList<>();
        actual.add(task);
        actual.add(task2);

        String expectedTasks = gson.toJson(actual);


        assertEquals(200, historyResponse.statusCode());
        assertEquals(expectedTasks, historyResponse.body());
    }
}
