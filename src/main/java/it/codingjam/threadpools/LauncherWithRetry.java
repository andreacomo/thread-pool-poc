package it.codingjam.threadpools;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;
import java.util.stream.IntStream;

/**
 * With a custom rejection policy, we retry to enqueue for max 3 seconds every rejected tasks:
 * as result, all tasks (10) are executed because they last 1 second each
 */
public class LauncherWithRetry {

    private static final Logger LOGGER = Logger.getLogger(LauncherWithRetry.class.getName());

    public static void main(String[] args) throws InterruptedException {
        int max = 10;
        CountDownLatch latch = new CountDownLatch(max);
        AtomicInteger executed = new AtomicInteger(0);
        try(ThreadPoolExecutor executor = new ThreadPoolExecutor(
                2,
                4,
                10,
                TimeUnit.SECONDS,
                new SynchronousQueue<>(true), // works with LinkedBlockingQueue<>(1) as well
                new RetryPolicy(3, TimeUnit.SECONDS)
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

    private static class RetryPolicy implements RejectedExecutionHandler {

        private final int timeout;
        private final TimeUnit timeoutUnit;

        private RetryPolicy(int timeout, TimeUnit timeoutUnit) {
            this.timeout = timeout;
            this.timeoutUnit = timeoutUnit;
        }

        @Override
        public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
            try {
                BlockingQueue<Runnable> queue = executor.getQueue();
                var startWaiting = System.currentTimeMillis();
                if (!queue.offer(r, this.timeout, this.timeoutUnit)) {
                    throw new RejectedExecutionException("Can't enqueue");
                }
                LOGGER.info("Rejection risked! Waited " + (System.currentTimeMillis() - startWaiting));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
