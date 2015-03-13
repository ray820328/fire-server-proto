package com.ray.game.logic;

import java.util.concurrent.ExecutorService;

import com.ray.utils.util.Log;
import com.ray.utils.util.NumberUtils;
import com.ray.utils.util.ThreadManager;

public class TestMapExecutor {
	private ExecutorService executor = ThreadManager.newThreadPool(1, 10, 0, "test", null);
	
	public void runTask(final Runnable task){
		Runnable t = new MapTask(task, executor);
		executor.execute(t);
	}
	
	public static void main(String[] args){
		TestMapExecutor mapExecutor = new TestMapExecutor();
		
		for(int i=0; i<5; i++){
			final int id = i;
			mapExecutor.runTask(new Runnable(){
				int count = 0;
				public void run(){
					System.out.println(id + ": " + Thread.currentThread().getName() + ": " + ++count);
				}
			});
		}
	}
	
	public static class MapTask implements Runnable {
		private Runnable destTask;
		private ExecutorService executor;
		
		public MapTask(Runnable dest, ExecutorService executor){
			if(dest==null || executor==null){
				throw new RuntimeException("MapTask.dest can't be null.");
			}
			destTask = dest;
			this.executor = executor;
		}
		public void run(){
//			while(true){
			destTask.run();
				try{
					synchronized(destTask){
						destTask.wait(NumberUtils.randomLong(1000, 1000));
					}
//					Thread.sleep(NumberUtils.randomLong(1000, 2000));
//					Thread.yield();
				}catch(Exception ex){
					Log.error(ex);
				}
//			}
			executor.execute(this);
		}
	}
}
