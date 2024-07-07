import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.TaskStatus;

public class Main {

    public static void main(String[] args) {
        TasksManager manager = new TasksManager();

        Task task1 = new Task("Переезд", TaskStatus.NEW, "Собрать вещи");
        final int taskId1 = manager.addNewTask(task1);
        task1 = new Task(taskId1, task1.getName(), task1.getStatus(), task1.getDescription());

        Epic epic1 = new Epic("Подготовка", TaskStatus.NEW,
                "Сходить в строительный магазин и вызвать грузовую машину");
        final int epicId1 = manager.addNewEpic(epic1);
        epic1 = new Epic(epicId1, epic1.getName(), epic1.getStatus(), epic1.getDescription());

        Subtask subtask1 = new Subtask("Строительный магазин", TaskStatus.NEW,
                "Купить коробки и скотч", epicId1);
        final int subtaskId1 = manager.addNewSubtask(subtask1);
        subtask1 = new Subtask(subtaskId1, subtask1.getName(), subtask1.getStatus(), subtask1.getDescription(),
                epicId1);

        Subtask subtask2 = new Subtask("Грузовая машина", TaskStatus.NEW,
                "Нанять грузчиков в помощь", epicId1);
        final int subtaskId2 = manager.addNewSubtask(subtask1);
        subtask2 = new Subtask(subtaskId2, subtask2.getName(), subtask2.getStatus(), subtask2.getDescription(),
                epicId1);

        Task task2 = new Task("Приготовить обед", TaskStatus.NEW, "Купить продукты");
        final int taskId2 = manager.addNewTask(task2);
        task2 = new Task(taskId2, task2.getName(), task2.getStatus(), task2.getDescription());

        Epic epic2 = new Epic("Продуктовый магазин", TaskStatus.NEW, "Составить список покупок");
        final int epicId2 = manager.addNewEpic(epic2);
        epic2 = new Epic(epicId2, epic2.getName(), epic2.getStatus(), epic2.getDescription());

        Subtask subtask3 = new Subtask("Список продуктов", TaskStatus.NEW,
                "Проверить наличие продуктов дома", epicId2);
        final int subtaskId3 = manager.addNewSubtask(subtask3);
        subtask3 = new Subtask(subtaskId3, subtask3.getName(), subtask3.getStatus(), subtask3.getDescription(),
                epicId2);

        System.out.println(manager.getEpics());
        System.out.println(manager.getTasks());
        System.out.println(manager.getSubtasks());
        System.out.println();
        manager.removeSubtaskById(3);
        manager.removeTaskById(5);
        System.out.println(manager.getEpics());
        System.out.println(manager.getTasks());
        System.out.println(manager.getSubtasks());
    }
}
