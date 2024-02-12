package tests;

import manager.FileBackedTasksManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import tasks.enums.TaskType;
import tasks.models.Task;
import tasks.models.Subtask;
import tasks.models.Epic;
import tasks.enums.TaskStatus;
import java.time.LocalDateTime;
import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTasksManagerTest extends TaskManagerTest<FileBackedTasksManager> {
    @BeforeEach
    void beforeEach() {
        super.beforeEach();

        taskManager = new FileBackedTasksManager(new File("./resources/tasksForTests.csv"));
        
        taskManager.deleteAllNormalTasks();
        taskManager.deleteAllEpics();
        taskManager.deleteAllSubtasks();
    }

    @Test
    void save() {
        FileBackedTasksManager fileBacked =
                new FileBackedTasksManager(new File("./resources/tasksForTests2.csv"));

        Task normalTask1 = new Task("Task 1", "Description of Task 1", TaskType.NORMAL,TaskStatus.NEW,
                30, LocalDateTime.of(2024, 2, 11, 15, 0, 0));

        Task normalTask2 = new Task("Task 2", "Description of Task 2", TaskType.NORMAL,
                TaskStatus.IN_PROGRESS,
                30, LocalDateTime.of(2024, 2, 11, 16, 0, 0));

        Task normalTask3 = new Task("Task 3", "Description of Task 3", TaskType.NORMAL,
                TaskStatus.IN_PROGRESS,
                30, LocalDateTime.of(2024, 2, 11, 17, 0, 0));

        fileBacked.createNormalTask(normalTask1);
        fileBacked.getNormalTask(normalTask1.getId());

        fileBacked.createNormalTask(normalTask2);
        fileBacked.getNormalTask(normalTask2.getId());

        fileBacked.createNormalTask(normalTask3);
        fileBacked.getNormalTask(normalTask3.getId());

        Epic epic1 = new Epic(4,"Epic 12", "Description of Epic 12", TaskStatus.NEW, 30,
                LocalDateTime.of(2024, 1, 1, 1, 1, 1));

        Epic epic2 = new Epic(5,"Epic 2", "Description of Epic 2", TaskStatus.NEW, 40,
                LocalDateTime.of(2023, 1, 1, 1, 1, 1));

        fileBacked.createEpic(epic1);
        fileBacked.getEpic(epic1.getId());

        fileBacked.createEpic(epic2);
        fileBacked.getEpic(epic2.getId());

        Subtask subtask1 = new Subtask("Subtask 1 (epic-1)", "Description of Subtask 1 (epic-1)",
                epic1.getId(), 30,
                LocalDateTime.of(2024, 1, 1, 1, 1, 1));

        Subtask subtask2 = new Subtask("Subtask 2 (epic-2)", "Description of Subtask 2 (epic-2)",
                epic2.getId(), 40,
                LocalDateTime.of(2023, 1, 1, 1, 1, 1));

        fileBacked.createSubtask(subtask1);
        fileBacked.getSubtask(subtask1.getId());

        fileBacked.createSubtask(subtask2);
        fileBacked.getSubtask(subtask2.getId());

        fileBacked.save();
        taskManager.save();

        compareFiles("./resources/tasksForTests.csv", "./resources/tasksForTests2.csv");
    }

    @Test
    void saveEpicWithoutAndWithoutHistory() {
        Epic epic1 = new Epic(1,"Epic #1", "Epic1 description", TaskStatus.NEW, 30,
                LocalDateTime.of(2024, 1, 1, 1, 1, 1));

        FileBackedTasksManager actualFilebacked =
                new FileBackedTasksManager(new File("./resources/tasksForTests3.csv"));

        actualFilebacked.createEpic(epic1);

        actualFilebacked.save();

        compareFiles("./resources/tasksForTests3.csv", "./resources/tasksForTests2.csv");
    }

    private void compareFiles(String expectedFile, String actualFile) {
        FileBackedTasksManager expectedFileBacked = FileBackedTasksManager.loadFromFile(new File(expectedFile));
        FileBackedTasksManager actualFileBacked = FileBackedTasksManager.loadFromFile(new File(actualFile));

        int i = 0;

        Task task;
        Task expectedTask;

        while (i < expectedFileBacked.getAllNormalTasks().size()) {
            expectedTask = expectedFileBacked.getAllNormalTasks().get(i);
            task = actualFileBacked.getAllNormalTasks().get(i);

            assertEquals(expectedTask.getName(), task.getName());
            assertEquals(expectedTask.getDescription(), task.getDescription());
            assertEquals(expectedTask.getStatus(), task.getStatus());
            assertEquals(expectedTask.getId(), task.getId());
            assertEquals(expectedTask.getType(), task.getType());

            expectedFileBacked.deleteNormalTask(expectedTask.getId());

            i++;
        }
        i = 0;

        while (i < expectedFileBacked.getHistory().size()) {
            expectedTask = expectedFileBacked.getHistory().get(i);
            task = actualFileBacked.getHistory().get(i);

            assertEquals(expectedTask.getName(), task.getName());
            assertEquals(expectedTask.getDescription(), task.getDescription());
            assertEquals(expectedTask.getStatus(), task.getStatus());
            assertEquals(expectedTask.getId(), task.getId());
            assertEquals(expectedTask.getType(), task.getType());

            expectedFileBacked.getHistory().remove(i);

            i++;
        }
    }
}