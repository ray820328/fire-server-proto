package com.ray.server.logic;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.mina.core.session.AttributeKey;
import org.apache.mina.core.session.IoEvent;
import org.apache.mina.core.session.IoEventType;
import org.apache.mina.core.write.WriteRequest;
import org.apache.mina.filter.executor.IoEventQueueHandler;
import org.apache.mina.filter.executor.UnorderedThreadPoolExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ray.communicate.message.IoHeader;
import com.ray.communicate.message.IoMessage;
import com.ray.communicate.server.bean.IoConnection;
import com.ray.utils.util.Log;

/**
 * A {@link ThreadPoolExecutor} that maintains the order of {@link IoEvent}s.
 * <p>
 * If you don't need to maintain the order of events per ioConnection, please use
 * {@link UnorderedThreadPoolExecutor}.

 * @author <a href="http://mina.apache.org">Apache MINA Project</a>
 * @org.apache.xbean.XBean
 */
public class UserOrderedThreadPoolExecutor extends ThreadPoolExecutor {
    /** A logger for this class (commented as it breaks MDCFlter tests) */
    static Logger LOGGER = LoggerFactory.getLogger(UserOrderedThreadPoolExecutor.class);

    /** A default value for the initial pool size */
    private static final int DEFAULT_INITIAL_THREAD_POOL_SIZE = 0;

    /** A default value for the maximum pool size */
    private static final int DEFAULT_MAX_THREAD_POOL = 16;

    /** A default value for the KeepAlive delay */
    private static final int DEFAULT_KEEP_ALIVE = 30;

    private static final IoConnection EXIT_SIGNAL = new IoConnection(0, null);
    /** 无法标记实际连接的数据 **/
    private static final IoConnection full_connection = new IoConnection(0, null);//实际参数session在event里面

    /** A key stored into the ioConnection's attribute for the event tasks being queued */
    private final AttributeKey TASKS_QUEUE = new AttributeKey(getClass(), "tasksQueue");

    /** A queue used to store the available ioConnections */
    private final BlockingQueue<IoConnection> waitingUsers = new LinkedBlockingQueue<IoConnection>();

    private final Set<Worker> workers = new HashSet<Worker>();

    private volatile int largestPoolSize;

    private final AtomicInteger idleWorkers = new AtomicInteger();

    private long completedTaskCount;

    private volatile boolean shutdown;

    private final IoEventQueueHandler eventQueueHandler;

    /**
     * Creates a default ThreadPool, with default values :
     * - minimum pool size is 0
     * - maximum pool size is 16
     * - keepAlive set to 30 seconds
     * - A default ThreadFactory
     * - All events are accepted
     */
    public UserOrderedThreadPoolExecutor() {
        this(DEFAULT_INITIAL_THREAD_POOL_SIZE, DEFAULT_MAX_THREAD_POOL, DEFAULT_KEEP_ALIVE, TimeUnit.SECONDS, Executors
                .defaultThreadFactory(), null);
    }

    /**
     * Creates a default ThreadPool, with default values :
     * - minimum pool size is 0
     * - keepAlive set to 30 seconds
     * - A default ThreadFactory
     * - All events are accepted
     * 
     * @param maximumPoolSize The maximum pool size
     */
    public UserOrderedThreadPoolExecutor(int maximumPoolSize) {
        this(DEFAULT_INITIAL_THREAD_POOL_SIZE, maximumPoolSize, DEFAULT_KEEP_ALIVE, TimeUnit.SECONDS, Executors
                .defaultThreadFactory(), null);
    }

    /**
     * Creates a default ThreadPool, with default values :
     * - keepAlive set to 30 seconds
     * - A default ThreadFactory
     * - All events are accepted
     *
     * @param corePoolSize The initial pool sizePoolSize
     * @param maximumPoolSize The maximum pool size
     */
    public UserOrderedThreadPoolExecutor(int corePoolSize, int maximumPoolSize) {
        this(corePoolSize, maximumPoolSize, DEFAULT_KEEP_ALIVE, TimeUnit.SECONDS, Executors.defaultThreadFactory(),
                null);
    }

