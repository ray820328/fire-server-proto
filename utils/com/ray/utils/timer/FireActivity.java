package com.ray.utils.timer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.ray.utils.util.Log;
import com.ray.utils.util.StringUtils;
import com.ray.utils.util.TimeUtil;
import com.ray.utils.util.ValueUtil;

/**
 * 需要提前通知开启的定时器事件，比如开启5分钟倒数
 * 实现方式：
 * 在部署定时器时，先部署最开始的倒计时定时器，
 * 倒计时逻辑结束后部署目标定时器，依次循环
 */
public class FireActivity {
	public static final int state_notice = 0;
	public static final int state_progress = 1;
	public static final int state_end = 2;
//	protected String configTimes = "1=17:00:00_17:05:00|3=17:00:00_17:05:00|5=17:00:00_17:05:00";
	protected String configTimes = "0=16:27:00_16:28:00|0=16:29:00_16:31:00|0=18:14:00_19:05:00";
	protected List<ActivityTime> times;
	protected ActivityTime atime;//主定时器的执行时间
	protected boolean start;
	private int state = 0;//当前状态
	
	private AbstractFireTimerTask mainTask;//主task
	private CounterTask counterTask;//倒计时通知
	
	public FireActivity(){
		this.times = new ArrayList<ActivityTime>();
		String ts[] = configTimes.split(StringUtils.split_verticalline);
		for(String t : ts){
			if(t==null || "".equals(t.trim())){
				break;
			}
			ActivityTime time = new ActivityTime(t);
			times.add(time);
		}
		Collections.sort(times);
//		初始化主任务开始时间
		initATime();
		
		mainTask = new AbstractFireTimerTask("mainTask", IFireTimerTask.type_period_with_fixed_delay){
			public void run(){
				Log.info("主任务逻辑");
				onEnd();
			}
			@Override
			public void onEnd(){
				if(state == state_progress){
					state = state_end;
					Log.info("活动开启");
				}else if(state == state_end){//重新开始
					onCancel(false);//一个周期完毕，取消主任务
					Log.info("活动结束");
					initATime();
//					重新开始部署
					schedule();
				}else{
					Log.info("主任务逻辑，无效状态=" + state);
				}
			}
		};
		
		counterTask = new CounterTask("activityCounterTask", IFireTimerTask.type_period_at_fixed_delay, 2, 5){
			@Override
			public void onEnd(){
				Log.info("结束倒计时任务，部署主定时器");
				state = state_progress;
				long maxPeriodTime = atime.getEndTime().getTime() - atime.getStartTime().getTime();
				long remainingPeriodTime = atime.getEndTime().getTime() - TimeUtil.getNowMillis();
				remainingPeriodTime = remainingPeriodTime>0 ? remainingPeriodTime : 0;
				mainTask.setStartTime(TimeUtil.getDate(atime.getStartTime().getTime()));
//				结束时间，可能已经开始，所以取较小值
				mainTask.setPeriod(remainingPeriodTime>maxPeriodTime ? maxPeriodTime : remainingPeriodTime);
				FireTimer.schedule(mainTask);//主任务
			}
		};
	}
	
	/** 开始部署 */
	public void schedule(){
		state = state_notice;
		long delay = atime.getStartTime().getTime() - TimeUtil.getNowMillis();
		counterTask.init(TimeUnit.SECONDS.convert(delay/TimeUtil.second_millis, counterTask.getTimeUnit()));
		FireTimer.schedule(counterTask);
	}
	
	public IFireTimerTask getNextTask(){
		
		return null;
	}
	
	public void onEnd(){
		
	}
	
