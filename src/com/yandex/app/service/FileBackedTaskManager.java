package com.yandex.app.service;

import com.yandex.app.model.Epic;
import com.yandex.app.model.Subtask;
import com.yandex.app.model.Task;
import com.yandex.app.model.TaskType;
import com.yandex.app.status.TaskStatus;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;

public class FileBackedTaskManager extends InMemoryTaskManager {

    private final File file;
    public static final String Header = "id,type,name,status,description,duration,startTime,epic \n";

    public FileBackedTaskManager(File file) {
        this.file = file;
    }

    public void save() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, StandardCharsets.UTF_8))) {
            writer.write(Header);
            getAllTasks().stream()
                    .map(Task::toString)
                    .forEach(taskString -> {
                        try {
                            writer.write(taskString + "\n");
                        } catch (IOException exp) {
                            throw new ManagerException("Ошибка сохранения задачи.");
                        }
                    });

            getAllEpics().stream()
                    .map(Epic::toString)
                    .forEach(epicString -> {
                        try {
                            writer.write(epicString + "\n");
                        } catch (IOException exp) {
                            throw new ManagerException("Ошибка сохранения эпика.");
                        }
                    });

            getAllSubtasks().stream()
                    .map(Subtask::toString)
                    .forEach(subtaskString -> {
                        try {
                            writer.write(subtaskString + "\n");
                        } catch (IOException exp) {
                            throw new ManagerException("Ошибка сохранения подзадачи.");
                        }
                    });
        } catch (IOException e) {
            throw new ManagerException("Can`t read from file: " + file.getName());
        }
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager manager = new FileBackedTaskManager(file);
        try (BufferedReader reader = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {
            reader.readLine();
            while (reader.ready()) {
                String line = reader.readLine();
                if (line.isEmpty()) {
                    continue;
                }
                Task task = manager.fromString(line);

                if (task.getType() == TaskType.TASK) {
                    manager.tasks.put(task.getId(), task);
                } else if (task.getType() == TaskType.EPIC) {
                    manager.epics.put(task.getId(), (Epic) task);
                } else if (task.getType() == TaskType.SUBTASK) {
                    manager.subtasks.put(task.getId(), (Subtask) task);
                }
            }
        } catch (IOException e) {
            throw new ManagerException("Can`t read from file: " + file.getName());
        }
        return manager;
    }

    @Override
    public int addNewTask(Task task)  {
        save();
        return super.addNewTask(task);
    }

    @Override
    public int addNewEpic(Epic epic) {
        save();
        return super.addNewEpic(epic);
    }

    @Override
    public int addNewSubtask(Subtask subtask) {
        save();
        return super.addNewSubtask(subtask);
    }

    @Override
    public void removeTasks() {
        super.removeTasks();
        save();
    }

    @Override
    public void removeEpics() {
        super.removeEpics();
        save();
    }

    @Override
    public void removeSubtasks() {
        super.removeSubtasks();
        save();
    }

    @Override
    public Task updateTask(Task task) {
        save();
        return super.updateTask(task);
    }

    @Override
    public Task updateEpic(Epic epic) {
        save();
        return super.updateEpic(epic);
    }

    @Override
    public Subtask updateSubtask(Subtask subtask) {
        save();
        return super.updateSubtask(subtask);
    }

    @Override
    public void removeTaskById(int id) {
        super.removeTaskById(id);
        save();
    }

    @Override
    public void removeEpicById(int id) {
        super.removeEpicById(id);
        save();
    }

    @Override
    public void removeSubtaskById(int id) {
        super.removeSubtaskById(id);
        save();
    }

    private Task fromString(String value) {
        String[] line = value.split(",");
        int id = Integer.parseInt(line[0]);
        String taskType = line[1];
        String name = line[2];
        TaskStatus status = TaskStatus.valueOf(line[3]);
        String description = line[4];
        Duration duration = Duration.parse(line[5]);
        LocalDateTime startTime = LocalDateTime.parse(line[6]);

        switch (taskType) {
            case "TASK":
                Task task = new Task(name, description, duration, startTime);
                task.setStatus(status);
                task.setId(id);
                return task;
            case "EPIC":
                Epic epic = new Epic(name, description, duration, startTime);
                epic.setId(id);
                return epic;
            case "SUBTASK":
                int epicId = Integer.parseInt(line[7]);
                Subtask subtask = new Subtask(name, description, epicId, duration, startTime);
                subtask.setId(id);
                subtask.setStatus(status);
                return subtask;
            default:
                throw new IllegalArgumentException("Unknown type: " + taskType);
        }
    }
}
