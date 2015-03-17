package com.ray.pool;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class FireThreadPoolCommon extends ThreadPoolExecutor {
	private final Set<Worker> workers = new HashSet<Worker>();
	private final AtomicInteger idleWorkers = new AtomicInteger();
	private volatile int largestPoolSize;
	private long completedTaskCount;
    private volatile boolean shutdown;
    private static final Runnable empty_task = new Runnable(){
    	public void run(){}
    };
	
	public FireThreadPoolCommon(int coreThreads, int maxThreads, 
			long keepAliveTime, String subname, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory) {
		super(coreThreads, maxThreads,
				keepAliveTime, TimeUnit.MILLISECONDS,
                workQueue==null ? new LinkedBlockingQueue<Runnable>() : workQueue,
                threadFactory);
		setRejectedExecutionHandler(new FireRejectedExecutionHandler());
	}
	
	@Override
    public boolean prestartCoreThread() {
        synchronized (workers) {
            if (workers.size() < super.getCorePoolSize()) {
                addWorker();
                return true;
            } else {
                return false;
            }
        }
    }
	
	@Override
	public void execute(Runnable task) {
		if (shutdown) {
            rejectTask(task);
            return;
        }
		if(!getQueue().offer(task)){
			rejectTask(task);
			return;
		}
//		Log.info("queue.size: " + getQueue().size());
		addWorkerIfNecessary();
    }
	
	private void rejectTask(Runnable task) {
		if(getRejectedExecutionHandler() == null){
			throw new RuntimeException("getRejectedExecutionHandler() is null.");
		}
        getRejectedExecutionHandler().rejectedExecution(task, this);
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
            getQueue().offer(empty_task);
        }
    }
    
    @Override
    public void setMaximumPoolSize(int maximumPoolSize) {
        if ((maximumPoolSize <= 0) || (maximumPoolSize < super.getCorePoolSize())) {
            throw new IllegalArgumentException("maximumPoolSize: " + maximumPoolSize);
        }

        synchronized (workers) {
            super.setMaximumPoolSize(maximumPoolSize);
            int difference = workers.size() - maximumPoolSize;
            while (difference > 0) {
                removeWorker();
                --difference;
            }
        }
    }
    
    public long getCompletedTaskCount() {
		return completedTaskCount;
	}

	public Set<Worker> getWorkers() {
		return workers;
	}

	/**
     * {@inheritDoc}
     */
    @Override
    public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {

        long deadline = System.currentTimeMillis() + unit.toMillis(timeout);

        synchronized (workers) {
            while (!isTerminated()) {
                long waitTime = deadline - System.currentTimeMillis();
                if (waitTime <= 0) {
                    break;
                }

                workers.wait(waitTime);
            }
        }
        return isTerminated();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isShutdown() {
        return shutdown;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isTerminated() {
        if (!shutdown) {
            return false;
        }

        synchronized (workers) {
            return workers.isEmpty();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void shutdown() {
        if (shutdown) {
            return;
        }

        shutdown = true;

        synchronized (workers) {
            for (int i = workers.size(); i > 0; i--) {
            	getQueue().offer(empty_task);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Runnable> shutdownNow() {
        shutdown();

        List<Runnable> answer = new ArrayList<Runnable>();

        for (Runnable task : getQueue()) {
        	if(task != empty_task){
        		answer.add(task);
        	}
        }
        getQueue().clear();

        synchronized (workers) {
            for (int i = workers.size(); i > 0; i--) {
            	getQueue().offer(empty_task);
            }
        	
        }

        return answer;
    }
    
    private class Worker implements Runnable {

        private volatile long completedTaskCount;

        private Thread thread;

        public void run() {
            thread = Thread.currentThread();

            try {
                for (;;) {
                    idleWorkers.decrementAndGet();

                    Runnable task = getTask();
                    if (task == null) {
                        synchronized (workers) {
                            if (workers.size() > getCorePoolSize()) {
                                // Remove now to prevent duplicate exit.
                                workers.remove(this);
                                break;
                            }
                        }
                    }

                    if (task == empty_task) {
                        break;
                    }

                    try {
                    	if(task != null){
                    		runTask(task);
                    	}
                    } finally {
                        idleWorkers.incrementAndGet();
                    }
                }
            } finally {
                synchronized (workers) {
                    workers.remove(this);
                    FireThreadPoolCommon.this.completedTaskCount += completedTaskCount;
                    workers.notifyAll();
                }
            }
        }
        
        private Runnable getTask() {
        	Runnable r = null;
        	long currentTime = System.currentTimeMillis();//.nanoTime();
        	long deadline = currentTime + getKeepAliveTime(TimeUnit.MILLISECONDS);
        
            for (;;) {
                try {
                    if (isShutdown())
                        return null;
                    long waitTime = deadline - currentTime;
                    if (waitTime <= 0) {
                        break;
                    }
                    try{
                    	r = getQueue().poll(waitTime, TimeUnit.MILLISECONDS);
                    	break;
                    }finally{
                    	if(r == null){
                    		currentTime = System.currentTimeMillis();
                    	}
                    }
                    // Else retry
                } catch (InterruptedException ie) {
                    // On interruption, re-check runState
                }
            }
            return r;
        }

        private void runTask(Runnable task) {
            beforeExecute(thread, task);
            boolean ran = false;
            try {
                task.run();
                ran = true;
                afterExecute(task, null);
                completedTaskCount++;
            } catch (RuntimeException e) {
                if (!ran) {
                    afterExecute(task, e);
                }
                throw e;
            }
        }
    }
    
    public static class FireRejectedExecutionHandler implements RejectedExecutionHandler {
        private List<Runnable> waitingQueue = new LinkedList<Runnable>();
        
        public FireRejectedExecutionHandler() { }

        public void rejectedExecution(Runnable r, ThreadPoolExecutor e) {
        	synchronized(waitingQueue){
        		waitingQueue.add(r);
        	}
        }

		public List<Runnable> getWaitingQueue() {
			return waitingQueue;
		}
		public void setWaitingQueue(List<Runnable> waitingQueue) {
			this.waitingQueue = waitingQueue;
		}
    }
}
