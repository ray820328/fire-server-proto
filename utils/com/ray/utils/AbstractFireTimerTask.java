package com.ray.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.concurrent.ScheduledFuture;

import com.ray.utils.util.Log;
import com.ray.utils.util.ValueUtil;

public abstract class AbstractFireTimerTask implements IFireTimerTask {
	private String name;
	private int type;
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
	public void onEnd(boolean mayInterruptIfRunning){
		if(scheduledFuture != null){
			if(!scheduledFuture.isDone()){
				scheduledFuture.cancel(mayInterruptIfRunning);
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
	
	@SuppressWarnings("rawtypes")
	public String toString(){
		try{
			StringBuffer sb = new StringBuffer(this.getClass().getName());
			sb.append(" {");
			Class clazz = getClass();
			Object value = null;
			while(clazz != Object.class){
				Field[] fields = clazz.getDeclaredFields();
				for(Field f : fields){
					if(f.getType().isAssignableFrom(ScheduledFuture.class) || 
							f.getType().isAssignableFrom(AbstractFireTimerTask.class)){
						continue;
					}
					if(Modifier.isStatic(f.getModifiers()) && Modifier.isFinal(f.getModifiers())){
						continue;//不显示静态常量
					}
					f.setAccessible(true);
					value = f.get(this);
					if(value != null){
						sb.append(f.getName() + ":" + f.get(this).toString() + ",");
					}
				}
				clazz = clazz.getSuperclass();
			}
			if(sb.length() > 0){
				sb.setLength(sb.length() - 1);
			}
			sb.append("}");
			return sb.toString();
		}catch(Exception ex){
			Log.error(ex);
			return super.toString();
		}
	}
}
