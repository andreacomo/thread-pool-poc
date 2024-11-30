package it.codingjam.threadpools;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;
import java.util.stream.IntStream;

public class LauncherWithBackPressure {

    private static final Logger LOGGER = Logger.getLogger(LauncherWithBackPressure.class.getName());

    public static void main(String[] args) throws InterruptedException {
        int max = 10;
        CountDownLatch latch = new CountDownLatch(max);
        AtomicInteger executed = new AtomicInteger(0);
        try(ThreadPoolExecutor executor = new ThreadPoolExecutor(
                2,
                4,
                10,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(1),
                new BackPressurePolicy(3, TimeUnit.SECONDS)
        )) {
            IntStream.range(0, max).forEach(i -> {
                try {
                    executor.submit(() -> {
                        try {
                            LOGGER.info("Starting " + i);
                            Thread.sleep(1000);
                            LOGGER.info("Finishing " + i);
                            latch.countDown();
                            executed.incrementAndGet();
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                    });
                } catch (RejectedExecutionException e) {
                    LOGGER.severe("Task with id " + i + " REJECTED");
                    latch.countDown();
                }
            });
        } finally {
            latch.await();
            LOGGER.info("Executed tasks: " + executed.get());
        }
    }

    private static class BackPressurePolicy implements RejectedExecutionHandler {

        private final int timeout;
        private final TimeUnit timeoutUnit;

        private BackPressurePolicy(int timeout, TimeUnit timeoutUnit) {
            this.timeout = timeout;
            this.timeoutUnit = timeoutUnit;
        }

        @Override
        public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
            try {
                BlockingQueue<Runnable> queue = executor.getQueue();
                if (!queue.offer(r, this.timeout, this.timeoutUnit)) {
                    throw new RejectedExecutionException("Can't enqueue");
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
