package com.yandex.app.service;

import com.yandex.app.status.TaskStatus;
import com.yandex.app.model.Epic;
import com.yandex.app.model.Subtask;
import com.yandex.app.model.Task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryTaskManager implements TaskManager {
    protected static final Map<Integer, Task> tasks = new HashMap<>();
    protected static final Map<Integer, Subtask> subtasks = new HashMap<>();
    protected static final Map<Integer, Epic> epics = new HashMap<>();
    private final HistoryManager historyManager = Managers.getDefaultHistory();

    protected int generatorId = 0;

    @Override
    public ArrayList<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public ArrayList<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public ArrayList<Subtask> getAllSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public int generatorId() {
        generatorId++;
        return generatorId;
    }

    @Override
    public int addNewTask(Task task) throws IOException {
        final int id = ++generatorId;
        task.setId(id);
        tasks.put(id, task);
        return id;
    }

    @Override
    public int addNewEpic(Epic epic) throws IOException {
        final int id = ++generatorId;
        epic.setId(id);
        epics.put(id, epic);
        return id;
    }

    @Override
    public int addNewSubtask(Subtask subtask) throws IOException {
        final int id = ++generatorId;
        subtask.setId(id);
        subtasks.put(id, subtask);
        Epic epic = epics.get(subtask.getEpicId());
        epic.addSubtask(subtask);
        updateEpicStatus(epic);
        return id;
    }

    @Override
    public List<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public Task getTask(int id) {
        final Task task = tasks.get(id);
        historyManager.addTask(task);
        return task;
    }

    public List<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public Task getEpic(int id) {
        final Epic epic = epics.get(id);
        historyManager.addTask(epic);
        return epic;
    }

    public List<Subtask> getSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public List<Subtask> getEpicSubtasks(int id) {
        Epic epic = epics.get(id);
        return epic.getSubtaskList();
    }

    @Override
    public Task getSubtask(int id) {
        final Subtask subtask = subtasks.get(id);
        historyManager.addTask(subtask);
        return subtask;
    }

    @Override
    public void removeTasks() throws IOException {
        tasks.clear();
    }

    @Override
    public void removeEpics() throws IOException {
        epics.clear();
        subtasks.clear();
    }

    @Override
    public void removeSubtasks() throws IOException {
        subtasks.clear();
        for (Epic epic : epics.values()) {
            epic.removeSubtasks();
            epic.setStatus(TaskStatus.NEW);
        }
    }

    @Override
    public Task getTaskById(int id) {
        return tasks.get(id);
    }

    @Override
    public Epic getEpicById(int id) {
        return epics.get(id);
    }

    @Override
    public Subtask getSubtaskById(int id) {
        return subtasks.get(id);
    }

    @Override
    public Task updateTask(Task task) throws IOException {
        tasks.put(task.getId(), task);
        return task;
    }

    @Override
    public Task updateEpic(Epic epic) throws IOException {
        Epic oldEpic = epics.get(epic.getId());
        oldEpic.setName(epic.getName());
        oldEpic.setDescription(epic.getDescription());
        return oldEpic;
    }

    @Override
    public Subtask updateSubtask(Subtask subtask) throws IOException {
        int epicId = subtask.getEpicId();
        Subtask oldSubtask = subtasks.get(subtask.getId());
        oldSubtask.setName(subtask.getName());
        oldSubtask.setStatus(subtask.getStatus());
        Epic epic = epics.get(epicId);
        updateEpicStatus(epic);
        return subtask;
    }

    @Override
    public void removeTaskById(int id) throws IOException {
        tasks.remove(id);
    }

    @Override
    public void removeEpicById(int id) throws IOException {
        ArrayList<Subtask> epicSubtasks = epics.remove(id).getSubtaskList();
        for (Subtask subtask : epicSubtasks) {
            subtasks.remove(subtask.getId());
        }
    }

    @Override
    public void removeSubtaskById(int id) throws IOException {
        Subtask subtask = subtasks.remove(id);
        int epicId = subtask.getEpicId();
        Epic epic = epics.get(epicId);
        ArrayList<Subtask> subtaskList = epic.getSubtaskList();
        subtaskList.remove(subtask);
        updateEpicStatus(epic);
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    private void updateEpicStatus(Epic epic) {
        if (epic.getSubtaskList().isEmpty()) {
            epic.setStatus(TaskStatus.NEW);
        } else {
            int doneCount = 0;
            int newCount = 0;

            List<Subtask> subtaskList = epic.getSubtaskList();

            for (Subtask subtask : subtaskList) {
                if (subtask.getStatus() == TaskStatus.DONE) {
                    doneCount++;
                } else if (subtask.getStatus() == TaskStatus.NEW) {
                    newCount++;
                } else {
                    epic.setStatus(TaskStatus.IN_PROGRESS);
                    return;
                }
            }
            if (doneCount == subtaskList.size()) {
                epic.setStatus(TaskStatus.DONE);
            } else if (newCount == subtaskList.size()) {
                epic.setStatus(TaskStatus.NEW);
            } else {
                epic.setStatus(TaskStatus.IN_PROGRESS);
            }
        }
    }
}