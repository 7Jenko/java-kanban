package com.yandex.app.test;

import com.yandex.app.model.Task;
import com.yandex.app.service.HistoryManager;
import com.yandex.app.service.Managers;
import com.yandex.app.status.TaskStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

public class InMemoryHistoryManagerTest {
    @Test
    void addTaskInHistory() {
        HistoryManager manager = Managers.getDefaultHistory();
        Task task1 = new Task(1, "Task1", TaskStatus.NEW, "description");
        manager.addTask(task1);
        final List<Task> history = manager.getHistory();
        Assertions.assertEquals(history, "В истории просмотров есть " + task1);
    }

    @Test
    void removeTaskFromHistory() {
        HistoryManager manager = Managers.getDefaultHistory();
        Task task1 = new Task(1, "Task1", TaskStatus.NEW, "description");
        manager.addTask(task1);
        manager.remove(1);
        final List<Task> history = manager.getHistory();
        Assertions.assertEquals(history, "История просмотров пуста");
    }
}