	public ActivityTime getAtime(){
		return atime;
	}
	public void initATime(){
		atime = null;
		Date now = TimeUtil.getNowDate();
		do{
			for(ActivityTime time : times){
				if(!time.isOver(now)){
					atime = time;
					break;
				}
			}
			if(atime == null){
				updateTime();
			}
		}while(atime == null);
	}
	/** 整体时间往后推一个周期，周期为一天或者一周 */
	public void updateTime(){
		for(ActivityTime time : times){
			time.updateTime();
		}
	}
	public String toString(){
		try{
			return ValueUtil.toJsonString(this, new Class[]{IFireTimerTask.class});
		}catch(Exception ex){
			ex.printStackTrace();
			return "";
		}
	}
	
	
	public static class ActivityTime implements Comparable<ActivityTime>{
		private int day;//每周日期(0-7)0:每天  1-7:依次为上周日到这周六
		private String start;//开始时间
		private String end;//结束时间
		private Date startTime;//开始时间
		private Date endTime;//结束时间
		private Date lastStartTime;//开始时间
		private Date lastEndTime;//结束时间
		
		public ActivityTime(){}
		public ActivityTime(String time){
			String str[] = time.split(StringUtils.split_equal);
			day = Integer.parseInt(str[0]);
			String str1[] = str[1].split(StringUtils.split_underline);
			start = str1[0];
			end = str1[1];
			
			if(day > 0){//一周的第几天
				startTime = TimeUtil.getDateOfNextWeek(day % 7, start);
				endTime = TimeUtil.getDateOfNextWeek(day % 7, end);
				lastStartTime = TimeUtil.getDateOfNextWeek(day % 7 , start);
				lastEndTime = TimeUtil.getDateOfNextWeek(day % 7, end);
			}else{//每天
				Date date = TimeUtil.getNowDate();
				startTime = TimeUtil.getDate(date, start);
				endTime = TimeUtil.getDate(date, end);
				lastStartTime = TimeUtil.getDate(date, start);
				lastEndTime = TimeUtil.getDate(date, end);
			}
		}
		public boolean isBegin(Date date){
			return startTime.getTime() <= date.getTime() && date.getTime() < endTime.getTime();
		}
		public boolean isOver(Date date){
			return endTime.getTime() <= date.getTime();
		}
		public void updateTime(){
			lastStartTime = startTime;
			lastEndTime = endTime;
			int period = day > 0 ? 7 : 1;
			startTime = TimeUtil.getDate(startTime.getTime() + period * TimeUtil.day_millis);
			endTime = TimeUtil.getDate(endTime.getTime() + period * TimeUtil.day_millis);
		}
		@Override
		public int compareTo(ActivityTime at) {
			if(startTime.getTime() < at.getStartTime().getTime()){
				return -1;
			}
			return 1;
		}
		
		public int getStartToEndMinute(){
			int interval = TimeUtil.getSecond(endTime.getTime() - startTime.getTime());
			int minute = (interval + TimeUtil.minute_second - 1) / TimeUtil.minute_second;
			return minute;
		}

		public String toString(){
			try{
				return ValueUtil.toJsonString(this, null);
			}catch(Exception ex){
				ex.printStackTrace();
				return "";
			}
		}
		
		public int getStartTimeSecond(){
			return TimeUtil.getSecond(startTime.getTime());
		}
		public int getEndTimeSecond(){
			return TimeUtil.getSecond(endTime.getTime());
		}
		public int getLastStartTimeSecond(){
			return TimeUtil.getSecond(lastStartTime.getTime());
		}
		public int getLastEndTimeSecond(){
			return TimeUtil.getSecond(lastEndTime.getTime());
		}
		public int getDay() {
			return day;
		}
		public void setDay(int day) {
			this.day = day;
		}
		public String getStart() {
			return start;
		}
		public void setStart(String start) {
			this.start = start;
		}
		public String getEnd() {
			return end;
		}
		public void setEnd(String end) {
			this.end = end;
		}
		public Date getStartTime() {
			return startTime;
		}
		public void setStartTime(Date startTime) {
			this.startTime = startTime;
		}
		public Date getEndTime() {
			return endTime;
		}
		public void setEndTime(Date endTime) {
			this.endTime = endTime;
		}
		public Date getLastStartTime() {
			return lastStartTime;
		}
		public void setLastStartTime(Date lastStartTime) {
			this.lastStartTime = lastStartTime;
		}
		public Date getLastEndTime() {
			return lastEndTime;
		}
		public void setLastEndTime(Date lastEndTime) {
			this.lastEndTime = lastEndTime;
		}
	}
}
