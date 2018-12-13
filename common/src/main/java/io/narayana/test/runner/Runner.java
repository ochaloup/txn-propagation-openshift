package io.narayana.test.runner;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public class Runner {
    private AtomicReference<ExecutorService> defaultThreadPool;
    private final Map<String, ExecutorService> threadPools = new HashMap<>();

    public void run(Runnable task) {
        getDefaultThreadPool().submit(task);
    }

    public void runAt(Runnable task, String threadPoolName) {
        getThreadPool(threadPoolName).submit(task);
    }

    public <V> Future<V> runAndGet(Callable<V> task) {
        return getDefaultThreadPool().submit(task);
    }

    /**
     * Waiting forever to finish all threads in thread pool.
     */
    public void awaitTermination(String threadPoolName) {
        awaitTermination(threadPoolName, Long.MAX_VALUE);
    }

    public void awaitTermination(String threadPoolName, long timeoutSeconds) {
        ExecutorService threadPool = getThreadPool(threadPoolName);
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

    private ExecutorService getDefaultThreadPool() {
        if(defaultThreadPool == null) {
            defaultThreadPool.compareAndSet(null, Executors.newCachedThreadPool());
        }
        return defaultThreadPool.get();
    }

    private ExecutorService getThreadPool(String threadPoolName) {
        synchronized(threadPools) {
            if(!threadPools.containsKey(threadPoolName)) {
                threadPools.put(threadPoolName, Executors.newCachedThreadPool());
            }
        }
        return threadPools.get(threadPoolName);
    }
}
