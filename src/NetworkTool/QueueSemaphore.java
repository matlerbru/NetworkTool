package NetworkTool;

import java.util.concurrent.Semaphore;

public class QueueSemaphore {

    public QueueSemaphore(int limit) {
        semaphore = new Semaphore(limit);
    }

    private Semaphore semaphore;

    boolean tryLogin() {
        return semaphore.tryAcquire();
    }

    void logout() {
        semaphore.release();
    }

    int availableSlots() {
        return semaphore.availablePermits();
    }







}
