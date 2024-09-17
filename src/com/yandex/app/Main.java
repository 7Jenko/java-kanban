package com.yandex.app;

import com.yandex.app.service.Managers;
import com.yandex.app.service.TaskManager;
import com.yandex.app.status.TaskStatus;
import com.yandex.app.model.Epic;
import com.yandex.app.model.Subtask;
import com.yandex.app.model.Task;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {
        TaskManager manager = Managers.getDefault();

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

        Subtask subtask2 = new Subtask("Грузовая машина", TaskStatus.DONE,
                "Нанять грузчиков в помощь", epicId1);
        final int subtaskId2 = manager.addNewSubtask(subtask2);
        subtask2 = new Subtask(subtaskId2, subtask2.getName(), subtask2.getStatus(), subtask2.getDescription(),
                epicId1);

        Task task2 = new Task("Приготовить обед", TaskStatus.NEW, "Купить продукты");
        final int taskId2 = manager.addNewTask(task2);
        task2 = new Task(taskId2, task2.getName(), task2.getStatus(), task2.getDescription());

        Epic epic2 = new Epic("Продуктовый магазин", TaskStatus.NEW, "Составить список покупок");
        final int epicId2 = manager.addNewEpic(epic2);
        epic2 = new Epic(epicId2, epic2.getName(), epic2.getStatus(), epic2.getDescription());

        Subtask subtask3 = new Subtask("Список продуктов", TaskStatus.DONE,
                "Проверить наличие продуктов дома", epicId2);
        final int subtaskId3 = manager.addNewSubtask(subtask3);
        subtask3 = new Subtask(subtaskId3, subtask3.getName(), subtask3.getStatus(), subtask3.getDescription(),
                epicId2);

        manager.getEpic(2);
        manager.getTask(15);
        manager.getSubtask(3);
        manager.getEpic(6);
        manager.getTask(5);
        manager.getSubtask(4);
        manager.getSubtask(7);
        manager.getEpic(2);
        manager.getTask(1);
        manager.getSubtask(3);
        manager.getEpic(6);
        printAllTasks(manager);
    }

    private static void printAllTasks(TaskManager manager) {
        System.out.println("Задачи:");
        for (Task task : manager.getTasks()) {
            System.out.println(task);
        }
        System.out.println("Эпики:");
        for (Task epic : manager.getEpics()) {
            System.out.println(epic);

            for (Task task : manager.getEpicSubtasks(epic.getId())) {
                System.out.println("--> " + task);
            }
        }
        System.out.println("Подзадачи:");
        for (Task subtask : manager.getSubtasks()) {
            System.out.println(subtask);
        }

        System.out.println("История:");
        for (Task task : manager.getHistory()) {
            System.out.println(task);
        }
    }
}
