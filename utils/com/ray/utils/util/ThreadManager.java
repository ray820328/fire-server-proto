package com.ray.utils.util;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;


public class ThreadManager {
	private static ThreadManager instance = null;
	private ThreadGroup threadGroup;

	private ThreadManager() {
		if (instance != null) {
			throw new RuntimeException("ThreadManager already inited.");
		}
		instance = this;
		threadGroup = new ThreadGroup("fire");
	}

	public static synchronized ThreadManager getInstance() {
		if (instance == null) {
			new ThreadManager();
		}
		return instance;
	}

	public Thread[] findAllThreads(){
		ThreadGroup group = threadGroup;

		ThreadGroup topGroup = group;
		while (group != null) {
			topGroup = group;
			group = group.getParent();
		}

		int estimatedSize = topGroup.activeCount() * 2;
		Thread[] slackList = new Thread[estimatedSize];

		int actualSize = topGroup.enumerate(slackList);

		Thread[] list = new Thread[actualSize];
		System.arraycopy(slackList, 0, list, 0, actualSize);

		return list;
	}
	
	public void showDetail(){
		Thread[] threads = findAllThreads();
		StringBuilder sb = new StringBuilder();
		int i = 0;
		for(Thread t : threads){
			sb.append("\n").append(i++);
			sb.append(", Priority: ").append(t.getPriority());
			sb.append(", Alive: ").append(t.isAlive());
			sb.append(", Daemon: ").append(t.isDaemon());
			sb.append(", Interrupted: ").append(t.isInterrupted());
			sb.append(", GName: ").append(t.getThreadGroup().getName());
			sb.append(", Name:").append(t.getName());
		}
		Log.info(sb.toString());
	}

	public ThreadGroup getThreadGroup() {
		return threadGroup;
	}

	/**
	 * The default thread factory
	 */
	public static class FireDefaultThreadFactory implements ThreadFactory {
		final ThreadGroup group;
		final AtomicInteger threadNumber = new AtomicInteger(1);
		final String namePrefix;

		public FireDefaultThreadFactory(String subname) {
			SecurityManager s = System.getSecurityManager();
			// group = (s != null)? s.getThreadGroup() :
			// Thread.currentThread().getThreadGroup();
			group = ThreadManager.getInstance().getThreadGroup();

			namePrefix = group.getName() + "-pool-" + subname + "-";
		}

		public Thread newThread(Runnable r) {
			Thread t = new Thread(group, r, namePrefix
					+ threadNumber.getAndIncrement(), 0);
			if (t.isDaemon())
				t.setDaemon(false);
			if (t.getPriority() != Thread.NORM_PRIORITY)
				t.setPriority(Thread.NORM_PRIORITY);
			return t;
		}
	}
}
