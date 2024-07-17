package com.yandex.app.model;
import com.yandex.app.Status.TaskStatus;

import java.util.ArrayList;

public class Epic extends Task {

    private ArrayList<Subtask> subtaskList = new ArrayList<>();
    /* Яков, привет. На вебинаре нам наставник говорил, что допустимо хранить и сабтасками и айдишниками, а у меня много
    методов завязано именно на хранении сабтасками. Если это не грубая ошибка, на данном этапе обучения, можно оставить
    таким образом?
    */

    public Epic(int id, String name, TaskStatus status, String description) {
        super(id, name, status, description);
    }

    public Epic(String name, TaskStatus status, String description) {
        super(name, status, description);

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
    public String toString() {
        return "Epic{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", status=" + status +
                ", description='" + description + '\'' +
                '}';
    }
}
