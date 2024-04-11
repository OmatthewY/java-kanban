package tests;

import manager.InMemoryTaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.enums.TaskStatus;
import tasks.enums.TaskType;
import tasks.models.Epic;
import tasks.models.Subtask;
import tasks.models.Task;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {
    private Epic epicWithNewAndDoneSubtask;
    private Epic epicWithNewSubtasks;
    private Epic epicWithDoneSubtasks;
    private Subtask subtaskWithDoneStatus;
    private Subtask subtaskWithNewStatus;

    @BeforeEach
    public void beforeEach() throws IOException, InterruptedException {
        super.beforeEach();
        taskManager = new InMemoryTaskManager();

        epicWithNewAndDoneSubtask = new Epic("Epic 1", "Description of Epic 1", TaskStatus.NEW);

        subtaskWithDoneStatus = new Subtask("Done Subtask", "Description of Done Subtask",
                epicWithNewAndDoneSubtask.getId(), TaskStatus.DONE);
        subtaskWithNewStatus = new Subtask("New Subtask", "Description of New Subtask",
                epicWithNewAndDoneSubtask.getId());

        epicWithDoneSubtasks = new Epic("Epic 2", "Description of Epic 2", TaskStatus.NEW);
        epicWithNewSubtasks = new Epic("Epic 3", "Description of Epic 3", TaskStatus.NEW);
    }

    @AfterEach
    public void afterEach() {
        taskManager.deleteAllNormalTasks();
        taskManager.deleteAllSubtasks();
        taskManager.deleteAllEpics();
    }

    @Test
    void createNormalTask() {
        final Task task = new Task("Task 1", "Description of Task 1",
                TaskType.NORMAL, TaskStatus.NEW);

        final Task savedTask = taskManager.createNormalTask(task);

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");

        final List<Task> tasks = taskManager.getAllNormalTasks();

        assertNotNull(tasks, "Задачи не найдены.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task, tasks.get(0), "Задачи не совпадают.");
    }

    @Test
    void createNormalTaskIfNull() {
        assertThrows(NullPointerException.class, () -> {
            taskManager.createNormalTask(null);
        });

        assertEquals(0, taskManager.getAllNormalTasks().size(), "Неверное количество задач");
    }

    @Test
    void createSubtask() {
        taskManager.createEpic(epic);

        assertEquals(1, taskManager.getAllEpics().size(), "Эпик не создался");

        Subtask subtask = new Subtask("Subtask 1", "Description of Subtask 1", epic.getId());
        Subtask savedSubtask = taskManager.createSubtask(subtask);

        assertNotNull(savedSubtask, "Подзадача не найдена");
        assertEquals(epic.getId(), savedSubtask.getEpicId(), "ID эпика не совпадает с ID подзадачи");

        final List<Subtask> subtasks = taskManager.getAllSubtasks();

        assertNotNull(subtasks, "Подзадачи не найдены");
        assertEquals(1, subtasks.size(), "Неверное количество подзадач");
        assertEquals(subtask, subtasks.get(0), "Подзадачи не совпадают");
    }

    @Test
    void createSubtaskIfNull() {
        assertThrows(NullPointerException.class, () -> {
            taskManager.createSubtask(null);
        });

        assertEquals(0, taskManager.getAllSubtasks().size(), "Неверное количество подзадач");
    }

    @Test
    void createEpic() {
        final Epic createdEpic = taskManager.createEpic(epic);
        final int epicId = createdEpic.getId();
        Epic savedEpic = taskManager.getEpic(epicId);

        assertNotNull(savedEpic, "Эпик не найден");
        assertEquals(epic, savedEpic, "Эпики не совпадают");

        final List<Epic> epics = taskManager.getAllEpics();

        assertNotNull(epics, "Эпики не возвращаются");
        assertEquals(1, epics.size(), "Неверное количество эпиков");
        assertEquals(epic, epics.get(0), "Эпики не совпадают");
    }

    @Test
    void createEpicIfNull() {
        assertThrows(NullPointerException.class, () -> {
            taskManager.createEpic(null);
        });

        assertEquals(0, taskManager.getAllEpics().size(), "Неверное количество эпиков");
    }

    @Test
    void updateNormalTask() {
        final Task createdNormalTask = taskManager.createNormalTask(normalTask);
        final int id = createdNormalTask.getId();

        Task updatedNormalTask = taskManager.updateNormalTask(createdNormalTask);

        final Task retrievedTask = taskManager.getNormalTask(id);

        assertNotNull(retrievedTask, "Задача не найдена");
        assertEquals(updatedNormalTask, retrievedTask, "Задачи не совпадают");

        final List<Task> tasks = taskManager.getAllNormalTasks();

        assertNotNull(tasks, "Задачи не возвращаются");
        assertEquals(1, tasks.size(), "Неверное количество задач");
        assertEquals(updatedNormalTask, tasks.get(0), "Задачи не совпадают");
    }

    @Test
    void updateNormalTaskIfNull() {
        assertThrows(NullPointerException.class, () -> {
            taskManager.updateNormalTask(null);
        });

        assertEquals(0, taskManager.getAllNormalTasks().size(), "Неверное количество задач");
    }

    @Test
    void updateSubtask() {
        taskManager.createEpic(epic);

        assertEquals(1, taskManager.getAllEpics().size(), "Эпик отсутствует");

        Subtask subtask = new Subtask("SubName", "SubDescription", epic.getId());
        final Subtask createdSubtask = taskManager.createSubtask(subtask);

        final int subId = createdSubtask.getId();

        Subtask updatedSub = taskManager.updateSubtask(createdSubtask);

        final Subtask retrievedSub = taskManager.getSubtask(subId);

        assertNotNull(retrievedSub, "Подзадача не найдена");
        assertEquals(updatedSub, retrievedSub, "Подзадачи не совпадают");

        final List<Subtask> subtasks = taskManager.getAllSubtasks();

        assertNotNull(subtasks, "Подзадачи не возвращаются");
        assertEquals(1, subtasks.size(), "Неверное количество задач");
        assertEquals(updatedSub, subtasks.get(0), "Задачи не совпадают");
    }

    @Test
    void updateSubtaskIfNull() {
        assertThrows(NullPointerException.class, () -> {
            taskManager.updateSubtask(null);
        });

        assertEquals(0, taskManager.getAllSubtasks().size(), "Неверное количество задач");
    }

    @Test
    void updateEpicWithNewAndDoneSubtasks() {
        final Epic createdEpic = taskManager.createEpic(epicWithNewAndDoneSubtask);
        final int id = createdEpic.getId();

        Subtask newSubtask = new Subtask("NEW Subtask", "Description of NEW Subtask", id);
        Subtask doneSubtask = new Subtask("DONE Subtask", "Description of DONE Subtask", id,
                TaskStatus.DONE);

        taskManager.createSubtask(newSubtask);
        taskManager.createSubtask(doneSubtask);
        taskManager.updateEpic(epicWithNewAndDoneSubtask);

        assertEquals(TaskStatus.IN_PROGRESS, epicWithNewAndDoneSubtask.getStatus());

        final Epic updatedEpic = taskManager.getEpic(epicWithNewAndDoneSubtask.getId());

        assertNotNull(updatedEpic, "Задача не найдена");
        assertEquals(epicWithNewAndDoneSubtask, updatedEpic, "Задачи не совпадают");

        final List<Epic> epics = taskManager.getAllEpics();

        assertNotNull(epics, "Задачи не возвращаются");
        assertEquals(1, epics.size(), "Неверное колиество задач");
        assertEquals(epicWithNewAndDoneSubtask, epics.get(0), "Задачи не совпадают");
    }

    @Test
    void updateEpicWithDoneSubtasks() {
        final Epic createdEpic = taskManager.createEpic(epicWithDoneSubtasks);
        final int id = createdEpic.getId();

        Subtask doneSubtask = new Subtask("DONE Subtask", "Description of DONE Subtask", id,
                TaskStatus.DONE);

        taskManager.createSubtask(doneSubtask);
        taskManager.updateEpic(epicWithDoneSubtasks);

        assertEquals(TaskStatus.DONE, epicWithDoneSubtasks.getStatus());

        final Epic updatedEpic = taskManager.getEpic(epicWithDoneSubtasks.getId());

        assertNotNull(updatedEpic, "Эпик не найден");
        assertEquals(epicWithDoneSubtasks, updatedEpic, "Эпики не совпадают");

        final List<Epic> epics = taskManager.getAllEpics();

        assertNotNull(epics, "Эпики не возвращаются");
        assertEquals(1, epics.size(), "Неверное количесво эпиков");
        assertEquals(epicWithDoneSubtasks, epics.get(0), "Эпики не совпадают");
    }

    @Test
    void updateEpicWithNewSubtasks() {
        final Epic createdEpic = taskManager.createEpic(epicWithNewSubtasks);
        final int id = createdEpic.getId();

        Subtask newSubtask = new Subtask("NEW Subtask", "Description of NEW Subtask", id);

        taskManager.createSubtask(newSubtask);
        taskManager.updateEpic(epicWithNewSubtasks);

        assertEquals(TaskStatus.NEW, epicWithNewSubtasks.getStatus());

        final Epic updatedEpic = taskManager.getEpic(epicWithNewSubtasks.getId());

        assertNotNull(updatedEpic, "Эпик не найден");
        assertEquals(epicWithNewSubtasks, updatedEpic, "Эпики не совпадают");

        final List<Epic> epics = taskManager.getAllEpics();

        assertNotNull(epics, "Эпики не возвращаются");
        assertEquals(1, epics.size(), "Неверное количество эпиков");
        assertEquals(epicWithNewSubtasks, epics.get(0), "Эпики не совпадают");
    }

    @Test
    void updateEpicWithoutSubtasks() {
        taskManager.createEpic(epic);
        taskManager.updateEpic(epic);

        assertEquals(TaskStatus.NEW, epic.getStatus());

        final Epic updatedEpic = taskManager.getEpic(epic.getId());

        assertNotNull(updatedEpic, "Эпик не найден");
        assertEquals(epic, updatedEpic, "Эпики не совпадают");

        final List<Epic> epics = taskManager.getAllEpics();

        assertNotNull(epics, "Эпики не возвращаются");
        assertEquals(1, epics.size(), "Неверное количество эпиков");
        assertEquals(0, epic.getSubtaskIds().size(), "Неверное количество подзадач");
        assertEquals(epic, epics.get(0), "Эпики не равны");
    }

    @Test
    void updateEpicIfNull() {
        assertThrows(NullPointerException.class, () -> {
            taskManager.updateEpic(null);
        });

        assertEquals(0, taskManager.getAllEpics().size(), "Неверное количество задач");
    }

    @Test
    void deleteAllNormalTasks() {
        taskManager.createNormalTask(normalTask);
        taskManager.deleteAllNormalTasks();

        assertEquals(0, taskManager.getAllNormalTasks().size(), "Список задач не очистился");
    }

    @Test
    void deleteAllSubtasks() {
        final Epic createdEpic = taskManager.createEpic(epic);
        final int id = createdEpic.getId();

        Subtask newSubtask1 = new Subtask("NEW Subtask 1", "Description of NEW Subtask 1", id);
        Subtask newSubtask2 = new Subtask("NEW Subtask 2", "Description of NEW Subtask 2", id);

        taskManager.createSubtask(newSubtask1);
        taskManager.createSubtask(newSubtask2);
        taskManager.deleteAllSubtasks();

        assertEquals(0, taskManager.getAllSubtasks().size(), "Список подзадач не очистился");
    }

    @Test
    void deleteAllEpics() {
        taskManager.createEpic(epic);
        taskManager.deleteAllEpics();

        assertEquals(0, taskManager.getAllEpics().size(), "Список задач не очистился");
    }

    @Test
    void deleteNormalTaskById() {
        Task normalTask1 = new Task("Normal Task 1", "Desceription of Normal Task 1",
                TaskType.NORMAL, TaskStatus.NEW);

        final Task createdNormalTask = taskManager.createNormalTask(normalTask);
        final int id = createdNormalTask.getId();

        taskManager.createNormalTask(normalTask1);
        Task removedNormalTask = taskManager.getNormalTask(id);

        assertNotNull(removedNormalTask, "Задача не найдена");
        assertEquals(normalTask, removedNormalTask, "Задачи не совпадают");

        taskManager.deleteNormalTask(id);

        assertEquals(1, taskManager.getAllNormalTasks().size(), "Задача не удалилась");
    }

    @Test
    void deleteSubtaskById() {
        final Epic createdEpic = taskManager.createEpic(epic);
        final int id = createdEpic.getId();

        Subtask newSubtask1 = new Subtask("NEW Subtask 1", "Description of NEW Subtask 1", id);
        Subtask newSubtask2 = new Subtask("NEW Subtask 2", "Description of NEW Subtask 2", id);

        taskManager.createSubtask(newSubtask1);
        taskManager.createSubtask(newSubtask2);

        Subtask removedSubtask = taskManager.getSubtask(newSubtask2.getId());

        assertNotNull(removedSubtask, "Подзадача не найдена");
        assertEquals(newSubtask2, removedSubtask, "Подзадачи не совпадают");

        taskManager.deleteSubtask(newSubtask2.getId());

        assertEquals(1, taskManager.getAllSubtasks().size(), "Подзадача не удалилась из списка");
    }

    @Test
    void deleteEpicById() {
        final Epic createdEpic1 = taskManager.createEpic(epic);
        final int id1 = createdEpic1.getId();

        final Epic createdEpic2 = taskManager.createEpic(epicWithDoneSubtasks);

        Epic removedEpic = taskManager.getEpic(epic.getId());

        assertNotNull(removedEpic, "Эпик не найден");
        assertEquals(epic, removedEpic, "Эпики не совпадают");

        taskManager.deleteEpic(id1);

        assertEquals(1, taskManager.getAllEpics().size(), "Задача не удалилась из списка");
    }
}