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
    protected TreeSet<Task> prioritizedTasks = new TreeSet<>();

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
        overlapTask(task);
        final int id = ++generatorId;
        task.setId(id);
        tasks.put(id, task);
        updatePrioritizedTasks();
        return id;
    }

    @Override
    public int addNewEpic(Epic epic) {
        final int id = ++generatorId;
        epic.setId(id);
        epics.put(id, epic);
        overlapTask(epic);
        return id;
    }

    @Override
    public int addNewSubtask(Subtask subtask)  {
        overlapTask(subtask);
        final int id = ++generatorId;
        subtask.setId(id);
        subtasks.put(id, subtask);
        Epic epic = epics.get(subtask.getEpicId());
        epic.addSubtask(subtask);
        epic.setDuration(getDuration(epic));
        epic.setStartTime(getStartTime(epic));
        epic.setEndTime(getEndTime(epic));
        updatePrioritizedTasks();
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
    public Task updateTask(Task task) {
        overlapTask(task);
        tasks.put(task.getId(), task);
        updatePrioritizedTasks();
        return task;
    }

    @Override
    public Task updateEpic(Epic epic)  {
        Epic oldEpic = epics.get(epic.getId());
        oldEpic.setName(epic.getName());
        oldEpic.setDescription(epic.getDescription());
        updatePrioritizedTasks();
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
        updatePrioritizedTasks();
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
    public TreeSet<Task> getPrioritizedTasks() {
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

    private void updatePrioritizedTasks() {
        List<Task> tasks = getTasks();
        List<Subtask> subtasks = getSubtasks();
        tasks.addAll(subtasks);
        List<Task> tasksWithStartTime = tasks.stream()
                .filter(task -> task.getStartTime() != null)
                .toList();
        prioritizedTasks.addAll(tasksWithStartTime);
    }

    private void overlapTask(Task task) {
        if (task.getStartTime() == null) {
            return;
        }
        boolean overlapTime = prioritizedTasks.stream()
                .anyMatch(task1 -> {
                    LocalDateTime endTime = task.getStartTime().plus(task.getDuration());
                    return (endTime.isAfter(task1.getStartTime()) && task.getStartTime().isBefore(task1.getStartTime()
                            .plus(task1.getDuration())));
                });
        if (overlapTime) {
            throw new ManagerSaveException("Задачи пересекаются по времени");
        }
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