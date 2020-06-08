package ru.javaops.multithread.e_syncs;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.StampedLock;

public class H_StampedLockConvToWrite {
    public static void main(String[] args) {
        RLock rLock = new RLock();
        rLock.run();
    }

    private static class RLock {
        public RLock() {
        }
        int count;

        void run() {
            ExecutorService executor = Executors.newFixedThreadPool(2);
            StampedLock lock = new StampedLock();

            executor.submit(() -> {
                long stamp = lock.readLock();
                try {
                    if (count == 0) {
                        stamp = lock.tryConvertToWriteLock(stamp);
                        if (stamp == 0L) {
                            System.out.println("Could not convert to write lock");
                            stamp = lock.writeLock();
                        }
                        count = 23;
                    }
                    System.out.println(count);
                } finally {
                    lock.unlock(stamp);
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
