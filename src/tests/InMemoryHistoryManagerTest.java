package tests;

import manager.InMemoryHistoryManager;
import tasks.enums.TaskStatus;
import tasks.enums.TaskType;
import tasks.models.Task;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import java.util.List;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {
    private InMemoryHistoryManager historyManager;
    private Task normalTask1;
    private Task normalTask2;
    private Task normalTask3;

    @BeforeEach
    void beforeEach() {
        historyManager = new InMemoryHistoryManager();

        normalTask1 = new Task(1, "Task 1", "Discription of Task 1", TaskType.NORMAL,
                TaskStatus.NEW);

        normalTask2 = new Task(2, "Task 1", "Discription of Task 1", TaskType.NORMAL,
                TaskStatus.NEW);

        normalTask3 = new Task(3, "Task 1", "Discription of Task 1", TaskType.NORMAL,
                TaskStatus.NEW);
    }

    @Test
    void add() {
        historyManager.add(normalTask1);

        final List<Task> history = historyManager.getHistory();

        assertNotNull(history, "Список истории пуст");
        assertEquals(1, history.size(), "Задача не была добавлена в историю");
    }

    @Test
    void addEmptyHistory() {
        assertThrows(NullPointerException.class, () -> {
            historyManager.add(null);
        });

        assertNotNull(historyManager.getHistory(),"История пустая");
        assertEquals(0, historyManager.getHistory().size(), "Задача была добавлена в историю");
    }

    @Test
    void addSimilarNormalTasks() {
        historyManager.add(normalTask1);
        historyManager.add(normalTask1);

        assertNotNull(historyManager.getHistory(),"История пустая");
        assertEquals(1, historyManager.getHistory().size(), "Неправильная история просмотра");
    }

    @Test
    void addTwoSimilarPairsOfNormalTasks() {
        historyManager.add(normalTask1);
        historyManager.add(normalTask1);
        historyManager.add(normalTask2);
        historyManager.add(normalTask2);

        assertNotNull(historyManager.getHistory(),"История пустая");
        assertEquals(2,historyManager.getHistory().size(),"Неправильная история просмотра");
    }

    @Test
    void deleteFirstNormalTask() {
        historyManager.add(normalTask1);
        historyManager.add(normalTask2);
        historyManager.add(normalTask3);

        historyManager.remove(normalTask1.getId());

        assertNotNull(historyManager.getHistory(), "История пустая");
        assertEquals(2, historyManager.getHistory().size(), "Неверная история просмотра");
        assertEquals(normalTask2, historyManager.getHistory().get(0));
        assertEquals(normalTask3, historyManager.getHistory().get(1));
    }

    @Test
    void deleteMiddleNormalTask() {
        historyManager.add(normalTask1);
        historyManager.add(normalTask2);
        historyManager.add(normalTask3);

        historyManager.remove(normalTask2.getId());

        assertNotNull(historyManager.getHistory(), "История пустая");
        assertEquals(2, historyManager.getHistory().size(), "Неверная история просмотра");
        assertEquals(normalTask1, historyManager.getHistory().get(0));
        assertEquals(normalTask3, historyManager.getHistory().get(1));
    }

    @Test
    void deleteLastNormalTask() {
        historyManager.add(normalTask1);
        historyManager.add(normalTask2);
        historyManager.add(normalTask3);

        historyManager.remove(normalTask3.getId());

        assertNotNull(historyManager.getHistory(), "История пустая");
        assertEquals(2, historyManager.getHistory().size(), "Неверная история просмотра");
        assertEquals(normalTask1, historyManager.getHistory().get(0));
        assertEquals(normalTask2, historyManager.getHistory().get(1));
    }

    @Test
    void getHistory() {
        historyManager.add(normalTask1);
        historyManager.add(normalTask2);
        historyManager.add(normalTask3);

        List<Task> history = new ArrayList<>();
        history.add(normalTask1);
        history.add(normalTask2);
        history.add(normalTask3);

        assertNotNull(historyManager.getHistory(),"История пустая");
        assertEquals(history, historyManager.getHistory(), "Неверная история просмотра");
    }

    @Test
    void getEmptyHistory() {
        assertNotNull(historyManager.getHistory());
        assertEquals(0,historyManager.getHistory().size(),"Неверная история просмотра");
    }
}