package tests;

import manager.InMemoryTaskManager;
import tasks.enums.TaskStatus;
import tasks.models.Epic;
import tasks.models.Subtask;
import tasks.models.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class EpicTest  {
    InMemoryTaskManager taskManager = new InMemoryTaskManager();
    Epic epic = new Epic(1, "Epic 1", "Description of Epic 1", TaskStatus.NEW);

    @BeforeEach
    public void beforeEach() throws Exception {
        taskManager.createEpic(epic);
        taskManager.deleteAllSubtasks();
    }

    @Test
    public void shouldReturnCorrectStartTime() throws Exception {
        Subtask subtaskTest1 = new Subtask(2,"Subtask 1", "Description of Subtask 1", 1,
                10, LocalDateTime.of(2023, 1, 1, 1, 1));
        taskManager.createSubtask(subtaskTest1);

        Subtask subtaskTest2 = new Subtask(3, "Subtask 2", "Description of Subtask 2", 1,
                20, LocalDateTime.of(2024, 1, 1, 1, 1));
        taskManager.createSubtask(subtaskTest2);

        taskManager.getEpicTime(epic);

        assertNotNull(epic.getStartTime(), "Время начала не найдено");
        assertEquals(epic.getStartTime(), subtaskTest1.getStartTime(), "Время начала неверное");
    }

    @Test
    public void shouldReturnCorrectEndTime() throws Exception {
        Subtask subtaskTest1 = new Subtask(2,"Subtask 1", "Description of Subtask 1", 1,
                10, LocalDateTime.of(2023, 1, 1, 1, 1));
        taskManager.createSubtask(subtaskTest1);

        Subtask subtaskTest2 = new Subtask(3, "Subtask 2", "Description of Subtask 2", 1,
                20, LocalDateTime.of(2024, 1, 1, 1, 1));
        taskManager.createSubtask(subtaskTest2);

        taskManager.getEpicTime(epic);

        assertNotNull(epic.getEndTime(), "Время конца не найдено");
        assertEquals(epic.getEndTime(), subtaskTest2.getEndTime(), "Время конца неверное");
    }

    @Test
    public void shouldReturnNullStartTime() throws Exception {
        Subtask subtaskTest1 = new Subtask(2,"Subtask 1", "Description of Subtask 1", 1);
        taskManager.createSubtask(subtaskTest1);

        Subtask subtaskTest2 = new Subtask(3, "Subtask 2", "Description of Subtask 2", 1);
        taskManager.createSubtask(subtaskTest2);

        taskManager.getEpicTime(epic);

        assertNull(epic.getStartTime(), "Время не null");
        assertEquals(epic.getStartTime(), subtaskTest1.getStartTime(), "Время неверное");
    }

    @Test
    public void shouldReturnNullEndTime() throws Exception {
        Subtask subtaskTest1 = new Subtask(2,"Subtask 1", "Description of Subtask 1", 1);
        taskManager.createSubtask(subtaskTest1);

        Subtask subtaskTest2 = new Subtask(3, "Subtask 2", "Description of Subtask 2", 1);
        taskManager.createSubtask(subtaskTest2);

        taskManager.getEpicTime(epic);

        assertNull(epic.getEndTime(), "Время не null");
        assertEquals(epic.getEndTime(), subtaskTest2.getEndTime(), "Время неверное");
    }

    @Test
    public void shouldReturnCorrectDuration() throws Exception {
        Subtask subtaskTest1 = new Subtask(2,"Subtask 1", "Description of Subtask 1", 1,
                10, LocalDateTime.of(2023, 1, 1, 1, 1));
        taskManager.createSubtask(subtaskTest1);

        Subtask subtaskTest2 = new Subtask(3, "Subtask 2", "Description of Subtask 2", 1,
                20, LocalDateTime.of(2024, 1, 1, 1, 1));
        taskManager.createSubtask(subtaskTest2);

        taskManager.getEpicTime(epic);

        int expectedDuration = subtaskTest1.getDuration() + subtaskTest2.getDuration();
        int actualDuration = epic.getDuration();

        assertNotNull(actualDuration, "Длительность подзадачи не найдена");
        assertEquals(actualDuration, expectedDuration, "Длительности подзадач неравны");
    }

    @Test
    public void shouldReturnNullDuration() throws Exception {
        Subtask subtaskTest1 = new Subtask(2,"Subtask 1", "Description of Subtask 1", 1);
        taskManager.createSubtask(subtaskTest1);

        Subtask subtaskTest2 = new Subtask(3, "Subtask 2", "Description of Subtask 2", 1);
        taskManager.createSubtask(subtaskTest2);

        taskManager.getEpicTime(epic);

        int actualDuration = epic.getDuration();

        assertEquals(actualDuration,0, "Время не null");
        assertEquals(actualDuration, subtaskTest1.getDuration(), "Время неверное");
    }

    @Test
    public void shouldReturnEpicWithoutSubtasks() {
        int subtasksSize = epic.getSubtaskIds().size();

        assertEquals(0, subtasksSize, "Список id подзадач не пустой");
        assertEquals(TaskStatus.NEW, epic.getStatus(), "Статус эпика изменился");
    }

    @Test
    public void shouldReturnEpicNewWhenAllSubtasksNew() throws Exception {
        Subtask subtaskTest1 = new Subtask(2,"Subtask 1", "Description of Subtask 1", 1);
        taskManager.createSubtask(subtaskTest1);

        Subtask subtaskTest2 = new Subtask(3, "Subtask 2", "Description of Subtask 2", 1);
        taskManager.createSubtask(subtaskTest2);

        assertEquals(TaskStatus.NEW, epic.getStatus(), "Статус эпика неверный");
    }

    @Test
    public void shouldReturnEpicDoneWhenAllSubtasksDone() throws Exception {
        Subtask subtaskTest1 = new Subtask(2,"Subtask 1", "Description of Subtask 1", 1,
                TaskStatus.DONE);
        taskManager.createSubtask(subtaskTest1);

        Subtask subtaskTest2 = new Subtask(3, "Subtask 2", "Description of Subtask 2", 1,
                TaskStatus.DONE);
        taskManager.createSubtask(subtaskTest2);

        assertEquals(TaskStatus.DONE, epic.getStatus(), "Статус эпика неверный");
    }

    @Test
    public void shouldReturnEpicInProgressWhenSubtasksNewAndDone() throws Exception {
        Subtask subtaskTest1 = new Subtask(2,"Subtask 1", "Description of Subtask 1", 1,
                TaskStatus.DONE);
        taskManager.createSubtask(subtaskTest1);

        Subtask subtaskTest2 = new Subtask(3, "Subtask 2", "Description of Subtask 2", 1);
        taskManager.createSubtask(subtaskTest2);

        assertEquals(TaskStatus.IN_PROGRESS, epic.getStatus(), "Статус эпика неверный");
    }

    @Test
    public void shouldReturnEpicInProgressWhenAllSubtasksInProgress() throws Exception {
        Subtask subtaskTest1 = new Subtask(2,"Subtask 1", "Description of Subtask 1", 1,
                TaskStatus.IN_PROGRESS);
        taskManager.createSubtask(subtaskTest1);

        Subtask subtaskTest2 = new Subtask(3, "Subtask 2", "Description of Subtask 2", 1,
                TaskStatus.IN_PROGRESS);
        taskManager.createSubtask(subtaskTest2);

        assertEquals(TaskStatus.IN_PROGRESS, epic.getStatus(), "Статус эпика неверный");
    }
}