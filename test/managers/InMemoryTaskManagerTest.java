package managers;

import managers.history.InMemoryHistoryManager;
import managers.memory.InMemoryTaskManager;

public class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {
    @Override
    protected InMemoryTaskManager createTaskManager() {
        return new InMemoryTaskManager(new InMemoryHistoryManager());
    }
}
