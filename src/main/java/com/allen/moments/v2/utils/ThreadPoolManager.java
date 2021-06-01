package com.allen.moments.v2.utils;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


public class ThreadPoolManager<T> {


    private static final int CPU_CORE_COUNT         = Runtime.getRuntime().availableProcessors();

    private static final int CORE_POOL_SIZE    = CPU_CORE_COUNT + 1;

    private static final int MAXIMUM_POOL_SIZE = CPU_CORE_COUNT * 2 + 1;

    private static final int KEEP_ALIVE_TIME = 1;

    private ThreadPoolExecutor executor;

    /**
     * use singleton with lazy initialization
     */
    private ThreadPoolManager() {
    }

    private static ThreadPoolManager<?> instance;

    public synchronized static ThreadPoolManager<?> getInstance() {
        if (instance == null) {
            instance = new ThreadPoolManager<>();
        }
        return instance;
    }

    /**
     * uses a thread to execute the task that has no value returned
     * @param runnable the task
     */
    public void execute(Runnable runnable) {
        if (executor == null) {
            executor = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE,
                    KEEP_ALIVE_TIME, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(20),
                    Executors.defaultThreadFactory(), new ThreadPoolExecutor.AbortPolicy());
        }
        executor.execute(runnable);
    }

    /**
     * uses a thread to execute the task that has value returned
     * @param runnable
     * @return
     */
    public Future<T> submit(Callable<T> runnable) {
        if (executor == null) {
            executor = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE,
                    KEEP_ALIVE_TIME, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(20),
                    Executors.defaultThreadFactory(), new ThreadPoolExecutor.AbortPolicy());
        }
        return executor.submit(runnable);
    }

    /**
     * remove task from queue
     * @param runnable
     */
    public void cancel(Runnable runnable) {
        if (runnable != null) {
            executor.getQueue().remove(runnable);
        }
    }

}
