package com.yandex.app.service;

import com.yandex.app.model.Task;

import java.util.HashMap;
import java.util.List;

public interface HistoryManager {
    List<Task> getHistory();

    void addTask(Task task);
}
