import manager.InMemoryTaskManager;
import manager.TaskManager;
import tasks.enums.TaskStatus;
import tasks.enums.TaskType;
import tasks.models.Epic;
import tasks.models.Task;
import tasks.models.Subtask;

import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        TaskManager taskManager = new InMemoryTaskManager();
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

                if (taskTypeCommand == 1) {
                    Epic epic = new Epic(name, description, TaskStatus.NEW);
                    Task createdTask = taskManager.createEpic(epic);

                    System.out.println("Создан новый Epic с ID: " + createdTask.getId());
                } else if (taskTypeCommand == 2) {
                    System.out.print("Введите ID эпика, к которому относится подзадача: ");
                    int epicId = scanner.nextInt();
                    scanner.nextLine();

                    Subtask subtask = new Subtask(0, name, description, epicId);
                    Task createdTask = taskManager.createSubtask(subtask);

                    if (createdTask != null) {
                        System.out.println("Создана новая подзадача с ID: " + createdTask.getId());
                    }
                } else if (taskTypeCommand == 3) {
                    Task normalTask = new Task(name, description, TaskType.NORMAL, TaskStatus.NEW);
                    Task createdTask = taskManager.createNormalTask(normalTask);

                    System.out.println("Создана новая Normal задача с ID: " + createdTask.getId());
                } else {
                    System.out.println("Такого типа не существует. Выберите 1, 2 или 3.");
                }
            } else if (command == 2) {
                System.out.print("Введите ID задачи для обновления: ");
                int taskId = scanner.nextInt();
                scanner.nextLine();

                Task taskToUpdate = taskManager.findSubtaskById(taskId);
                if (taskToUpdate == null) {
                    taskToUpdate = taskManager.findEpicById(taskId);
                }
                if (taskToUpdate == null) {
                    taskToUpdate = taskManager.findNormalTaskById(taskId);
                }

                if (taskToUpdate != null) {
                    System.out.print("Введите новое название задачи: ");
                    String updatedName = scanner.nextLine();

                    System.out.print("Введите новое описание задачи: ");
                    String updatedDescription = scanner.nextLine();

                    System.out.print("Введите новый статус задачи (NEW, IN_PROGRESS, DONE): ");
                    String statusInput = scanner.nextLine();

                    TaskStatus updatedStatus = null;

                    try {
                        updatedStatus = TaskStatus.valueOf(statusInput.toUpperCase());
                    } catch (IllegalArgumentException e) {
                        System.out.println("Недопустимый статус задачи. Пожалуйста," +
                                " введите NEW, IN_PROGRESS или DONE.");
                        continue;
                    }

                    if (taskToUpdate.getType() == TaskType.EPIC) {
                        Epic updatedEpic = new Epic(taskToUpdate.getId(), updatedName,
                                updatedDescription, updatedStatus);

                        taskManager.updateEpic(updatedEpic);
                    } else if (taskToUpdate.getType() == TaskType.SUBTASK) {
                        Subtask updatedSubtask = new Subtask(taskToUpdate.getId(), updatedName,
                                updatedDescription, ((Subtask) taskToUpdate).getEpicId());

                        updatedSubtask.setStatus(updatedStatus);
                        taskManager.updateSubtask(updatedSubtask);
                    } else if (taskToUpdate.getType() == TaskType.NORMAL) {
                        Task updatedNormalTask = new Task(taskToUpdate.getId(), updatedName,
                                updatedDescription, TaskType.NORMAL, updatedStatus);

                        taskManager.updateNormalTask(updatedNormalTask);
                    }
                    System.out.println("Задача успешно обновлена.");
                } else {
                    System.out.println("Задача с указанным ID не найдена.");
                }
            } else if (command == 3) {
                System.out.print("Введите ID задачи для удаления: ");
                int taskIdToDelete = scanner.nextInt();
                scanner.nextLine();

                Task taskToDelete = taskManager.findSubtaskById(taskIdToDelete);
                if (taskToDelete == null) {
                    taskToDelete = taskManager.findEpicById(taskIdToDelete);
                }
                if (taskToDelete == null) {
                    taskToDelete = taskManager.findNormalTaskById(taskIdToDelete);
                }

                if (taskToDelete != null) {
                    if (taskToDelete.getType() == TaskType.EPIC) {
                        taskManager.deleteEpic(taskIdToDelete);
                    } else if (taskToDelete.getType() == TaskType.SUBTASK) {
                        taskManager.deleteSubtask(taskIdToDelete);
                    } else if (taskToDelete.getType() == TaskType.NORMAL) {
                        taskManager.deleteNormalTask(taskIdToDelete);
                    }
                    System.out.println("Задача успешно удалена.");
                } else {
                    System.out.println("Задача с указанным ID не найдена.");
                }
            } else if (command == 4) {
                List<Task> allEpics = taskManager.getAllEpics();
                List<Task> allSubtasks = taskManager.getAllSubtasks();
                List<Task> allNormalTasks = taskManager.getAllNormalTasks();

                System.out.println("Список всех Epic задач:");
                for (Task epic : allEpics) {
                    System.out.println(epic);
                }

                System.out.println("Список всех Subtask задач:");
                for (Task subtask : allSubtasks) {
                    System.out.println(subtask);
                }

                System.out.println("Список всех Normal задач:");
                for (Task normalTask : allNormalTasks) {
                    System.out.println(normalTask);
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
            } else if (command == 6) {
                List<Task> history = taskManager.getHistory();

                if (history.isEmpty()) {
                    System.out.println("История просмотров пуста.");
                } else {
                    System.out.println("История просмотров:");

                    for (Task task : history) {
                        System.out.println(task);
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
        System.out.println("6. Показать историю просмотров");
        System.out.println("0. Выход");
        System.out.print("Выберите действие: ");
    }
}