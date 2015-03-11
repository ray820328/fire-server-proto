package com.ray.utils.timer;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Date;
import java.util.concurrent.ScheduledFuture;

import com.ray.utils.util.Log;
import com.ray.utils.util.ValueUtil;

public abstract class AbstractFireTimerTask implements IFireTimerTask {
	private String name;
	private int type;
	private Date startTime;
	private long period;
	private ScheduledFuture<?> scheduledFuture;
	
	public AbstractFireTimerTask(String name, int type){
		this.name = name;
		this.type = type;
	}
	
	public ScheduledFuture<?> getScheduledFuture() {
		return scheduledFuture;
	}
	public void setScheduledFuture(ScheduledFuture<?> scheduledFuture) {
		this.scheduledFuture = scheduledFuture;
	}
	@Override
	public void afterSchedule(){
		
	}
	@Override
	public void onCancel(boolean mayInterruptIfRunning){
		if(scheduledFuture != null){
			if(!scheduledFuture.isDone()){
				scheduledFuture.cancel(mayInterruptIfRunning);
			}
		}
		FireTimer.removeTask(this);
	}
	
//	@Override
//	public void run(){
//		beforeExecute();
//		exec();
//		endExecute();
//	}
//	/** 处理开启前 */
//	public void beforeExecute(){
//		
//	}
//	/** 处理关闭前 */
//	public void endExecute(){
//		
//	}
//	public abstract void exec();
	
	public void onEnd(){
		
	}
	public void onStart(){
		
	}

	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	
	public Date getStartTime(){
		return startTime;
	}
	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}
	public void setPeriod(long period) {
		this.period = period;
	}
	public long getPeriod(){
		return period;
	}
	
	public String toString(){
		try{
			return ValueUtil.toJsonString(this, new Class[]{ScheduledFuture.class, IFireTimerTask.class});
		}catch(Exception ex){
			Log.error(ex);
			return super.toString();
		}
	}
}
