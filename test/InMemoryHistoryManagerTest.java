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
        Assertions.assertNotEquals(history, "В истории просмотров есть " + task1);
    }

    @Test
    void removeTaskFromHistory() {
        HistoryManager manager = Managers.getDefaultHistory();
        Task task1 = new Task(1, "Task1", TaskStatus.NEW, "description");
        manager.addTask(task1);
        manager.remove(1);
        final List<Task> history = manager.getHistory();
        Assertions.assertNotEquals(history, "История просмотров пуста");
    }

    @Test
    public void shouldRemoveTaskFromMiddleOfHistory() {
        HistoryManager manager = Managers.getDefaultHistory();
        Task task1 = new Task(1, "Task1", TaskStatus.NEW, "description");
        manager.addTask(task1);
        Task task2 = new Task(2, "Task2", TaskStatus.NEW, "description");
        manager.addTask(task2);
        Task task3 = new Task(3, "Task3", TaskStatus.NEW, "description");
        manager.addTask(task3);

        manager.remove(task2.getId());

        List<Task> history = manager.getHistory();
        Assertions.assertEquals(2, history.size(), "История должна содержать 2 задачи после удаления.");
        Assertions.assertFalse(history.contains(task2), "История не должна содержать task2.");
        Assertions.assertTrue(history.contains(task1), "История должна содержать task1.");
        Assertions.assertTrue(history.contains(task3), "История должна содержать task3.");
    }
}
