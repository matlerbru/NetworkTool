package NetworkTool;

import java.util.concurrent.Semaphore;

public class QueueSemaphore {

    public QueueSemaphore(int limit) {
        semaphore = new Semaphore(limit);
    }

    private Semaphore semaphore;

    public boolean tryLogin() {
        return semaphore.tryAcquire();
    }

    public void logout() {
        semaphore.release();
    }

    public int availableSlots() {
        return semaphore.availablePermits();
    }

    public void setLimit(int limit) {
        semaphore = new Semaphore(limit);
    }







}
