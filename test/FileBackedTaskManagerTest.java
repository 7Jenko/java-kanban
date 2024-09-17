import com.yandex.app.model.Task;
import com.yandex.app.model.TaskType;
import com.yandex.app.service.FileBackedTaskManager;
import com.yandex.app.status.TaskStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

class FileBackedTaskManagerTest {
    private FileBackedTaskManager fileBackedTaskManager;
    private File tempFile;

    @BeforeEach
    void beforeEach() throws IOException {
        tempFile = File.createTempFile("file_backed_taskManager_test", ".csv");
        fileBackedTaskManager = new FileBackedTaskManager(tempFile);
    }

    @Test
    void savingAndLoadingEmptyFile() {
        List<Task> tasks = fileBackedTaskManager.getAllTasks();
        Assertions.assertTrue(tasks.isEmpty(), "Список задач должен быть пуст после загрузки " +
                "из пустого файла.");
    }

    @Test
    void savingAndLoadingMultipleTasks() throws IOException {
        Task task1 = new Task(1, TaskType.TASK, "Task1", TaskStatus.NEW, "description", Duration.ofMinutes(10), LocalDateTime.now());
        fileBackedTaskManager.addNewTask(task1);

        Task task2 = new Task(2, TaskType.TASK, "Task2", TaskStatus.NEW, "description", Duration.ofMinutes(10), LocalDateTime.now().plusHours(1));
        fileBackedTaskManager.addNewTask(task2);

        fileBackedTaskManager.save();

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);
        List<Task> loadedTasks = loadedManager.getAllTasks();

        Assertions.assertEquals(2, loadedTasks.size(), "Должно быть загружено столько же задач, " +
                "сколько сохранено");
        Assertions.assertEquals(task1, loadedTasks.get(0), "Задача должна совпадать с сохраненной.");
        Assertions.assertEquals(task2, loadedTasks.get(1), "Задача должна совпадать с сохраненной.");
        fileBackedTaskManager.removeTasks();
    }
}