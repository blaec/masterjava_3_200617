package ru.javaops.multithread.e_syncs;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.StampedLock;
import java.util.stream.IntStream;

public class I_Semaphore {
    public static void main(String[] args) {
        RLock rLock = new RLock();
        rLock.run();
    }

    private static class RLock {
        public RLock() {
        }
        int count;

        void run() {
            ExecutorService executor = Executors.newFixedThreadPool(10);

            Semaphore semaphore = new Semaphore(5);

            Runnable longRunningTask = () -> {
                boolean permit = false;
                try {
                    permit = semaphore.tryAcquire(1, TimeUnit.SECONDS);
                    if (permit) {
                        System.out.println("Semaphore acquired");
                        sleep(5);
                    } else {
                        System.out.println("Could not acquire semaphore");
                    }
                } catch (InterruptedException e) {
                    throw new IllegalStateException(e);
                } finally {
                    if (permit) {
                        semaphore.release();
                    }
                }
            };

            IntStream.range(0, 10)
                    .forEach(i -> executor.submit(longRunningTask));

            stop(executor);
        }

        private void sleep(int i) {
            try {
                TimeUnit.SECONDS.sleep(i);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        private void stop(ExecutorService executor) {
            try {
//                System.out.println("attempt to shutdown executor");
                executor.shutdown();
                executor.awaitTermination(5, TimeUnit.SECONDS);
            }
            catch (InterruptedException e) {
                System.err.println("tasks interrupted");
            }
            finally {
                if (!executor.isTerminated()) {
                    System.err.println("cancel non-finished tasks");
                }
                executor.shutdownNow();
//                System.out.println("shutdown finished");
            }
        }
    }
}
