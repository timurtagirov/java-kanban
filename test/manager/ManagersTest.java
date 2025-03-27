package manager;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class ManagersTest {

    private final Managers managers = new Managers();

    @Test
    public void shouldReturnInitializedTaskManager() {
        TaskManager taskManager = managers.getDefault();
        assertNotNull(taskManager);
    }
}