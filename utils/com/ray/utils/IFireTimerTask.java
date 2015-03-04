package com.ray.utils;

import java.util.concurrent.ScheduledFuture;

public interface IFireTimerTask extends Runnable{//,Callable<Object> {

	public ScheduledFuture<?> getScheduledFuture();

	public void setScheduledFuture(ScheduledFuture<?> scheduledFuture);
	
	/**
	 * 如果任务处于激活状态，结束或取消任务
	 * @param mayInterruptIfRunning
	 */
	void onEnd(boolean mayInterruptIfRunning);
}
