package ru.javaops.multithread.e_syncs;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.StampedLock;

public class G_StampedLockOptLock {
    public static void main(String[] args) {
        RLock rLock = new RLock();
        rLock.run();
    }

    private static class RLock {
        public RLock() {
        }

        void run() {
            ExecutorService executor = Executors.newFixedThreadPool(2);
            StampedLock lock = new StampedLock();

            executor.submit(() -> {
                long stamp = lock.tryOptimisticRead();
                try {
                    System.out.println("Optimistic Lock Valid: " + lock.validate(stamp));
                    sleep(1);
                    System.out.println("Optimistic Lock Valid: " + lock.validate(stamp));
                    sleep(2);
                    System.out.println("Optimistic Lock Valid: " + lock.validate(stamp));
                } finally {
                    lock.unlock(stamp);
                }
            });

            executor.submit(() -> {
                long stamp = lock.writeLock();
                try {
                    System.out.println("Write Lock acquired");
                    sleep(2);
                } finally {
                    lock.unlock(stamp);
                    System.out.println("Write done");
                }
            });

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
