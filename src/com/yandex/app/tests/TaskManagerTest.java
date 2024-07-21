package com.yandex.app.tests;

import com.yandex.app.model.Epic;
import com.yandex.app.model.Subtask;
import com.yandex.app.model.Task;
import com.yandex.app.service.Managers;
import com.yandex.app.service.TaskManager;
import com.yandex.app.status.TaskStatus;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TaskManagerTest {
    @Test
    void taskIdEquals() {
        Task task1 = new Task(1, "Task1", TaskStatus.NEW, "description");
        Task task2 = new Task(1, "Task1", TaskStatus.DONE, "description");
        assertEquals(task1, task2, "Экземпляры класса Task должны быть равны друг другу, если равен их id;");
    }

    @Test
    void epicIdEquals() {
        Epic epic1 = new Epic(2, "Epic2", TaskStatus.NEW, "description");
        Epic epic2 = new Epic(2, "Epic2", TaskStatus.DONE, "description");
        assertEquals(epic1, epic2, "Наследники класса Task должны быть равны друг другу, если равен их id;");
    }

    @Test
    void subtaskIdEquals() {
        Subtask subtask1 = new Subtask(3, "Subtask1", TaskStatus.NEW, "description", 2);
        Subtask subtask2 = new Subtask(3, "Subtask1", TaskStatus.DONE, "description", 2);
        assertEquals(subtask1, subtask2, "Наследники класса Task должны быть равны друг другу, " +
                "если равен их id;");
    }

    @Test
    void epicCannotAddedToItselfAsASubtask() {
        TaskManager manager = Managers.getDefault();
        Epic epic1 = new Epic(1, "Epic1", TaskStatus.NEW, "description");
        manager.addNewEpic(epic1);
        Subtask subtask = new Subtask(1, "Epic1", TaskStatus.NEW, "description", 1);
        manager.addNewSubtask(subtask);
        assertTrue(manager.getEpics().contains(subtask), "Объект Epic нельзя добавить в самого себя " +
                "в виде подзадачи");
    }

    @Test
    public void subtaskCannotBeMadeIntoItsOwnEpic() {
        TaskManager manager = Managers.getDefault();
        Epic epic1 = new Epic(1, "Epic1", TaskStatus.NEW, "description");
        manager.addNewEpic(epic1);
        Subtask subtask = new Subtask(1, "Epic1", TaskStatus.NEW, "description", 1);
        manager.addNewSubtask(subtask);
        assertTrue(manager.getSubtasks().contains(epic1), "Объект Subtask нельзя сделать своим же эпиком");
    }

    @Test
    void checkIfCanAddDiffTypes() {
        TaskManager manager = Managers.getDefault();
        Task task1 = new Task(1, "Task1", TaskStatus.NEW, "description");
        manager.addNewTask(task1);
        Epic epic1 = new Epic(2, "Epic1", TaskStatus.NEW, "description");
        manager.addNewEpic(epic1);
        Subtask subtask1 = new Subtask(3, "Subtask1", TaskStatus.NEW, "description", 2);
        manager.addNewSubtask(subtask1);
        manager.getTask(1);
        manager.getEpic(2);
        manager.getSubtask(3);
        assertEquals(manager.getHistory().size(), 3);
    }

    @Test
    void immutabilityAddingTaskToManager(){
        TaskManager manager = Managers.getDefault();
        Task task1 = new Task(1, "Task1", TaskStatus.NEW, "description1");
        manager.addNewTask(task1);
        Task[] arrayOne = new Task[]{task1};
        Task task2 = manager.getTask(1);
        Task[] arrayTwo = new Task[]{task2};
        assertArrayEquals(arrayOne, arrayTwo);
    }

    @Test
    void givenIdGeneratedIdDontConflict(){
        TaskManager manager = Managers.getDefault();
        Task task1 = new Task(1, "Task1", TaskStatus.NEW, "description1");
        manager.addNewTask(task1);
        Task[] arrayOne = new Task[]{task1};
        Task task2 = new Task(manager.generatorId(), "Task2", TaskStatus.NEW, "description2");
        manager.addNewTask(task2);
        Task[] arrayTwo = new Task[]{task2};
        assertArrayEquals(arrayOne, arrayTwo, "Задачи с заданным id и сгенерированным id не конфликтуют " +
                "внутри менеджера");
    }
}