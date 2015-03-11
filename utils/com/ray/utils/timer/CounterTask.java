package com.ray.utils.timer;

import java.util.concurrent.TimeUnit;

import com.ray.utils.util.Log;
import com.ray.utils.util.TimeUtil;

public abstract class CounterTask extends AbstractFireTimerTask {

	private TimeUnit timeUnit = TimeUnit.SECONDS;//单位
	private int gapTime = 5;//一次间隔时间
	private int maxCount = 5;//提前通知的最大次数
	private int count;//当前次数
	
	public CounterTask(String name){
		super(name, IFireTimerTask.type_period_at_fixed_delay);
	}
	public CounterTask(String name, int type, int gapTime, int maxCount){
		this(name);
		this.gapTime = gapTime;
		this.maxCount = maxCount;
	}
	
	@Override
	public void run(){
		Log.info("倒计时第 " + count + " 次");
		count--;
		if(count <= 0){
			onCancel(false);
			onEnd();
			return;
		}
	}
	@Override
	public void onCancel(boolean mayInterruptIfRunning){
		super.onCancel(mayInterruptIfRunning);
		Log.info("倒计时退出！");
	}
	
	/** 单位(秒) */
	public void init(long delay){
		if(delay < gapTime){//少于一次通知时间，不需要通知立刻执行
			this.setStartTime(TimeUtil.getDate(TimeUtil.getNowMillis()));
			this.setPeriod(TimeUnit.MILLISECONDS.convert(gapTime, timeUnit));
			return;
		}
		Log.info("部署倒计时");
		long noticeDelay = 0;
		int needCount = 0;
		while((delay-noticeDelay)>=gapTime && needCount<maxCount){//计算通知次数
			needCount++;
			noticeDelay += gapTime;
		}
		this.count = needCount;
		noticeDelay = delay - noticeDelay;//实际通知timer相对当前的延迟时间
		
		this.setStartTime(TimeUtil.getDate(TimeUtil.getNowMillis() + 
				TimeUnit.MILLISECONDS.convert(noticeDelay, timeUnit)));
		this.setPeriod(TimeUnit.MILLISECONDS.convert(gapTime, timeUnit));
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
