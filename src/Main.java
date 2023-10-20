import java.util.Scanner;
import java.util.List;

public class Main {

    private static final Scanner scanner = new Scanner(System.in);
    private static final TaskManager taskManager = new TaskManager();

    public static void main(String[] args) {

        while (true) {
            printMenu();

            int command = scanner.nextInt();

            if (command == 1) {
                createTask();
            } else if (command == 2) {
                updateTask();
            } else if (command == 3) {
                deleteTask();
            } else if (command == 4) {
                printTaskList();
            } else if (command == 5) {
                printSubtasksByEpicId();
            } else if (command == 6) {
                printAllEpics();
            } else if (command == 7) {
                printTasksByStatus();
            } else if (command == 0) {
                System.out.println("Выход из приложения.");
                break;
            } else {
                System.out.println("Такой команды пока что нет. Повторите попытку.");
            }

        }
    }

    private static void printMenu() {
        System.out.println("===== Меню =====");
        System.out.println("1. Создать задачу");
        System.out.println("2. Обновить задачу");
        System.out.println("3. Удалить задачу");
        System.out.println("4. Вывести список задач");
        System.out.println("5. Вывести список подзадач");
        System.out.println("6. Вывести список эпиков");
        System.out.println("7. Вывести задачи с указанным статусом");
        System.out.println("0. Выйти из приложения");
    }

    private static void createTask() {
        System.out.println("Введите название задачи: ");
        scanner.nextLine();
        String name = scanner.nextLine();

        System.out.println("Введите описание задачи: ");
        String description = scanner.nextLine();

        System.out.println("Выберите тип задачи (1 - EPIC, 2 - SUBTASK, 3 - NORMAL): ");
        int typeInt = scanner.nextInt();

        TaskType type;
        int epicId = 0;

        switch (typeInt) {
            case 1:
                type = TaskType.EPIC;
                break;
            case 2:
                type = TaskType.SUBTASK;

                System.out.println("Введите ID эпика для этой подзадачи: ");
                epicId = scanner.nextInt();

                if (taskManager.findEpicById(epicId) == null) {
                    System.out.println("Эпик с указанным ID не найден. Невозможно создать подзадачу.");
                    return;
                }
                break;
            default:
                type = TaskType.NORMAL;
        }
        taskManager.createTask(name, description, type, epicId);

        System.out.println("Создана новая задача.");
    }

    private static void updateTask() {
        System.out.println("Введите ID задачи: ");
        int id = scanner.nextInt();

        Task task = taskManager.findTaskById(id);

        if (task == null) {
            System.out.println("Задача с указанным ID не найдена");
            return;
        }

        System.out.println("Укажите новое название: ");
        scanner.nextLine();
        String newName = scanner.nextLine();

        System.out.println("Укажите новое описание: ");
        String newDescription = scanner.nextLine();

        System.out.println("Укажите новый статус (1 - NEW, 2 - IN_PROGRESS, 3 - DONE): ");
        int statusInt = scanner.nextInt();

        TaskStatus status;

        switch (statusInt) {
            case 1:
                status = TaskStatus.NEW;
                break;
            case 2:
                status = TaskStatus.IN_PROGRESS;
                break;
            default:
                status = TaskStatus.DONE;
        }
        taskManager.updateTask(task, newName, newDescription, status);

        System.out.println("Задача обновлена.");
    }

    private static void deleteTask() {
        System.out.println("Введите ID задачи для удаления: ");
        int id = scanner.nextInt();

        Task task = taskManager.findTaskById(id);

        if (task == null) {
            System.out.println("Задача с указанным ID не найдена");
            return;
        }
        taskManager.deleteTask(task);

        System.out.println("Задача удалена.");
    }

    private static void printTaskList() {
        List<Task> tasks = taskManager.getAllTasks();

        for (Task task : tasks) {
            System.out.println("ID: " + task.getId() + " Имя: " + task.getName()
                    + " Описание: " + task.getDescription() + " Тип: " + task.getType()
                    + " Статус: " + task.getStatus());
        }
    }

    private static void printSubtasksByEpicId() {
        System.out.println("Введите ID эпика: ");
        int epicId = scanner.nextInt();

        List<Subtask> subtasks = taskManager.getSubtasksByEpicId(epicId);

        if (subtasks.isEmpty()) {
            System.out.println("Подзадачи для указанного эпика не найдены.");
        } else {
            System.out.println("Подзадачи для эпика с ID " + epicId + ":");

            for (Subtask subtask : subtasks) {
                System.out.println("ID: " + subtask.getId() + " Имя: " + subtask.getName()
                        + " Описание: " + subtask.getDescription() + " Статус: " + subtask.getStatus());
            }
        }
    }

    private static void printAllEpics() {
        List<Epic> epics = taskManager.getAllEpics();

        if (epics.isEmpty()) {
            System.out.println("Эпики не найдены.");
        } else {
            System.out.println("Список эпиков:");

            for (Epic epic : epics) {
                System.out.println("ID: " + epic.getId() + " Имя: " + epic.getName()
                        + " Описание: " + epic.getDescription() + " Статус: " + epic.getStatus());
            }
        }
    }

    private static void printTasksByStatus() {
        System.out.println("Выберите статус задач (1 - NEW, 2 - IN_PROGRESS, 3 - DONE): ");
        int statusInt = scanner.nextInt();

        TaskStatus status;
        switch (statusInt) {
            case 1:
                status = TaskStatus.NEW;
                break;
            case 2:
                status = TaskStatus.IN_PROGRESS;
                break;
            default:
                status = TaskStatus.DONE;
        }

        List<Task> tasks = taskManager.getTasksByStatus(status);

        if (tasks.isEmpty()) {
            System.out.println("Задачи с указанным статусом не найдены.");
        } else {
            System.out.println("Задачи со статусом " + status + ":");

            for (Task task : tasks) {
                System.out.println("ID: " + task.getId() + " Имя: " + task.getName()
                        + " Описание: " + task.getDescription() + " Тип: " + task.getType());
            }
        }
    }
}