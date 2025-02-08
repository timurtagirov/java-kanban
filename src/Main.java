public class Main {

    public static void main(String[] args) {

        TaskManager taskManager = new TaskManager();
        Task task1 = new Task("Задача 1", "Детали задачи 1", Status.NEW);
        Task task2 = new Task("Задача 2", "Детали задачи 2", Status.IN_PROGRESS);
        Epic epicA = new Epic("Эпик A", "Детали эпика A");
        Subtask subtaskA1 = new Subtask("Подзадача A1", "Детали подзадачи A1", Status.NEW, 3);
        Subtask subtaskA2 = new Subtask("Подзадача A2", "Детали подзадачи A2", Status.NEW, 3);
        Epic epicB = new Epic("Эпик B", "Детали эпика B");
        Subtask subtaskB1 = new Subtask("Подзадача B1", "Детали подзадачи B1", Status.IN_PROGRESS, 6);

        taskManager.addTask(task1);
        taskManager.addTask(task2);
        taskManager.addEpic(epicA);
        taskManager.addSubtask(subtaskA1);
        taskManager.addSubtask(subtaskA2);
        taskManager.addEpic(epicB);
        taskManager.addSubtask(subtaskB1);

        System.out.println('\n' + "Проверка printAll() и что все создалось правильно");
        taskManager.printAll();
        System.out.println();

        System.out.println("Проверка getById() и что всё печатается корректно");
        for (int i = 1; i <= 7; i++) {
            System.out.println(taskManager.getById(i));
        }
        System.out.println();

        System.out.println("Проверка updateTask(), updateEpic(), updateSubtask(), " +
                "и как обновляются статусы в эпике от обновления подзадач");
        task1 = new Task("Задача 1", "Детали задачи 1", Status.IN_PROGRESS);
        epicB = new Epic("Эпик B", "Новые детали эпика B");
        subtaskA1 = new Subtask("Подзадача A1", "Детали подзадачи A1", Status.IN_PROGRESS, 3);
        subtaskB1 = new Subtask("Подзадача B1", "Детали подзадачи B1", Status.DONE, 6);

        taskManager.updateTask(1, task1);
        taskManager.updateEpic(6, epicB);
        taskManager.updateSubtask(4, subtaskA1);
        taskManager.updateSubtask(7, subtaskB1);
        taskManager.printAll();
        System.out.println();

        System.out.println("Проверка removeById(), и что от удаления эпика удаляются также и подзадачи");
        taskManager.removeById(1);
        taskManager.removeById(3);
        taskManager.printAll();
    }
}