    /**
     * Creates a default ThreadPool, with default values :
     * - A default ThreadFactory
     * - All events are accepted
     * 
     * @param corePoolSize The initial pool sizePoolSize
     * @param maximumPoolSize The maximum pool size
     * @param keepAliveTime Default duration for a thread
     * @param unit Time unit used for the keepAlive value
     */
    public UserOrderedThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit) {
        this(corePoolSize, maximumPoolSize, keepAliveTime, unit, Executors.defaultThreadFactory(), null);
    }

    /**
     * Creates a default ThreadPool, with default values :
     * - A default ThreadFactory
     * 
     * @param corePoolSize The initial pool sizePoolSize
     * @param maximumPoolSize The maximum pool size
     * @param keepAliveTime Default duration for a thread
     * @param unit Time unit used for the keepAlive value
     * @param eventQueueHandler The queue used to store events
     */
    public UserOrderedThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit,
            IoEventQueueHandler eventQueueHandler) {
        this(corePoolSize, maximumPoolSize, keepAliveTime, unit, Executors.defaultThreadFactory(), eventQueueHandler);
    }

    /**
     * Creates a default ThreadPool, with default values :
     * - A default ThreadFactory
     * 
     * @param corePoolSize The initial pool sizePoolSize
     * @param maximumPoolSize The maximum pool size
     * @param keepAliveTime Default duration for a thread
     * @param unit Time unit used for the keepAlive value
     * @param threadFactory The factory used to create threads
     */
    public UserOrderedThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit,
            ThreadFactory threadFactory) {
        this(corePoolSize, maximumPoolSize, keepAliveTime, unit, threadFactory, null);
    }

    /**
     * Creates a new instance of a UserOrderedThreadPoolExecutor.
     * 
     * @param corePoolSize The initial pool sizePoolSize
     * @param maximumPoolSize The maximum pool size
     * @param keepAliveTime Default duration for a thread
     * @param unit Time unit used for the keepAlive value
     * @param threadFactory The factory used to create threads
     * @param eventQueueHandler The queue used to store events
     */
    public UserOrderedThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit,
            ThreadFactory threadFactory, IoEventQueueHandler eventQueueHandler) {
        // We have to initialize the pool with default values (0 and 1) in order to
        // handle the exception in a better way. We can't add a try {} catch() {}
        // around the super() call.
        super(DEFAULT_INITIAL_THREAD_POOL_SIZE, 1, keepAliveTime, unit, new SynchronousQueue<Runnable>(),
                threadFactory, new AbortPolicy());

        if (corePoolSize < DEFAULT_INITIAL_THREAD_POOL_SIZE) {
            throw new IllegalArgumentException("corePoolSize: " + corePoolSize);
        }

        if ((maximumPoolSize == 0) || (maximumPoolSize < corePoolSize)) {
            throw new IllegalArgumentException("maximumPoolSize: " + maximumPoolSize);
        }

        // Now, we can setup the pool sizes
        super.setCorePoolSize(corePoolSize);
        super.setMaximumPoolSize(maximumPoolSize);

        // The queueHandler might be null.
        if (eventQueueHandler == null) {
            this.eventQueueHandler = IoEventQueueHandler.NOOP;
        } else {
            this.eventQueueHandler = eventQueueHandler;
        }
    }

    /**
     * Get the ioConnection's tasks queue.
     */
    private IoConnectionTasksQueue getIoConnectionTasksQueue(IoConnection ioConnection) {
        IoConnectionTasksQueue queue = (IoConnectionTasksQueue) ioConnection.getAttribute(TASKS_QUEUE);

        if (queue == null) {
            queue = new IoConnectionTasksQueue();
            IoConnectionTasksQueue oldQueue = (IoConnectionTasksQueue) ioConnection.setAttributeIfAbsent(TASKS_QUEUE, queue);

            if (oldQueue != null) {
                queue = oldQueue;
            }
        }

        return queue;
    }

    /**
     * @return The associated queue handler. 
     */
    public IoEventQueueHandler getQueueHandler() {
        return eventQueueHandler;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setRejectedExecutionHandler(RejectedExecutionHandler handler) {
        // Ignore the request.  It must always be AbortPolicy.
    }

    /**
     * Add a new thread to execute a task, if needed and possible.
     * It depends on the current pool size. If it's full, we do nothing.
     */
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
            waitingUsers.offer(EXIT_SIGNAL);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getMaximumPoolSize() {
        return super.getMaximumPoolSize();
    }

    /**
     * {@inheritDoc}
     */
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
                waitingUsers.offer(EXIT_SIGNAL);
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
        IoConnection ioConnection;

        while ((ioConnection = waitingUsers.poll()) != null) {
            if (ioConnection == EXIT_SIGNAL) {
                waitingUsers.offer(EXIT_SIGNAL);
                Thread.yield(); // Let others take the signal.
                continue;
            }

            IoConnectionTasksQueue ioIoConnectionTasksQueue = (IoConnectionTasksQueue) ioConnection.getAttribute(TASKS_QUEUE);

            synchronized (ioIoConnectionTasksQueue.tasksQueue) {

                for (Runnable task : ioIoConnectionTasksQueue.tasksQueue) {
                    getQueueHandler().polled(this, (IoEvent) task);
                    answer.add(task);
                }

                ioIoConnectionTasksQueue.tasksQueue.clear();
            }
        }

        return answer;
    }

    /**
     * A Helper class used to print the list of events being queued. 
     */
    private void print(Queue<Runnable> queue, IoEvent event) {
        StringBuilder sb = new StringBuilder();
        sb.append("Adding event ").append(event.getType()).append(" to ioConnection ").append(event.getSession().getId());
        boolean first = true;
        sb.append("\nQueue : [");
        for (Runnable elem : queue) {
            if (first) {
                first = false;
            } else {
                sb.append(", ");
            }

            sb.append(((IoEvent) elem).getType()).append(", ");
        }
        sb.append("]\n");
        LOGGER.debug(sb.toString());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute(Runnable task) {
        if (shutdown) {
            rejectTask(task);
        }

        // Check that it's a IoEvent task
        checkTaskType(task);

        IoEvent event = (IoEvent) task;

//      如果有中转，将接收和发送的消息按实际连接提交先后线程同步顺序执行，其余无法辨别实际连接的统一按提交顺序执行
        IoHeader header = null;
        if(event.getType() == IoEventType.MESSAGE_RECEIVED){
        	IoMessage message = (IoMessage)event.getParameter();
    		header = message.getHeader();
//    		Log.info("UserOrderedThreadPool.received: " + header.toString());
        }else if(event.getType()==IoEventType.WRITE || event.getType()==IoEventType.MESSAGE_SENT){
        	WriteRequest writeRequest = (WriteRequest)event.getParameter();
        	IoMessage message = (IoMessage)writeRequest.getMessage();
        	header = message.getHeader();
//        	Log.info("UserOrderedThreadPool.sending: " + header.toString());
        }
        // Get the associated ioConnection
        IoConnection ioConnection = null;
        if(header != null){
        	ioConnection = FireNioHandler.instance.getIoConnection(header.getSid(), event.getSession());
        }
        ioConnection = ioConnection==null ? full_connection : ioConnection;

        // Get the ioConnection's queue of events
        IoConnectionTasksQueue ioIoConnectionTasksQueue = getIoConnectionTasksQueue(ioConnection);
        Queue<Runnable> tasksQueue = ioIoConnectionTasksQueue.tasksQueue;

        boolean offerSession;

        // propose the new event to the event queue handler. If we
        // use a throttle queue handler, the message may be rejected
        // if the maximum size has been reached.
        boolean offerEvent = eventQueueHandler.accept(this, event);

        if (offerEvent) {
            // Ok, the message has been accepted
            synchronized (tasksQueue) {
                // Inject the event into the executor taskQueue
                tasksQueue.offer(event);

                if (ioIoConnectionTasksQueue.processingCompleted) {
                    ioIoConnectionTasksQueue.processingCompleted = false;
                    offerSession = true;
                } else {
                    offerSession = false;
                }

                if (LOGGER.isDebugEnabled()) {
                    print(tasksQueue, event);
                }
            }
        } else {
            offerSession = false;
        }

        if (offerSession) {
            // As the tasksQueue was empty, the task has been executed
            // immediately, so we can move the ioConnection to the queue
            // of ioConnections waiting for completion.
            waitingUsers.offer(ioConnection);
        }

        addWorkerIfNecessary();

        if (offerEvent) {
            eventQueueHandler.offered(this, event);
        }
    }

    private void rejectTask(Runnable task) {
        getRejectedExecutionHandler().rejectedExecution(task, this);
    }

    private void checkTaskType(Runnable task) {
        if (!(task instanceof IoEvent)) {
            throw new IllegalArgumentException("task must be an IoEvent or its subclass.");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getActiveCount() {
        synchronized (workers) {
            return workers.size() - idleWorkers.get();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getCompletedTaskCount() {
        synchronized (workers) {
            long answer = completedTaskCount;
            for (Worker w : workers) {
                answer += w.completedTaskCount;
            }

            return answer;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getLargestPoolSize() {
        return largestPoolSize;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getPoolSize() {
        synchronized (workers) {
            return workers.size();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getTaskCount() {
        return getCompletedTaskCount();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isTerminating() {
        synchronized (workers) {
            return isShutdown() && !isTerminated();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int prestartAllCoreThreads() {
        int answer = 0;
        synchronized (workers) {
            for (int i = super.getCorePoolSize() - workers.size(); i > 0; i--) {
                addWorker();
                answer++;
            }
        }
        return answer;
    }

    /**
     * {@inheritDoc}
     */
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

    /**
     * {@inheritDoc}
     */
    @Override
    public BlockingQueue<Runnable> getQueue() {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void purge() {
        // Nothing to purge in this implementation.
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean remove(Runnable task) {
        checkTaskType(task);
        IoEvent event = (IoEvent) task;
        
        IoHeader header = null;
        if(event.getType() == IoEventType.MESSAGE_RECEIVED){
        	IoMessage message = (IoMessage)event.getParameter();
    		header = message.getHeader();
        }else if(event.getType()==IoEventType.WRITE || event.getType()==IoEventType.MESSAGE_SENT){
        	WriteRequest writeRequest = (WriteRequest)event.getParameter();
        	IoMessage message = (IoMessage)writeRequest.getMessage();
        	header = message.getHeader();
        }
        // Get the associated ioConnection
        IoConnection ioConnection = full_connection;
        if(header != null){
        	ioConnection = FireNioHandler.instance.getConnections().get(header.getSid());
        }
        
        IoConnectionTasksQueue ioIoConnectionTasksQueue = (IoConnectionTasksQueue) ioConnection.getAttribute(TASKS_QUEUE);
        Queue<Runnable> tasksQueue = ioIoConnectionTasksQueue.tasksQueue;

        if (ioIoConnectionTasksQueue == null) {
            return false;
        }

        boolean removed;

        synchronized (tasksQueue) {
            removed = tasksQueue.remove(task);
        }

        if (removed) {
            getQueueHandler().polled(this, event);
        }

        return removed;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getCorePoolSize() {
        return super.getCorePoolSize();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setCorePoolSize(int corePoolSize) {
        if (corePoolSize < 0) {
            throw new IllegalArgumentException("corePoolSize: " + corePoolSize);
        }
        if (corePoolSize > super.getMaximumPoolSize()) {
            throw new IllegalArgumentException("corePoolSize exceeds maximumPoolSize");
        }

        synchronized (workers) {
            if (super.getCorePoolSize() > corePoolSize) {
                for (int i = super.getCorePoolSize() - corePoolSize; i > 0; i--) {
                    removeWorker();
                }
            }
            super.setCorePoolSize(corePoolSize);
        }
    }

    private class Worker implements Runnable {

        private volatile long completedTaskCount;

        private Thread thread;

        public void run() {
            thread = Thread.currentThread();

            try {
                for (;;) {
                    IoConnection ioConnection = fetchSession();

                    idleWorkers.decrementAndGet();

                    if (ioConnection == null) {
                        synchronized (workers) {
                            if (workers.size() > getCorePoolSize()) {
                                // Remove now to prevent duplicate exit.
                                workers.remove(this);
                                break;
                            }
                        }
                    }

                    if (ioConnection == EXIT_SIGNAL) {
                        break;
                    }

                    try {
                        if (ioConnection != null) {
                            runTasks(getIoConnectionTasksQueue(ioConnection));
                        }
                    } finally {
                        idleWorkers.incrementAndGet();
                    }
                }
            } finally {
                synchronized (workers) {
                    workers.remove(this);
                    UserOrderedThreadPoolExecutor.this.completedTaskCount += completedTaskCount;
                    workers.notifyAll();
                }
            }
        }

        private IoConnection fetchSession() {
            IoConnection ioConnection = null;
            long currentTime = System.currentTimeMillis();
            long deadline = currentTime + getKeepAliveTime(TimeUnit.MILLISECONDS);
            for (;;) {
                try {
                    long waitTime = deadline - currentTime;
                    if (waitTime <= 0) {
                        break;
                    }

                    try {
                        ioConnection = waitingUsers.poll(waitTime, TimeUnit.MILLISECONDS);
                        break;
                    } finally {
                        if (ioConnection == null) {
                            currentTime = System.currentTimeMillis();
                        }
                    }
                } catch (InterruptedException e) {
                    // Ignore.
                    continue;
                }
            }
            return ioConnection;
        }

        private void runTasks(IoConnectionTasksQueue ioIoConnectionTasksQueue) {
            for (;;) {
                Runnable task;
                Queue<Runnable> tasksQueue = ioIoConnectionTasksQueue.tasksQueue;

                synchronized (tasksQueue) {
                    task = tasksQueue.poll();

                    if (task == null) {
                        ioIoConnectionTasksQueue.processingCompleted = true;
                        break;
                    }
                }

                eventQueueHandler.polled(UserOrderedThreadPoolExecutor.this, (IoEvent) task);

                runTask(task);
            }
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

    /**
     * A class used to store the ordered list of events to be processed by the
     * ioConnection, and the current task state.
     */
    private class IoConnectionTasksQueue {
        /**  A queue of ordered event waiting to be processed */
        private final Queue<Runnable> tasksQueue = new ConcurrentLinkedQueue<Runnable>();

        /** The current task state */
        private boolean processingCompleted = true;
    }
}
