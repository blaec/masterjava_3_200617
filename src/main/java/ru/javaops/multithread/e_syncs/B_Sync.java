package ru.javaops.multithread.e_syncs;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

public class B_Sync {
    public static void main(String[] args) {
        Synced synced = new Synced();
        synced.run();
    }

    private static class Synced {
        int count = 0;

        public Synced() {
        }

        void run() {
            ExecutorService executor = Executors.newFixedThreadPool(2);

            IntStream.range(0, 10000)
                    .forEach(i -> executor.submit(this::increment));

            stop(executor);

            System.out.println(count);  // 9965
        }

        void increment() {
            synchronized (this) {
                count = count + 1;
            }
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
    }
}
