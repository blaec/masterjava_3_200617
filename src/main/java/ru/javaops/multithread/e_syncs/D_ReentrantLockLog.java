package ru.javaops.multithread.e_syncs;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

public class D_ReentrantLockLog {
    public static void main(String[] args) {
        RLock rLock = new RLock();
        rLock.run();
    }

    private static class RLock {
        public RLock() {
        }

        private void stop(ExecutorService executor) {
            try {
                System.out.println("attempt to shutdown executor");
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
                System.out.println("shutdown finished");
            }
        }

        void run() {
            ExecutorService executor = Executors.newFixedThreadPool(2);
            ReentrantLock lock = new ReentrantLock();

            executor.submit(() -> {
                lock.lock();
                try {
                    sleep(1);
                } finally {
                    lock.unlock();
                }
            });

            executor.submit(() -> {
                System.out.println("Locked: " + lock.isLocked());
                System.out.println("Held by me: " + lock.isHeldByCurrentThread());
                boolean locked = lock.tryLock();
                System.out.println("Lock acquired: " + locked);
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
    }
}
