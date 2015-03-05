package com.ray.utils;

import java.util.concurrent.ScheduledFuture;

import com.ray.utils.util.Log;
import com.ray.utils.util.ValueUtil;

public abstract class AbstractFireTimerTask implements IFireTimerTask {
	private String name;
	private int type;
	private ScheduledFuture<?> scheduledFuture;
	
	public AbstractFireTimerTask(String name){
		this.name = name;
	}
	public AbstractFireTimerTask(String name, int type){
		this(name);
		this.type = type;
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
	
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
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
