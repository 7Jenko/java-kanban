package com.yandex.app.service;

import com.yandex.app.model.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private final List<Task> list = new ArrayList<>();
    private static final int LIST_SIZE_FOR_HISTORY = 10;

    @Override
    public List<Task> getHistory() {
        return List.copyOf(list);
    }

    @Override
    public void addTask(Task task) {
        if (task != null) {
            if (list.size() >= LIST_SIZE_FOR_HISTORY) {
                list.removeFirst();
            }
            list.add(task);
        }
    }
}
