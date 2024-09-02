package com.yandex.app.service;

import com.yandex.app.model.Epic;
import com.yandex.app.model.Subtask;
import com.yandex.app.model.Task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public interface TaskManager {
    ArrayList<Task> getAllTasks();

    ArrayList<Epic> getAllEpics();

    ArrayList<Subtask> getAllSubtasks();

    int generatorId();

    int addNewTask(Task task) throws IOException;

    int addNewEpic(Epic epic) throws IOException;

    int addNewSubtask(Subtask subtask) throws IOException;

    List<Task> getTasks();

    Task getTask(int id);

    List<Epic> getEpics();

    Task getEpic(int id);

    List<Subtask> getSubtasks();

    List<Subtask> getEpicSubtasks(int id);

    Task getSubtask(int id);

    void removeTasks() throws IOException;

    void removeEpics() throws IOException;

    void removeSubtasks() throws IOException;

    Task getTaskById(int id);

    Epic getEpicById(int id);

    Subtask getSubtaskById(int id);

    Task updateTask(Task task) throws IOException;

    Task updateEpic(Epic epic) throws IOException;

    Subtask updateSubtask(Subtask subtask) throws IOException;

    void removeTaskById(int id) throws IOException;

    void removeEpicById(int id) throws IOException;

    void removeSubtaskById(int id) throws IOException;

    List<Task> getHistory();
}
