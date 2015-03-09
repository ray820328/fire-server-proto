package com.ray.utils;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import com.ray.utils.util.Log;
import com.ray.utils.util.TimeUtil;

/**
 * 需要提前通知开启的定时器事件，比如开启5分钟倒数
 * 实现方式：
 * 在部署定时器后，额外部署一个倒计时定时器，
 * 倒计时定时器以一定间隔执行并部署下一个倒计时定时器直到次数达到后结束
 */
public abstract class NoticeTask extends AbstractFireTimerTask {

	private TimeUnit timeUnit = TimeUnit.SECONDS;//单位
	private int gapTime = 5;//一次间隔时间
	private int maxCount = 5;//提前通知的最大次数
	private int count;//当前次数
	private IFireTimerTask noticingTask;
	
	public NoticeTask(String name, int type){
		super(name, type);
	}
	public NoticeTask(String name, int type, int gapTime, int maxCount){
		this(name, type);
		this.gapTime = gapTime;
		this.maxCount = maxCount;
	}
	
//	@Override
//	public void run(){
//		beforeExecute();
//		exec();
//		endExecute();
//	}
//	/** 处理开启前通知 */
//	public void beforeExecute(){
//		
//	}
//	/** 处理关闭前通知 */
//	public void endExecute(){
////		重新部署下一次通知
//		scheduleNoticing();
//	}
//	public abstract void exec();
	
	@Override
	public void afterSchedule(){
		scheduleNoticing();
	}
	
	public void scheduleNoticing(){
		long delay = getScheduledFuture()==null ? 0 : getScheduledFuture().getDelay(TimeUnit.SECONDS);
		Log.info("delay= " + getScheduledFuture().getDelay(TimeUnit.SECONDS));
		if(delay < gapTime){//少于一次通知时间，不需要通知立刻执行
			return;
		}
		long noticeDelay = 0;
		int needCount = 0;
		while((delay-noticeDelay)>=gapTime && needCount<maxCount){//计算通知次数
			needCount++;
			noticeDelay += gapTime;
		}
		this.count = needCount;
		noticeDelay = delay - noticeDelay;//实际通知timer相对当前的延迟时间
//		通知定时器
		noticingTask = new AbstractFireTimerTask("NoticeTimerTask", IFireTimerTask.type_period_with_fixed_delay){
			public void run(){
				Log.info("倒计时第 " + count + " 次");
				count--;
				if(count <= 0){
					Log.info("通知结束！");
					noticingTask.onEnd(false);
					FireTimer.printTaskList();
					return;
				}
			}
		};
		FireTimer.schedule(noticingTask, 
				TimeUtil.getDate(TimeUtil.getNowMillis()+TimeUnit.MILLISECONDS.convert(noticeDelay, timeUnit)), 
				TimeUnit.MILLISECONDS.convert(gapTime, timeUnit));
	}
	
	@Override
	public void onEnd(boolean mayInterruptIfRunning){
//		结束主task
		super.onEnd(mayInterruptIfRunning);
		if(noticingTask == null){
			return;
		}
//		结束通知task
		if(noticingTask.getScheduledFuture() != null){
			if(!noticingTask.getScheduledFuture().isDone()){
				noticingTask.getScheduledFuture().cancel(false);
			}
		}
		FireTimer.removeTask(noticingTask);
	}

	public TimeUnit getTimeUnit() {
		return timeUnit;
	}
	public void setTimeUnit(TimeUnit timeUnit) {
		this.timeUnit = timeUnit;
	}

	public int getGapTime() {
		return gapTime;
	}
	public void setGapTime(int gapTime) {
		this.gapTime = gapTime;
	}

	public int getMaxCount() {
		return maxCount;
	}
	public void setMaxCount(int maxCount) {
		this.maxCount = maxCount;
	}
}
