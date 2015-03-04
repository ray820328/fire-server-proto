package com.ray.utils;

import java.util.concurrent.ScheduledFuture;

import com.ray.fire.util.Log;
import com.ray.fire.util.ValueUtil;

public abstract class AbstractFireTimerTask implements IFireTimerTask {
	private String name;
	private ScheduledFuture<?> scheduledFuture;
	
	public AbstractFireTimerTask(String name){
		this.name = name;
	}
	
	public ScheduledFuture<?> getScheduledFuture() {
		return scheduledFuture;
	}
	public void setScheduledFuture(ScheduledFuture<?> scheduledFuture) {
		this.scheduledFuture = scheduledFuture;
	}
	
	public void onEnd(boolean mayInterruptIfRunning){
		if(scheduledFuture != null){
			if(!scheduledFuture.isDone()){
				scheduledFuture.cancel(false);
			}
		}
		FireTimer.removeTask(this);
	}
	
	public String toString(){
		try{
			return this.getClass().getName() + ": " + ValueUtil.toJsonString(this);
		}catch(Exception ex){
			Log.error(ex);
			return super.toString();
		}
	}
}
