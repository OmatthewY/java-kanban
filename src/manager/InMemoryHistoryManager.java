package manager;

import tasks.models.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class InMemoryHistoryManager implements HistoryManager {
    private CustomLinkedList historyList = new CustomLinkedList();
    private Map<Integer, Node> taskNodeMap = new HashMap<>();

    @Override
    public void add(Task task) {
        if (taskNodeMap.containsKey(task.getId())) {
            Node node = taskNodeMap.get(task.getId());

            historyList.removeNode(node);
        }

        historyList.linkLast(task);
        taskNodeMap.put(task.getId(), historyList.tail);

        if (historyList.getTasks().size() > 10) {
            Node removedNode = historyList.head;

            historyList.removeNode(removedNode);
            taskNodeMap.remove(removedNode.task.getId());
        }
    }

    @Override
    public void remove(int id) {
        if (taskNodeMap.containsKey(id)) {
            Node nodeToRemove = taskNodeMap.get(id);

            historyList.removeNode(nodeToRemove);
            taskNodeMap.remove(id);
        }
    }

    @Override
    public void removeAllTasksOfType(Class<? extends Task> type) {
        List<Task> tasksToRemove = new ArrayList<>();

        for (Node node : taskNodeMap.values()) {
            Task task = node.task;

            if (type.isInstance(task)) {
                tasksToRemove.add(task);
            }
        }

        for (Task taskToRemove : tasksToRemove) {
            remove(taskToRemove.getId());
        }
    }

    @Override
    public List<Task> getHistory() {
        return historyList.getTasks();
    }
}

class Node {
    Task task;
    Node next;
    Node prev;

    public Node(Task task) {
        this.task = task;
    }
}

class CustomLinkedList {
    protected Node head;
    protected Node tail;

    void linkLast(Task task) {
        Node newNode = new Node(task);

        if (tail == null) {
            head = tail = newNode;
        } else {
            tail.next = newNode;
            newNode.prev = tail;
            tail = newNode;
        }
    }

    List<Task> getTasks() {
        List<Task> tasks = new ArrayList<>();

        Node current = head;
        while (current != null) {
            tasks.add(current.task);
            current = current.next;
        }
        return tasks;
    }

    void removeNode(Node node) {
        if (node.prev != null) {
            node.prev.next = node.next;
        } else {
            head = node.next;
        }
        if (node.next != null) {
            node.next.prev = node.prev;
        } else {
            tail = node.prev;
        }
    }
}