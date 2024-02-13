package tests;

import manager.TaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.enums.TaskStatus;
import tasks.enums.TaskType;
import tasks.models.Epic;
import tasks.models.Subtask;
import tasks.models.Task;

import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public abstract class TaskManagerTest<T extends TaskManager> {
    public T taskManager;
    protected Epic epic;
    protected Subtask subtaskNewStat1;
    protected Subtask subtaskNewStat2;
    protected Task normalTask;

    @BeforeEach
    void beforeEach() {
        epic = new Epic("Epic 1", "Description of Epic 1", TaskStatus.NEW);
        subtaskNewStat1 = new Subtask("Subtask 1", "Description of Subtask 1", epic.getId());
        subtaskNewStat2 = new Subtask("Subtask 2", "Description of Subtask 2", epic.getId());
        normalTask = new Task("Task 1", "Description of Task 1", TaskType.NORMAL, TaskStatus.NEW);
    }

    @AfterEach
    void afterEach() {
        taskManager.deleteAllEpics();
        taskManager.deleteAllSubtasks();
        taskManager.deleteAllNormalTasks();
    }

    @Test
    void getCreatedEpicById() throws Exception {
        final Epic createdEpic = (Epic) taskManager.createEpic(epic);
        final int epicId = createdEpic.getId();
        Epic savedEpic = taskManager.getEpic(epicId);

        assertNotNull(savedEpic, "Эпик не найден");
        assertEquals(createdEpic, savedEpic, "Эпики не совпадают");
    }

    @Test
    void getCreatedSubtaskById() throws Exception {
        final Epic createdEpic = (Epic) taskManager.createEpic(epic);
        final int epicId = createdEpic.getId();

        Subtask newSubtask = new Subtask("Subtask 1", "Description of Subtask 1", epicId);

        Subtask createdSubtask = (Subtask) taskManager.createSubtask(newSubtask);
        final int subtaskId = createdSubtask.getId();
        Subtask savedSubtask = taskManager.getSubtask(subtaskId);

        assertNotNull(savedSubtask, "Подзадача не найдена");
        assertEquals(newSubtask, savedSubtask, "Подзадачи не совпадают");
    }

    @Test
    void getCreatedNormalTasksById() throws Exception {
        taskManager.createNormalTask(normalTask);
        Task savedNormalTask = taskManager.getNormalTask(normalTask.getId());
        
        assertNotNull(savedNormalTask, "Задача не найдена");
        assertEquals(normalTask, savedNormalTask, "Задачи не совпадают");
    }

    @Test
    void getAllCreatedEpics() throws Exception {
        taskManager.createEpic(epic);

        assertNotNull(taskManager.getAllEpics(), "Эпики не возвращаются");
        assertEquals(1, taskManager.getAllEpics().size(),
                "Возвращается неверное количество эпиков");
        assertEquals(epic, taskManager.getAllEpics().get(0), "Возвращается неверный список эпиков");
    }

    @Test
    void getAllCreatedSubtasks() throws Exception {
        final Epic createdEpic = (Epic) taskManager.createEpic(epic);
        final int epicId = createdEpic.getId();

        Subtask newSubtask = new Subtask("Subtask 1", "Description of Subtask 1", epicId);

        taskManager.createSubtask(newSubtask);

        assertNotNull(taskManager.getAllSubtasks(), "Подзадачи не возвращаются");
        assertEquals(1, taskManager.getAllSubtasks().size(),
                "Возвращается неверное количество подзадач");
        assertEquals(newSubtask, taskManager.getAllSubtasks().get(0), "Возвращается неверный список подзадач");
    }

    @Test
    void getAllCreatedNormalTasks() throws Exception {
        taskManager.createNormalTask(normalTask);

        assertNotNull(taskManager.getAllNormalTasks(), "Задачи не возвращаются");
        assertEquals(1, taskManager.getAllNormalTasks().size(),
                "Возвращается неверное количество задач");
        assertEquals(normalTask, taskManager.getAllNormalTasks().get(0), "Возвращается неверный список задач");
    }

    @Test
    void getEpicSubtasks() throws Exception {
        final Epic createdEpic = (Epic) taskManager.createEpic(epic);
        final int epicId = createdEpic.getId();

        Subtask newSubtask = new Subtask("Subtask 1", "Description of Subtask 1", epicId);

        Subtask createdSubtask = (Subtask) taskManager.createSubtask(newSubtask);
        final int subtaskId = createdSubtask.getId();

        List<Integer> subtasks = new ArrayList<>();
        subtasks.add(subtaskId);

        assertNotNull(epic.getSubtaskIds(),"Подазадачи не возвращаются");
        assertEquals(subtasks, epic.getSubtaskIds(), "Подзадачи не совпадают");
    }

    @Test
    void getEpicWithoutSubtasks() throws Exception {
        taskManager.createEpic(epic);
        assertEquals(0, epic.getSubtaskIds().size(), "Список не пустой");
    }

    @Test
    void getPrioritizedTasks() throws Exception {
        Task normalTask1 = new Task("Task 1", "Description of Task 1", TaskType.NORMAL,
                TaskStatus.NEW, 20,LocalDateTime.of(2022,1,1,1,1,1));
        taskManager.createNormalTask(normalTask1);

        final Epic createdEpic = (Epic) taskManager.createEpic(epic);
        final int epicId = createdEpic.getId();

        Subtask subtask1 = new Subtask("Subtask 1", "Description of Subtask 1", epicId,
                20,LocalDateTime.of(2023,1,1,1,1,1));
        Subtask subtask2 = new Subtask("Subtask 2", "Description of Subtask 2", epicId,
                30, LocalDateTime.of(2024,1,1,1,1,1));

        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);

        ArrayList<Task> expectedTasks = new ArrayList<>();

        expectedTasks.add(normalTask1);
        expectedTasks.add(subtask1);
        expectedTasks.add(subtask2);
        expectedTasks.add(epic);

        assertEquals(expectedTasks, taskManager.getPrioritizedTasks());
    }
}
