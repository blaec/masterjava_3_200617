package ru.javaops.multithread.e_syncs;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class E_ReadWriteLock {
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
            Map<String, String> map = new HashMap<>();
            ReadWriteLock lock = new ReentrantReadWriteLock();

            executor.submit(() -> {
                lock.writeLock().lock();
                try {
                    sleep(1);
                    map.put("foo", "bar");
                } finally {
                    lock.writeLock().unlock();
                }
            });

            Runnable readTask = () -> {
                lock.readLock().lock();
                try {
                    System.out.println(map.get("foo"));
                    sleep(1);
                } finally {
                    lock.readLock().unlock();
                }
            };

            executor.submit(readTask);
            executor.submit(readTask);

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
