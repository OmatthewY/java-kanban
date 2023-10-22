import tasks.enums.TaskStatus;
import tasks.enums.TaskType;
import tasks.models.Task;

import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        TaskManager taskManager = new TaskManager();
        Scanner scanner = new Scanner(System.in);

        while (true) {
            printMenu();

            int command = scanner.nextInt();
            scanner.nextLine();

            if (command == 1) {
                System.out.print("Введите название задачи: ");
                String name = scanner.nextLine();

                System.out.print("Введите описание задачи: ");
                String description = scanner.nextLine();

                System.out.print("Выберите тип задачи (1 - Epic, 2 - Subtask, 3 - Normal): ");
                int taskTypeCommand = scanner.nextInt();
                scanner.nextLine();

                TaskType taskType;

                if (taskTypeCommand == 1) {
                    taskType = TaskType.EPIC;
                } else if (taskTypeCommand == 2) {
                    System.out.print("Введите ID эпика, к которому относится подзадача: ");
                    int epicId = scanner.nextInt();
                    scanner.nextLine();

                    Task epic = taskManager.findTaskById(epicId);

                    if (epic != null && epic.getType() == TaskType.EPIC) {
                        Task subtask = taskManager.createTask(name, description, TaskType.SUBTASK, epicId);

                        if (subtask != null) {
                            System.out.println("Создана новая подзадача с ID: " + subtask.getId());
                        }
                    } else {
                        System.out.println("Эпик с указанным ID не найден. Пожалуйста, создайте эпик сначала.");
                    }
                    continue;
                } else if (taskTypeCommand == 3) {
                    taskType = TaskType.NORMAL;
                } else {
                    System.out.println("Такого типа не существует. Выберите 1, 2 или 3.");
                    continue;
                }

                Task task = taskManager.createTask(name, description, taskType, 0);

                System.out.println("Создана новая задача с ID: " + task.getId());

            } else if (command == 2) {
                System.out.print("Введите ID задачи для обновления: ");
                int taskId = scanner.nextInt();
                scanner.nextLine();

                Task taskToUpdate = taskManager.findTaskById(taskId);

                if (taskToUpdate != null) {
                    System.out.print("Введите новое название задачи: ");
                    String updatedName = scanner.nextLine();

                    System.out.print("Введите новое описание задачи: ");
                    String updatedDescription = scanner.nextLine();

                    System.out.print("Выберите новый статус задачи (NEW, IN_PROGRESS, DONE): ");
                    TaskStatus updatedStatus = TaskStatus.valueOf(scanner.nextLine().toUpperCase());
                    taskManager.updateTask(taskId, updatedName, updatedDescription, updatedStatus);

                    System.out.println("Задача успешно обновлена.");
                } else {
                    System.out.println("Задача с указанным ID не найдена.");
                }
            } else if (command == 3) {
                System.out.print("Введите ID задачи для удаления: ");
                int taskIdToDelete = scanner.nextInt();
                scanner.nextLine();

                Task taskToDelete = taskManager.findTaskById(taskIdToDelete);

                if (taskToDelete != null) {
                    taskManager.deleteTask(taskIdToDelete);

                    System.out.println("Задача успешно удалена.");
                } else {
                    System.out.println("Задача с указанным ID не найдена.");
                }
            } else if (command == 4) {
                List<Task> allTasks = taskManager.getAllTasks();

                if (allTasks.isEmpty()) {
                    System.out.println("Список задач пуст.");
                } else {
                    System.out.println("Список всех задач:");

                    for (Task t : allTasks) {
                        System.out.println(t);
                    }
                }
            } else if (command == 5) {
                System.out.print("Введите ID эпика, для которого вы хотите получить подзадачи: ");
                int epicId = scanner.nextInt();
                scanner.nextLine();

                List<Task> subtasksForEpic = taskManager.getSubtasksForEpic(epicId);

                if (subtasksForEpic.isEmpty()) {
                    System.out.println("Подзадачи для выбранного эпика отсутствуют.");
                } else {
                    System.out.println("Список подзадач для выбранного эпика:");

                    for (Task subtask : subtasksForEpic) {
                        System.out.println(subtask);
                    }
                }
            } else if (command == 0) {
                System.out.println("Программа завершена.");
                break;
            } else {
                System.out.println("Такой команды пока нет. Попробуйте еще раз.");
            }
        }
    }

    public static void printMenu() {
        System.out.println("======Меню======");
        System.out.println("1. Создать задачу");
        System.out.println("2. Обновить задачу");
        System.out.println("3. Удалить задачу");
        System.out.println("4. Показать все задачи");
        System.out.println("5. Показать подзадачи");
        System.out.println("0. Выход");
        System.out.print("Выберите действие: ");
    }
}