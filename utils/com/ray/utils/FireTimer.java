package com.ray.utils;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.ray.utils.util.TimeUtil;
import com.ray.utils.util.Log;
import com.ray.utils.util.ThreadManager.FireDefaultThreadFactory;

public class FireTimer {

	private final static ScheduledThreadPoolExecutor timer = 
			new ScheduledThreadPoolExecutor(1, new FireDefaultThreadFactory("timer"));
	static{
//		 如果value为true，则在主线程shutdown()方法后如果还没有执行(未达到delay的时间)，
//		则延迟任务仍然有机会执行，反之则不会执行，直接退出。（针对b、c方法）
		timer.setExecuteExistingDelayedTasksAfterShutdownPolicy(false);
//		如果value为true，则在主线程shutdown()方法后如果还没有执行(未达到delay的时间)，
//		则延迟任务仍然有机会执行，反之则不会执行，直接退出。（针对a方法）
		timer.setContinueExistingPeriodicTasksAfterShutdownPolicy(false);
    }
	public final static List<IFireTimerTask> taskList = new LinkedList<IFireTimerTask>();//timer.getQueue();//取不到task
	
	public static void schedule(IFireTimerTask task, Date datetime, long period){
		if(datetime == null){
			throw new RuntimeException("FireTimer.scheduleAtFixedRateAt datetime is empty!");
		}
		long initialDelay = TimeUtil.getNowMillis() - datetime.getTime();
		initialDelay = initialDelay<0 ? 0 : initialDelay;
		switch(task.getType()){
		case IFireTimerTask.type_one_time:
			scheduleOneTime(task, initialDelay, TimeUnit.MILLISECONDS);
			break;
		case IFireTimerTask.type_period_at_fixed_delay:
			scheduleAtFixedRate(task, initialDelay, period, TimeUnit.MILLISECONDS);
			break;
		case IFireTimerTask.type_period_with_fixed_delay:
			scheduleWithFixedDelay(task, initialDelay, period, TimeUnit.MILLISECONDS);
			break;
		}
	}
	
	/** a，延迟delay时间后开始执行 */
	public static void scheduleOneTime(IFireTimerTask task, long delay, TimeUnit unit){
		ScheduledFuture<?> scheduledFuture = timer.schedule(task, delay, unit);
		task.setScheduledFuture(scheduledFuture);
		taskList.add(task);
	}
	/** b，延迟initialDelay时间后开始执行，并且按照period时间周期性重复调用，
	 * 如果command运行时间较长、可能会同时执行（周期时间包括task运行时间） */
	public static void scheduleAtFixedRate(IFireTimerTask task, long initialDelay, long period, TimeUnit unit){
		ScheduledFuture<?> scheduledFuture = timer.scheduleAtFixedRate(task, initialDelay, period, unit);
		task.setScheduledFuture(scheduledFuture);
		taskList.add(task);
	}
	/** c，延迟initialDelay时间后开始执行，并且按照period时间周期性重复调用，
	 * 并且保证在上次运行完后才会执行下一次（周期时间不包括task运行时间）*/
	public static void scheduleWithFixedDelay(IFireTimerTask task, long initialDelay, long delay, TimeUnit unit){
		ScheduledFuture<?> scheduledFuture = timer.scheduleWithFixedDelay(task, initialDelay, delay, unit);
		task.setScheduledFuture(scheduledFuture);
		taskList.add(task);
	}
	
	public static void removeTask(IFireTimerTask task){
		synchronized(taskList){
			taskList.remove(task);
		}
	}
	
	public static StringBuilder getTaskListAsString(){
		StringBuilder sb = new StringBuilder();
		try{
			synchronized(taskList){
				for(IFireTimerTask task : taskList){
//					RunnableScheduledFuture st = (RunnableScheduledFuture)task;
//					sb.append(st).append(", delay=").append(st.getDelay(TimeUnit.SECONDS)).append("(S)\n");
					sb.append("runDate=").append(TimeUtil.formatFullDate(
							TimeUtil.getNowMillis() + task.getScheduledFuture().getDelay(TimeUnit.MILLISECONDS))).
					append(", delay=").append(task.getScheduledFuture().getDelay(TimeUnit.SECONDS)).append("Sec").
					append(", detail=").append(task.toString()).append("\n");
				}
			}
		}catch(Exception ex){
			Log.error("FireTimer.printTaskList", ex);
		}
		return sb;
	}
	public static void printTaskList(){
		StringBuilder sb = getTaskListAsString();
		sb.insert(0, "Start printing timer tasks-----------------------\n");
		sb.append("End printing timer tasks-----------------------");
		Log.info(sb.toString());
	}
	
	public static final void main(String[] args){
//		固定间隔执行
		IFireTimerTask timerTask = new AbstractFireTimerTask("testTimerTask", IFireTimerTask.type_period_with_fixed_delay){
			private int count = 0;
			public void run(){
				if(count >= 3){
					Log.info("执行已经满足 " + count + " 次，本次任务取消！");
					onEnd(false);
					FireTimer.printTaskList();
					return;
				}
				Log.info("开始执行第 " + count + " 次");
				try{
		            TimeUnit.SECONDS.sleep(4);
		        } catch(InterruptedException e) {
		            e.printStackTrace();
		        }
				Log.info("结束第 " + count + " 次");
				count++;
			}
			public Object call() throws Exception{
				return null;
			}
		};
		FireTimer.schedule(timerTask, TimeUtil.getDate(TimeUtil.getNowMillis()+5), 3);
		FireTimer.printTaskList();
	}
}
