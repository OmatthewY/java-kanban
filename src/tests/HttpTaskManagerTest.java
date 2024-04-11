package tests;

import manager.Managers;
import http.HttpTaskManager;
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
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HttpTaskManagerTest extends TaskManagerTest<HttpTaskManager> {
    private HttpTaskManager httpTaskManager;
    private KVServer kVServer;


    @BeforeEach
    void beforeEach() throws IOException, InterruptedException {
        super.beforeEach();
        kVServer = new KVServer();
        kVServer.start();
        httpTaskManager = (HttpTaskManager) Managers.getDefault();
        taskManager = httpTaskManager;
    }

    @AfterEach
    void afterEach() {
        super.afterEach();
        kVServer.stop();
    }

    @Test
    void save() throws IOException, InterruptedException {
        Task normalTask1 = new Task("Task 1", "Task1 description", TaskType.NORMAL, TaskStatus.NEW,
                30, LocalDateTime.of(2024, 2, 11, 15, 0, 0));

        Task normalTask2 = new Task("Task 2", "Task2 description", TaskType.NORMAL,
                TaskStatus.IN_PROGRESS, 30,
                LocalDateTime.of(2024, 2, 11, 16, 0, 0));

        Task normalTask3 = new Task("Task 3", "Task3 description", TaskType.NORMAL,
                TaskStatus.IN_PROGRESS, 30,
                LocalDateTime.of(2024, 2, 11, 17, 0, 0));

        httpTaskManager.createNormalTask(normalTask1);
        httpTaskManager.getNormalTask(normalTask1.getId());

        httpTaskManager.createNormalTask(normalTask2);
        httpTaskManager.getNormalTask(normalTask2.getId());

        httpTaskManager.createNormalTask(normalTask3);
        httpTaskManager.getNormalTask(normalTask3.getId());

        Epic epic1 = new Epic(1,"Epic 1", "Description of Epic 1", TaskStatus.NEW, 30,
                LocalDateTime.of(2024, 1, 1, 1, 1, 1));

        Epic epic2 = new Epic(2,"Epic 2", "Description of Epic 2", TaskStatus.NEW, 40,
                LocalDateTime.of(2023, 1, 1, 1, 1, 1));

        httpTaskManager.createEpic(epic1);
        httpTaskManager.getEpic(epic1.getId());

        httpTaskManager.createEpic(epic2);
        httpTaskManager.getEpic(epic2.getId());

        Subtask subtask1 = new Subtask("Subtask 1 (epic-1)", "Description of Subtask 1 (epic-1)",
                epic1.getId(), 30,
                LocalDateTime.of(2024, 1, 1, 1, 1, 1));

        Subtask subtask2 = new Subtask("Subtask 2 (epic-2)", "Description of Subtask 2 (epic-2)",
                epic2.getId(), 40,
                LocalDateTime.of(2023, 1, 1, 1, 1, 1));

        httpTaskManager.createSubtask(subtask1);
        httpTaskManager.getSubtask(subtask1.getId());

        httpTaskManager.createSubtask(subtask2);
        httpTaskManager.getSubtask(subtask2.getId());

        HttpTaskManager newHttpTaskManager = (HttpTaskManager) Managers.getDefault();

        compareFiles(httpTaskManager, newHttpTaskManager);
    }

    private void compareFiles(HttpTaskManager actualManager, HttpTaskManager expectedManager) {

        int i = 0;
        Task task;
        Task expectedTask;

        while (i < expectedManager.getAllNormalTasks().size()) {
            expectedTask = expectedManager.getAllNormalTasks().get(i);
            task = actualManager.getAllNormalTasks().get(i);

            assertEquals(expectedTask.getName(), task.getName());
            assertEquals(expectedTask.getDescription(), task.getDescription());
            assertEquals(expectedTask.getStatus(), task.getStatus());
            assertEquals(expectedTask.getId(), task.getId());
            assertEquals(expectedTask.getEpicType(), task.getEpicType());

            expectedManager.deleteNormalTask(expectedTask.getId());


            i++;
        }

        i = 0;

        while (i < expectedManager.getHistory().size()) {
            expectedTask = expectedManager.getHistory().get(i);
            task = actualManager.getHistory().get(i);

            assertEquals(expectedTask.getName(), task.getName());
            assertEquals(expectedTask.getDescription(), task.getDescription());
            assertEquals(expectedTask.getStatus(), task.getStatus());
            assertEquals(expectedTask.getId(), task.getId());
            assertEquals(expectedTask.getEpicType(), task.getEpicType());

            expectedManager.getHistory().remove(i);

            i++;
        }

    }
}