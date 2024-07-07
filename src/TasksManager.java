import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.TaskStatus;

import java.util.ArrayList;
import java.util.HashMap;

public class TasksManager {
    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, Subtask> subtasks = new HashMap<>();
    private final HashMap<Integer, Epic> epics = new HashMap<>();

    private int generatorId = 0;

    public int addNewTask(Task task) {
        final int id = ++generatorId;
        task.setId(id);
        tasks.put(id, task);
        return id;
    }

    public int addNewEpic(Epic epic) {
        final int id = ++generatorId;
        epic.setId(id);
        epics.put(id, epic);
        return id;
    }

    public int addNewSubtask(Subtask subtask) {
        final int id = ++generatorId;
        subtask.setId(id);
        subtasks.put(id, subtask);
        Epic epic = epics.get(subtask.getEpicId());
        epic.addSubtask(subtask);
        subtasks.put(subtask.getId(), subtask);
        updateEpicStatus(epic);
        return id;
    }

    public void updateEpicStatus(Epic epic) {
        int doneCount = 0;
        int newCount = 0;

        ArrayList<Subtask> list = epic.getSubtaskList();

        for (Subtask subtask : list) {
            if (subtask.getStatus() == TaskStatus.DONE) {
                doneCount++;
            }
            if (subtask.getStatus() == TaskStatus.NEW) {
                newCount++;
            }
        }
        if (doneCount == list.size()) {
            epic.setStatus(TaskStatus.DONE);
        } else if (newCount == list.size()) {
            epic.setStatus(TaskStatus.NEW);
        } else {
            epic.setStatus(TaskStatus.IN_PROGRESS);
        }
    }

    public ArrayList<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    public ArrayList<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    public ArrayList<Subtask> getSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    public ArrayList<Subtask> getEpicSubtasks(Epic epic) {
        return epic.getSubtaskList();
    }

    public void removeTasks() {
        tasks.clear();
        epics.clear();
        subtasks.clear();
    }

    public void removeEpics() {
        epics.clear();
        subtasks.clear();
    }

    public void removeSubtasks() {
        subtasks.clear();
    }

    public Task getTaskById(int id) {
        return tasks.get(id);
    }

    public Epic getEpicById(int id) {
        return epics.get(id);
    }

    public Subtask getSubtaskById(int id) {
        return subtasks.get(id);
    }

    public Task updateTask(Task task) {
        task = new Task(task.getId(), "", TaskStatus.NEW, "");
        tasks.put(task.getId(), task);
        return task;
    }

    public Task updateEpic(Epic epic) {
        epic = new Epic(epic.getId(), "", TaskStatus.NEW, "");
        tasks.put(epic.getId(), epic);
        return epic;
    }

    public Subtask updateSubtask(Subtask subtask, Epic epic) {
        subtask = new Subtask(subtask.getId(), "", TaskStatus.NEW, "", subtask.getEpicId());
        tasks.put(subtask.getId(), subtask);
        updateEpicStatus(epic);
        return subtask;
    }

    public void removeTaskById(int id) {
        tasks.remove(id);
    }

    public void removeEpicById(int id) {
        epics.remove(id);
    }

    public void removeSubtaskById(int id) {
        subtasks.remove(id);
    }
}