package com.yandex.app.model;
import com.yandex.app.status.TaskStatus;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Objects;

public class Epic extends Task {

    private ArrayList<Subtask> subtaskList = new ArrayList<>();

    private LocalDateTime endTime;

    public Epic(int id, String name, TaskStatus status, String description) {
        super(id, name, status, description);
    }

    public Epic(String name, TaskStatus status, String description) {
        super(name, status, description);

    }

    public Epic(String name, String description) {
        super(name, description);
    }

    public Epic(Integer id, TaskType type, String name, TaskStatus status, String description,
                ArrayList<Subtask> subtaskList) {
        super(id, name, status, description);
        this.type = type;
        this.subtaskList = subtaskList;
    }

    public Epic(Integer id, TaskType type, String name, TaskStatus status, String description,
                ArrayList<Subtask> subtaskList, Duration duration, LocalDateTime startTime, LocalDateTime endTime) {
        super(id, type, name, status, description, duration, startTime);
        this.subtaskList = subtaskList;
        this.endTime = endTime;
    }

    public Epic(String name, String description, Duration duration, LocalDateTime startTime) {
        super(name, description, duration, startTime);
    }

    public void addSubtask(Subtask subtask) {
        subtaskList.add(subtask);
    }

    public ArrayList<Subtask> getSubtaskList() {
        return subtaskList;
    }

    public void removeSubtasks() {
        subtaskList.clear();
    }

    public void setSubtaskList(ArrayList<Subtask> subtaskList) {
        this.subtaskList = subtaskList;
    }

    @Override
    public TaskType getType() {
        return TaskType.EPIC;
    }

    @Override
    public String toString() {
        return id +
                "," + getType() +
                "," + name +
                "," + status +
                "," + description +
                "," + duration+
                "," + startTime;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        if (!super.equals(object)) return false;
        Epic epic = (Epic) object;
        return Objects.equals(subtaskList, epic.subtaskList);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), subtaskList);
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }
}
