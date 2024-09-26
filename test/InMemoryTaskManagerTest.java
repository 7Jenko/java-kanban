import com.yandex.app.model.Epic;
import com.yandex.app.model.Subtask;
import com.yandex.app.model.Task;
import com.yandex.app.model.TaskType;
import com.yandex.app.service.Managers;
import com.yandex.app.service.TaskManager;
import com.yandex.app.status.TaskStatus;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {
    @Test
    void taskIdEquals() {
        Task task1 = new Task(1, "Task1", TaskStatus.NEW, "description");
        Task task2 = new Task(1, "Task1", TaskStatus.DONE, "description");
        assertNotEquals(task1, task2, "Экземпляры класса Task должны быть равны друг другу, если равен их id;");
    }

    @Test
    void epicIdEquals() {
        Epic epic1 = new Epic(2, "Epic2", TaskStatus.NEW, "description");
        Epic epic2 = new Epic(2, "Epic2", TaskStatus.DONE, "description");
        assertNotEquals(epic1, epic2, "Наследники класса Task должны быть равны друг другу, если равен их id;");
    }

    @Test
    void subtaskIdEquals() {
        Subtask subtask1 = new Subtask(3, "Subtask1", TaskStatus.NEW, "description", 2);
        Subtask subtask2 = new Subtask(3, "Subtask1", TaskStatus.DONE, "description", 2);
        assertNotEquals(subtask1, subtask2, "Наследники класса Task должны быть равны друг другу, " +
                "если равен их id;");
    }

    @Test
    void epicCannotAddedToItselfAsASubtask() throws IOException {
        TaskManager manager = Managers.getDefault();
        Epic epic1 = new Epic(1, "Epic1", TaskStatus.NEW, "description");
        manager.addNewEpic(epic1);
        Subtask subtask = new Subtask(1, "Epic1", TaskStatus.NEW, "description", 1);
        manager.addNewSubtask(subtask);
        assertFalse(manager.getEpics().contains(subtask), "Объект Epic нельзя добавить в самого себя " +
                "в виде подзадачи");
    }

    @Test
    public void subtaskCannotBeMadeIntoItsOwnEpic() throws IOException {
        TaskManager manager = Managers.getDefault();
        Epic epic1 = new Epic(1, "Epic1", TaskStatus.NEW, "description");
        manager.addNewEpic(epic1);
        Subtask subtask = new Subtask(1, "Epic1", TaskStatus.NEW, "description", 1);
        manager.addNewSubtask(subtask);
        assertFalse(manager.getSubtasks().contains(epic1), "Объект Subtask нельзя сделать своим же эпиком");
    }

    @Test
    void checkIfCanAddDiffTypes() throws IOException {
        TaskManager manager = Managers.getDefault();
        Task task1 = new Task(1, "Task1", TaskStatus.NEW, "description");
        manager.addNewTask(task1);
        Epic epic1 = new Epic(2, "Epic1", TaskStatus.NEW, "description");
        manager.addNewEpic(epic1);
        Subtask subtask1 = new Subtask(3, "Subtask1", TaskStatus.NEW, "description", 2);
        manager.addNewSubtask(subtask1);
        manager.getTask(1);
        manager.getEpic(2);
        manager.getSubtask(3);
        assertEquals(manager.getHistory().size(), 3);
    }

    @Test
    void immutabilityAddingTaskToManager() throws IOException {
        TaskManager manager = Managers.getDefault();
        Task task1 = new Task(1, "Task1", TaskStatus.NEW, "description1");
        manager.addNewTask(task1);
        Task[] arrayOne = new Task[]{task1};
        Task task2 = manager.getTask(1);
        Task[] arrayTwo = new Task[]{task2};
        assertArrayEquals(arrayOne, arrayTwo);
    }

    @Test
    void givenIdGeneratedIdDontConflict() throws IOException {
        TaskManager manager = Managers.getDefault();
        Task task1 = new Task(1, "Task1", TaskStatus.NEW, "description1");
        manager.addNewTask(task1);
        Task[] arrayOne = new Task[]{task1};
        Task task2 = new Task(manager.generatorId(), "Task2", TaskStatus.NEW, "description2");
        manager.addNewTask(task2);
        Task[] arrayTwo = new Task[]{task2};
        assertNotEquals(arrayOne, arrayTwo, "Задачи с заданным id и сгенерированным id не конфликтуют " +
                "внутри менеджера");
    }

    @Test
    void deletedSubtasksShouldNotStoreOldIds() throws IOException {
        TaskManager manager = Managers.getDefault();
        Epic epic1 = new Epic(1, "Epic1", TaskStatus.NEW, "description");
        manager.addNewEpic(epic1);
        Subtask subtask1 = new Subtask(2, "Subtask1", TaskStatus.NEW, "description", 1);
        manager.addNewSubtask(subtask1);
        manager.removeSubtaskById(2);
        assertNotEquals(manager.getSubtasks(), "Удаляемая подзадача не хранит в себе старый Id");
        }

    @Test
    void shouldBeNoIrrelevantIdSubtasksInsideEpics() throws IOException {
        TaskManager manager = Managers.getDefault();
        Epic epic1 = new Epic(1, "Epic1", TaskStatus.NEW, "description");
        manager.addNewEpic(epic1);
        Subtask subtask1 = new Subtask(2, "Subtask1", TaskStatus.NEW, "description", 1);
        manager.addNewSubtask(subtask1);
        manager.removeSubtaskById(2);
        assertNotEquals(manager.getEpicSubtasks(1), "Список эпиков пуст");
    }

    @Test
    void usingSettersAllowToChangeTheirFields() throws IOException {
        TaskManager manager = Managers.getDefault();
        Task task1 = new Task(1, "Task1", TaskStatus.NEW, "description1");
        final int taskId1 = manager.addNewTask(task1);
        /*final*/ Task task2 = new Task("Task2", TaskStatus.NEW, "description2");
        task2 = new Task(taskId1, task2.getName(), task2.getStatus(), task2.getDescription());
        Task[] arrayOne = new Task[]{task1};
        Task[] arrayTwo = new Task[]{task2};
        assertNotEquals(arrayOne, arrayTwo, "Задачи с одинаковыми id должны быть одинаковыми. " +
                "Чтобы не допустить изменений полей с помощью сеттеров, используйте final в объявлении задач.");
    }
    @Test
    public void checkEpicsStatusIsNew() {
        TaskManager manager = Managers.getDefault();
        Epic epic = new Epic(1, TaskType.EPIC, "Epic", TaskStatus.DONE, "description",
                new ArrayList());
        manager.addNewEpic(epic);
        Subtask subtask1 = new Subtask(2,TaskType.SUBTASK,"Subtask1", TaskStatus.NEW,
                "description", epic.getId());
        Subtask subtask2 = new Subtask(3,TaskType.SUBTASK, "Subtask2", TaskStatus.NEW,
                "description", epic.getId());
        manager.addNewSubtask(subtask1);
        manager.addNewSubtask(subtask2);
        assertEquals(TaskStatus.NEW, epic.getStatus());
    }

    @Test
    public void checkEpicsStatusIsDone() {
        TaskManager manager = Managers.getDefault();
        Epic epic = new Epic(1, TaskType.EPIC, "Epic", TaskStatus.DONE, "description",
                new ArrayList());
        manager.addNewEpic(epic);
        Subtask subtask1 = new Subtask(2,TaskType.SUBTASK,"Subtask1", TaskStatus.DONE,
                "description", epic.getId());
        Subtask subtask2 = new Subtask(3, TaskType.SUBTASK, "Subtask2", TaskStatus.DONE,
                "description", epic.getId());
        manager.addNewSubtask(subtask1);
        manager.addNewSubtask(subtask2);
        assertEquals(TaskStatus.DONE, epic.getStatus());
    }

    @Test
    public void checkEpicsStatusIsInProgress() {
        TaskManager manager = Managers.getDefault();
        Epic epic = new Epic(1, TaskType.EPIC, "Epic", TaskStatus.NEW, "description",
                new ArrayList());
        manager.addNewEpic(epic);
        Subtask subtask1 = new Subtask(2,TaskType.SUBTASK,"Subtask1", TaskStatus.DONE,
                "description", epic.getId());
        Subtask subtask2 = new Subtask(3, TaskType.SUBTASK, "Subtask2", TaskStatus.NEW,
                "description", epic.getId());
        manager.addNewSubtask(subtask1);
        manager.addNewSubtask(subtask2);
        assertEquals(TaskStatus.IN_PROGRESS, epic.getStatus());
    }

    @Test
    public void checkSubtaskNotEmpty() {
        TaskManager manager = Managers.getDefault();
        Epic epic1 = new Epic("Epic", "description");
        manager.addNewEpic(epic1);
        Subtask subtaskWithTime = new Subtask(2, TaskType.SUBTASK, "Subtask 1", TaskStatus.NEW,
                "description", Duration.ofMinutes(120), LocalDateTime.now(), epic1.getId());
        manager.addNewSubtask(subtaskWithTime);
        Epic epic = manager.getEpicById(subtaskWithTime.getEpicId());
        assertNotNull(subtaskWithTime);
        assertEquals(subtaskWithTime.getDuration(), epic.getDuration());
        assertEquals(subtaskWithTime.getStartTime(), epic.getStartTime());
        assertEquals(epic.getEndTime(), epic.getStartTime().plus(epic.getDuration()));
    }

    @Test
    public void shouldNotAddOverlappingTasks() {
        TaskManager manager = Managers.getDefault();
        Task task1 = new Task("Task 1", "description", Duration.ofMinutes(10), LocalDateTime.now());
        manager.addNewTask(task1);

        Task task2 = new Task("Task 2", "description", Duration.ofMinutes(10), LocalDateTime.now().plusMinutes(5));

        int initialSize = manager.getPrioritizedTasks().size();
        int result = manager.addNewTask(task2);

        assertEquals(initialSize, manager.getPrioritizedTasks().size(), "Пересекающаяся задача не должна добавляться");
        assertEquals(-1, result, "Метод должен вернуть -1 при пересечении");
    }

}
