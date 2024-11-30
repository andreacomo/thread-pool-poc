package it.codingjam.threadpools;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;
import java.util.stream.IntStream;

public class LauncherWithAbort {

    private static final Logger LOGGER = Logger.getLogger(LauncherWithAbort.class.getName());

    public static void main(String[] args) throws InterruptedException {
        int max = 10;
        CountDownLatch latch = new CountDownLatch(max);
        AtomicInteger executed = new AtomicInteger(0);
        try(ThreadPoolExecutor executor = new ThreadPoolExecutor(
                2,
                4,
                10,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(1)
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
}
