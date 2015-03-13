package com.ray.pool;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.mina.filter.executor.OrderedThreadPoolExecutor.Worker;

public class FireThreadPoolCommon extends ThreadPoolExecutor {
	private final AtomicInteger idleWorkers = new AtomicInteger();
	
	public FireThreadPoolCommon(int coreThreads, int maxThreads, 
			long keepAliveTime, String subname, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory) {
		super(coreThreads, maxThreads,
				keepAliveTime, TimeUnit.MILLISECONDS,
                workQueue==null ? new LinkedBlockingQueue<Runnable>() : workQueue,
                threadFactory);
	}
	
	private void addWorker() {
        synchronized (workers) {
            if (workers.size() >= super.getMaximumPoolSize()) {
                return;
            }

            // Create a new worker, and add it to the thread pool
            Worker worker = new Worker();
            Thread thread = getThreadFactory().newThread(worker);

            // As we have added a new thread, it's considered as idle.
            idleWorkers.incrementAndGet();

            // Now, we can start it.
            thread.start();
            workers.add(worker);

            if (workers.size() > largestPoolSize) {
                largestPoolSize = workers.size();
            }
        }
    }

    /**
     * Add a new Worker only if there are no idle worker.
     */
    private void addWorkerIfNecessary() {
        if (idleWorkers.get() == 0) {
            synchronized (workers) {
                if (workers.isEmpty() || (idleWorkers.get() == 0)) {
                    addWorker();
                }
            }
        }
    }

    private void removeWorker() {
        synchronized (workers) {
            if (workers.size() <= super.getCorePoolSize()) {
                return;
            }
            waitingSessions.offer(EXIT_SIGNAL);
        }
    }
}
