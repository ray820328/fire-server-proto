package com.ray.utils;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.RunnableScheduledFuture;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import com.ray.fire.util.Log;

public class FireTimer {

	private final static ScheduledThreadPoolExecutor timer = new ScheduledThreadPoolExecutor(1, new ThreadFactory(){
		private AtomicInteger atoInteger = new AtomicInteger(0); 
	    public Thread newThread(Runnable r) { 
	        Thread t = new Thread(r); 
	        t.setName("timer-pool-"+ atoInteger.getAndIncrement()); 
	        return t; 
	    } 
	});
	static{
//		 如果value为true，则在主线程shutdown()方法后如果还没有执行(未达到delay的时间)，
//		则延迟任务仍然有机会执行，反之则不会执行，直接退出。（针对b、c方法）
		timer.setExecuteExistingDelayedTasksAfterShutdownPolicy(false);
//		如果value为true，则在主线程shutdown()方法后如果还没有执行(未达到delay的时间)，
//		则延迟任务仍然有机会执行，反之则不会执行，直接退出。（针对a方法）
		timer.setContinueExistingPeriodicTasksAfterShutdownPolicy(false);
    }
	public final static List<IFireTimerTask> taskList = new LinkedList<IFireTimerTask>();//timer.getQueue();//取不到task
	
	/** a，延迟delay时间后开始执行 */
	public static void schedule(IFireTimerTask task, long delay, TimeUnit unit){
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
	
	public static void printTaskList(){
		StringBuilder sb = new StringBuilder("Start printing timer tasks-----------------------\n");
		try{
			synchronized(taskList){
				for(IFireTimerTask task : taskList){
//					RunnableScheduledFuture st = (RunnableScheduledFuture)task;
//					sb.append(st).append(", delay=").append(st.getDelay(TimeUnit.SECONDS)).append("(S)\n");
					sb.append("delay=").append(task.getScheduledFuture().getDelay(TimeUnit.SECONDS)).append("Sec").
					append(", detail=").append(task.toString()).append("\n");
				}
			}
		}catch(Exception ex){
			Log.error("FireTimer.printTaskList", ex);
		}
		sb.append("End printing timer tasks-----------------------");
		Log.info(sb.toString());
	}
	
	public static final void main(String[] args){
//		固定间隔执行
		IFireTimerTask timerTask = new AbstractFireTimerTask("testTimerTask"){
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
//		FireTimer.scheduleAtFixedRate(timerTask, 5, 3, TimeUnit.SECONDS);
		FireTimer.scheduleWithFixedDelay(timerTask, 5, 3, TimeUnit.SECONDS);
		FireTimer.printTaskList();
	}
}
