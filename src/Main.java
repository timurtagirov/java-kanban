import manager.FileBackedTaskManager;
import manager.InMemoryTaskManager;
import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;

import java.io.File;

import java.nio.file.Files;

public class Main {

    public static void main(String[] args) {

        InMemoryTaskManager taskManager = new InMemoryTaskManager();
        Task task1 = new Task("Задача 1", "Детали задачи 1", Status.NEW);
        Task task2 = new Task("Задача 2", "Детали задачи 2", Status.IN_PROGRESS);
        Epic epicA = new Epic("Эпик A", "Детали эпика A");
        Subtask subtaskA1 = new Subtask("Подзадача A1", "Детали подзадачи A1", Status.NEW, 3);
        Subtask subtaskA2 = new Subtask("Подзадача A2", "Детали подзадачи A2", Status.NEW, 3);
        Epic epicB = new Epic("Эпик B", "Детали эпика B", 6);
        Subtask subtaskB1 = new Subtask("Подзадача B1", "Детали подзадачи B1", Status.IN_PROGRESS, 6);

        taskManager.addTask(task1);
        taskManager.addTask(task2);
        taskManager.addEpic(epicA);
        taskManager.addSubtask(subtaskA1);
        taskManager.addSubtask(subtaskA2);
        taskManager.addEpic(epicB);
        taskManager.addSubtask(subtaskB1);

        System.out.println('\n' + "Проверка printTasks(), printEpics(), printSubtasks() и что все создалось правильно");
        System.out.println("Список задач:");
        System.out.println(taskManager.getTasks());
        System.out.println("Список эпиков:");
        System.out.println(taskManager.getEpics());
        System.out.println("Список подзадач:");
        System.out.println(taskManager.getSubtasks());
        System.out.println();

        System.out.println("Проверка getById() и что всё печатается корректно");
        for (int i = 1; i <= 7; i++) {
            System.out.println(taskManager.getById(i));
        }
        System.out.println("Вызовем ещё раз некоторые таски, чтобы проверить в конце InMemoryHistoryManager");
        System.out.println(taskManager.getById(5));
        System.out.println(taskManager.getById(3));
        System.out.println(taskManager.getById(1));
        System.out.println();

        System.out.println("Проверка updateTask(), updateEpic(), updateSubtask(), " +
                "и как обновляются статусы в эпике от обновления подзадач");
        task1 = new Task("Задача 1", "Детали задачи 1", 1, Status.IN_PROGRESS);
        epicB = new Epic("Эпик B", "Новые детали эпика B", 6);
        subtaskA1 = new Subtask("Подзадача A1", "Детали подзадачи A1", 4, Status.IN_PROGRESS, 3);
        subtaskB1 = new Subtask("Подзадача B1", "Детали подзадачи B1", 7, Status.DONE, 6);

        taskManager.updateTask(task1);
        taskManager.updateEpic(epicB);
        taskManager.updateSubtask(subtaskA1);
        taskManager.updateSubtask(subtaskB1);
        System.out.println("Список задач:");
        System.out.println(taskManager.getTasks());
        System.out.println("Список эпиков:");
        System.out.println(taskManager.getEpics());
        System.out.println("Список подзадач:");
        System.out.println(taskManager.getSubtasks());
        System.out.println();

        System.out.println("Проверка removeById(), и что от удаления эпика удаляются также и подзадачи");
        taskManager.removeById(4);
        taskManager.removeById(6);
        System.out.println("Список задач:");
        System.out.println(taskManager.getTasks());
        System.out.println("Список эпиков:");
        System.out.println(taskManager.getEpics());
        System.out.println("Список подзадач:");
        System.out.println(taskManager.getSubtasks());
        System.out.println();

        System.out.println("Проверка removeSubtasks()");
        taskManager.removeSubtasks();
        System.out.println("Список задач:");
        System.out.println(taskManager.getTasks());
        System.out.println("Список эпиков:");
        System.out.println(taskManager.getEpics());
        System.out.println("Список подзадач:");
        System.out.println(taskManager.getSubtasks());

        System.out.println(taskManager.getHistory());

        FileBackedTaskManager taskManager1 = new FileBackedTaskManager("tasks.csv");
        taskManager1.addTask(task1);
        taskManager1.addTask(task2);
        taskManager1.addEpic(epicA);
        taskManager1.addSubtask(subtaskA1);
        taskManager1.addSubtask(subtaskA2);
        taskManager1.addEpic(epicB);
        taskManager1.addSubtask(subtaskB1);

        System.out.println(taskManager1.toString(task1));
        System.out.println(taskManager1.toString(epicA));
        System.out.println(taskManager1.toString(subtaskA1));
        System.out.println(taskManager1.toString(taskManager1.fromString("1,TASK,Задача 1,IN_PROGRESS,Детали задачи 1")));
        System.out.println(taskManager1.toString(taskManager1.fromString("3,EPIC,Эпик A,IN_PROGRESS,Детали эпика A")));
        System.out.println(taskManager1.toString(taskManager1.fromString("4,SUBTASK,Подзадача A1,IN_PROGRESS,Детали подзадачи A1,3")));
        System.out.println(System.getProperty("user.dir"));

        FileBackedTaskManager taskManager2 = FileBackedTaskManager.loadFromFile(new File("C:\\Users\\Lenovo\\IdeaProjects\\Sprint4\\java-kanban\\tasks.csv"));
        System.out.println(taskManager2.toString(task1));
        System.out.println(taskManager2.toString(epicA));
        System.out.println(taskManager2.toString(subtaskA1));
    }
}
