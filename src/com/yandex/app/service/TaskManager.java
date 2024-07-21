package com.yandex.app.service;

import com.yandex.app.model.Epic;
import com.yandex.app.model.Subtask;
import com.yandex.app.model.Task;

import java.util.ArrayList;
import java.util.List;

public interface TaskManager {
    int generatorId();

    int addNewTask(Task task);

    int addNewEpic(Epic epic);

    int addNewSubtask(Subtask subtask);

    ArrayList<Task> getTasks();

    Task getTask(int id);

    ArrayList<Epic> getEpics();

    Task getEpic(int id);

    ArrayList<Subtask> getSubtasks();

    ArrayList<Subtask> getEpicSubtasks(int id);

    Task getSubtask(int id);

    void removeTasks();

    void removeEpics();

    void removeSubtasks();

    Task getTaskById(int id);

    Epic getEpicById(int id);

    Subtask getSubtaskById(int id);

    Task updateTask(Task task);

    Task updateEpic(Epic epic);

    Subtask updateSubtask(Subtask subtask);

    void removeTaskById(int id);

    void removeEpicById(int id);

    void removeSubtaskById(int id);

    List<Task> getHistory();
}
