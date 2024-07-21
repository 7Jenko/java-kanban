package com.yandex.app.service;

import com.yandex.app.model.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager{
    private final ArrayList<Task> list = new ArrayList<>();

    @Override
    public List<Task> getHistory() {
        return list;
    }

    @Override
    public void addTask(Task task) {
        if (list.size() == 10){
            list.removeFirst();
            list.add(task);
        } else {
            list.add(task);
        }
    }
}
