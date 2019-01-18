package io.narayana.test.runner;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public final class Runner {
    private static AtomicReference<ExecutorService> defaultThreadPool;
    private static final Map<String, ExecutorService> threadPools = new HashMap<>();

    private Runner() {
        // utility class
    }

    public static void run(Runnable task) {
        getDefaultThreadPool().submit(task);
    }

    public static void runAt(Runnable task, String threadPoolName) {
        getThreadPool(threadPoolName).submit(task);
    }

    public static <V> Future<V> runAndGet(Callable<V> task) {
        return getDefaultThreadPool().submit(task);
    }

    public static <V> Future<V> runAndGetAt(Callable<V> task, String threadPoolName) {
        return getThreadPool(threadPoolName).submit(task);
    }

    /**
     * Waiting forever to finish all threads in thread pool.
     */
    public static void awaitTermination(String threadPoolName) {
        awaitTermination(getThreadPool(threadPoolName), Long.MAX_VALUE);
    }

    public static void awaitTermination() {
        awaitTermination(getDefaultThreadPool(), Long.MAX_VALUE);
    }

    public static void awaitTermination(long timeoutSeconds) {
        awaitTermination(getDefaultThreadPool(), timeoutSeconds);
    }

    public static void awaitTermination(ExecutorService threadPool, long timeoutSeconds) {
        threadPool.shutdown();
        try {
            if (!threadPool.awaitTermination(timeoutSeconds, TimeUnit.SECONDS)) {
                threadPool.shutdownNow();
            }
        } catch (InterruptedException ie) {
            threadPool.shutdownNow();
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Awaiting for termination of the thread pool '" + threadPool + "' was interupted", ie);
        }
    }

    private static ExecutorService getDefaultThreadPool() {
        if(defaultThreadPool == null) {
            defaultThreadPool.compareAndSet(null, Executors.newCachedThreadPool());
        }
        return defaultThreadPool.get();
    }

    private static ExecutorService getThreadPool(String threadPoolName) {
        synchronized(threadPools) {
            if(!threadPools.containsKey(threadPoolName)) {
                threadPools.put(threadPoolName, Executors.newCachedThreadPool());
            }
        }
        return threadPools.get(threadPoolName);
    }
}
