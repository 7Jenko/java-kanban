package com.yandex.app.service;

import com.yandex.app.status.TaskStatus;
import com.yandex.app.model.Epic;
import com.yandex.app.model.Subtask;
import com.yandex.app.model.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    protected final Map<Integer, Task> tasks = new HashMap<>();
    protected final Map<Integer, Subtask> subtasks = new HashMap<>();
    protected final Map<Integer, Epic> epics = new HashMap<>();
    private final HistoryManager historyManager = Managers.getDefaultHistory();

    protected Set<Task> prioritizedTasks = new TreeSet<>((task1, task2) -> {
        if (task1.getStartTime() == null && task2.getStartTime() == null) {
            return 0;
        }
        if (task1.getStartTime() == null) {
            return -1;
        }
        if (task2.getStartTime() == null) {
            return 1;
        }
        return task1.getStartTime().compareTo(task2.getStartTime());
    });

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
    public int addNewTask(Task task) {
        if (overlapTask(task)) {
            System.out.println("Задача пересекается по времени с уже существующими задачами и не была добавлена.");
            return -1;
        }
        final int id = ++generatorId;
        task.setId(id);
        tasks.put(id, task);
        updatePrioritizedTasks(task);
        return id;
    }

    @Override
    public int addNewEpic(Epic epic) {
        if (overlapTask(epic)) {
            System.out.println("Эпик пересекается по времени с уже существующими задачами и не был добавлен.");
            return -1;
        }
        final int id = ++generatorId;
        epic.setId(id);
        epics.put(id, epic);
        return id;
    }

    @Override
    public int addNewSubtask(Subtask subtask)  {
        overlapTask(subtask);
        if (overlapTask(subtask)) {
            System.out.println("Подзадача пересекается по времени с уже существующими задачами и не была добавлена.");
            return -1;
        }
        final int id = ++generatorId;
        subtask.setId(id);
        subtasks.put(id, subtask);
        Epic epic = epics.get(subtask.getEpicId());
        epic.addSubtask(subtask);
        epic.setDuration(getDuration(epic));
        epic.setStartTime(getStartTime(epic));
        epic.setEndTime(getEndTime(epic));
        updatePrioritizedTasks(subtask);
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
    public void removeTasks() {
        tasks.clear();
    }

    @Override
    public void removeEpics() {
        epics.clear();
        subtasks.clear();
    }

    @Override
    public void removeSubtasks() {
        subtasks.clear();
        epics.values().forEach(epic -> {
            epic.removeSubtasks();
            epic.setStatus(TaskStatus.NEW);
        });
    }

    @Override
    public Task getTaskById(int id) {
        Task task = tasks.get(id);
        if (task != null) {
            historyManager.addTask(task);
        }
        return task;
    }

    @Override
    public Epic getEpicById(int id) {
        Epic epic = epics.get(id);
        if (epic != null) {
            historyManager.addTask(epic);
        }
        return epic;
    }

    @Override
    public Subtask getSubtaskById(int id) {
        Subtask subtask = subtasks.get(id);
        if (subtask != null) {
            historyManager.addTask(subtask);
        }
        return subtask;
    }

    @Override
    public Task updateTask(Task task) {
        overlapTask(task);
        tasks.put(task.getId(), task);
        updatePrioritizedTasks(task);
        return task;
    }

    @Override
    public Task updateEpic(Epic epic)  {
        Epic oldEpic = epics.get(epic.getId());
        oldEpic.setName(epic.getName());
        oldEpic.setDescription(epic.getDescription());
        updatePrioritizedTasks(epic);
        return oldEpic;
    }

    @Override
    public Subtask updateSubtask(Subtask subtask) {
        int epicId = subtask.getEpicId();
        Subtask oldSubtask = subtasks.get(subtask.getId());
        oldSubtask.setName(subtask.getName());
        oldSubtask.setStatus(subtask.getStatus());
        Epic epic = epics.get(epicId);
        epic.setDuration(getDuration(epic));
        epic.setStartTime(getStartTime(epic));
        epic.setEndTime(getEndTime(epic));
        updatePrioritizedTasks(subtask);
        updateEpicStatus(epic);
        return subtask;
    }

    @Override
    public void removeTaskById(int id) {
        tasks.remove(id);
    }

    @Override
    public void removeEpicById(int id) {
        List<Subtask> epicSubtasks = epics.remove(id).getSubtaskList();
        epicSubtasks.stream()
                .map(Subtask::getId)
                .forEach(subtasks::remove);
    }

    @Override
    public void removeSubtaskById(int id) {
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

    @Override
    public Set<Task> getPrioritizedTasks() {
        return prioritizedTasks;
    }

    public LocalDateTime getStartTime(Epic epic) {
        List<Subtask> subtasks = getEpicSubtasks(epic.getId());
        return subtasks.stream()
                .map(Subtask::getStartTime)
                .filter(Objects::nonNull)
                .min(LocalDateTime::compareTo)
                .orElse(null);
    }

    public LocalDateTime getEndTime(Epic epic) {
        return Optional.ofNullable(epic.getStartTime())
                .map(startTime -> startTime.plus(epic.getDuration()))
                .orElse(null);
    }

    public Duration getDuration(Epic epic) {
        List<Subtask> subtasks = getEpicSubtasks(epic.getId());
        return subtasks.stream()
                .map(Subtask::getDuration)
                .filter(Objects::nonNull)
                .reduce(Duration.ZERO, Duration::plus);
    }

    private void updatePrioritizedTasks(Task task) {
            prioritizedTasks.add(task);
    }

    private boolean overlapTask(Task task) {
        if (task.getStartTime() == null) {
            return false;
        }
        return prioritizedTasks.stream()
                .anyMatch(task1 -> {
                    LocalDateTime endTime = task.getEndTime();
                    return (endTime.isAfter(task1.getStartTime()) && task.getStartTime().isBefore(task1.getEndTime()));
                });
    }

    private void updateEpicStatus(Epic epic) {
        if (epic.getSubtaskList().isEmpty()) {
            epic.setStatus(TaskStatus.NEW);
        } else {
            List<Subtask> subtaskList = epic.getSubtaskList();

            long doneCount = subtaskList.stream()
                    .filter(subtask -> subtask.getStatus() == TaskStatus.DONE)
                    .count();

            long newCount = subtaskList.stream()
                    .filter(subtask -> subtask.getStatus() == TaskStatus.NEW)
                    .count();

            boolean hasOtherStatus = subtaskList.stream()
                    .anyMatch(subtask -> subtask.getStatus() != TaskStatus.DONE && subtask.getStatus() != TaskStatus.NEW);

            if (hasOtherStatus) {
                epic.setStatus(TaskStatus.IN_PROGRESS);
            } else if (doneCount == subtaskList.size()) {
                epic.setStatus(TaskStatus.DONE);
            } else if (newCount == subtaskList.size()) {
                epic.setStatus(TaskStatus.NEW);
            } else {
                epic.setStatus(TaskStatus.IN_PROGRESS);
            }
        }
    }
}