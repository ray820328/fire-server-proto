package com.ray.utils;

import java.util.concurrent.ScheduledFuture;

public interface IFireTimerTask extends Runnable{//,Callable<Object> {
	int type_one_time = 0;//仅运行一次
	int type_period_at_fixed_delay = 1;//如果运行时间较长、可能会同时执行
	int type_period_with_fixed_delay = 2;//保证在上次运行完后才会执行下一次
	
	public int getType();
	
	public ScheduledFuture<?> getScheduledFuture();

	public void setScheduledFuture(ScheduledFuture<?> scheduledFuture);
	
	/**
	 * 如果任务处于激活状态，结束或取消任务
	 * @param mayInterruptIfRunning
	 */
	void onEnd(boolean mayInterruptIfRunning);
}
