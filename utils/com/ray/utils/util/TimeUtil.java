package com.ray.utils.util;
/**
 * <p>For Date-String and String-Date exchange</p>
 * <p>Copyright: Copyright (c) 2007 Sample King</p>
 * @author Even
 * @version v0.0
 * <p>Last update（2008-12-10）</p>
 */
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TimeUtil {
	
	private static final SimpleDateFormat common_date_format =
		new SimpleDateFormat("yyyy-MM-dd");
	private static final SimpleDateFormat date_format_MMddyyyy =
		new SimpleDateFormat("MM/dd/yyyy");
	
	private static final SimpleDateFormat full_date_format =
		new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private static final SimpleDateFormat full_date_yyyyMMdd =
			new SimpleDateFormat("yyyy/MM/dd");
	private static final SimpleDateFormat common_time_format =
			new SimpleDateFormat("HH:mm:ss");
	private static final SimpleDateFormat full_time_format =
			new SimpleDateFormat("HH:mm:ss SS");
		
	public final static int second_ = 1;
	public final static int minute_second = 60 * second_;
	public final static int houre_second = 60 * minute_second;
	public final static int day_second = 24 * houre_second;
	
	public final static long second_millis = 1000;
	public final static long minute_millis = minute_second * second_millis;
	public final static long hour_millis = 60 * minute_millis;
	public final static long day_millis = 24 * hour_millis;
	
	/**
	 * format for: MM/dd/yyyy
	 * @param datetime 
	 * @return 
	 */
	public static String formatMMddyyyyDate(long datetime){
		return date_format_MMddyyyy.format(new Date(datetime));
	}
	
	/**
	 * format for: yyyy/MM/dd
	 * @param datetime 
	 * @return 
	 */
	public static String formatyyyyMMddDate(long datetime){
		return full_date_yyyyMMdd.format(new Date(datetime));
	}
	/**
	 * format for: yyyy-MM-dd
	 * @param datetime 
	 * @return 
	 */
	public static String formatCommonDate(long datetime){
		return common_date_format.format(new Date(datetime));
	}
	/**
	 * format for: yyyy-MM-dd HH:mm:ss
	 * @param datetime 
	 * @return 
	 */
	public static String formatFullDate(long datetime){
		return full_date_format.format(new Date(datetime));
	}
	/**
	 * parse parameter to the date object
	 * @param str (yyyy-MM-dd)
	 * @return null if the format is not fit for it
	 */
	public static Date toCommonDate(String str){
		try{
			return common_date_format.parse(str);
		}catch(Exception e){
			throw new RuntimeException("The input sample must be like this: 08/21/2008");
		}
	}
	/**
	 * return the Long value
	 * @param str (yyyy-MM-dd)
	 * @return null if the parameter is not a data string
	 */
	public static Long toCommonDateValue(String str){
		try{
			return common_date_format.parse(str).getTime();
		}catch(Exception e){
			return null;
		}
	}
	
	/**
	 * return the Long value
	 * @param str (MM/dd/yyyy)
	 * @return null if the parameter is not a data string
	 */
	public static Long toMMddyyyyDateValue(String str){
		try{
			return date_format_MMddyyyy.parse(str).getTime();
		}catch(Exception e){
			return null;
		}
	}
	/**
	 * return the Long value
	 * @param str (yyyy-MM-dd HH:mm:ss)
	 * @return null if the parameter is not a data string
	 */
	public static Date toFullDate(String str){
		try{
			return full_date_format.parse(str);
		}catch(Exception e){
			return null;
		}
	}
	
	/** format for: HH:mm:ss */
	public static String formatCommonTime(long datetime){
		return common_time_format.format(new Date(datetime));
	}
	/** parse parameter to the date object (HH:mm:ss) */
	public static Date toCommonTime(String str){
		try{
			return common_time_format.parse(str);
		}catch(Exception e){
			throw new RuntimeException("The input sample must be like this: HH:mm:ss");
		}
	}
	
	/** format for: HH:mm:ss */
	public static String formatFullTime(long datetime){
		return full_time_format.format(new Date(datetime));
	}
	/** parse parameter to the date object (HH:mm:ss SS) */
	public static Date toFullTime(String str){
		try{
			return full_time_format.parse(str);
		}catch(Exception e){
			throw new RuntimeException("The input sample must be like this: HH:mm:ss SS");
		}
	}
	
	/** today as 'MM/dd/yyyy' */
	public static String todayAsString(){
		return formatMMddyyyyDate(new Date().getTime());
	}
	
	public static int getNowSecond(){
		return (int)(System.currentTimeMillis() / second_millis);
	}
	public static long getNowMillis(){
		return System.currentTimeMillis();
	}
	public static Date getNowDate(){
		return new Date();
	}
	public static Date getDate(long millis){
		return new Date(millis);
	}
	/** 参数为时间戳Millis*/
	public static int getSecond(long time){
		return (int)(time / second_millis);
	}
	/** 某一天对应时间点的结果 */
	public static Date getDate(Date date, String str){
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(TimeUtil.toCommonTime(str));
		int startHour = calendar.get(Calendar.HOUR_OF_DAY);//24小时制
		int startMinute = calendar.get(Calendar.MINUTE);
		int startSecond = calendar.get(Calendar.SECOND);
		
		return getDate(date, startHour, startMinute, startSecond, 0);
	}
	/** 某一天对应时间点的结果 */
	public static Date getDate(Date date, int hour, int minute, int second, int millis){
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.HOUR_OF_DAY, hour);
		calendar.set(Calendar.MINUTE, minute);
		calendar.set(Calendar.SECOND, second);
		calendar.set(Calendar.MILLISECOND, millis);
		return calendar.getTime();
	}
	/** 某月某一天某时刻 */
	public static Date getDateOfMonth(int month, int day, int hour, int minute, int second, int millis){
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.MONTH, month);
		calendar.set(Calendar.DATE, day);
		calendar.set(Calendar.HOUR_OF_DAY, hour);
		calendar.set(Calendar.MINUTE, minute);
		calendar.set(Calendar.SECOND, second);
		calendar.set(Calendar.MILLISECOND, millis);
		return calendar.getTime();
	}
	/** 某月某一天某时刻 */
	public static Date getDateOfYear(int year, int month, int day, int hour, int minute, int second){
		Calendar calendar = Calendar.getInstance();
		calendar.set(year, month, day, hour, minute, second);
		return calendar.getTime();
	}
	
	/** 一周的星期几对应的日期，注意一周从上周日开始算，依次为1-7 */
	public static Date getDateOfWeek(int day, String str){
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(TimeUtil.toCommonTime(str));
		int startHour = calendar.get(Calendar.HOUR_OF_DAY);//24小时制
		int startMinute = calendar.get(Calendar.MINUTE);
		int startSecond = calendar.get(Calendar.SECOND);
		return getDateOfWeek(day, startHour, startMinute, startSecond);
	}
	/** 一周的星期几对应的日期，注意一周从上周日开始算，依次为1-7 */
	public static Date getDateOfWeek(int day, int hour, int minute, int second){
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.DAY_OF_WEEK, day);
		calendar.set(Calendar.HOUR_OF_DAY, hour);
		calendar.set(Calendar.MINUTE, minute);
		calendar.set(Calendar.SECOND, second);
		return calendar.getTime();
	}
	
	/** 一周的星期几对应的日期,如果目标时间已经过了,返回下一周的day,注意一周从上周日开始算,依次为1-7 */
	public static Date getDateOfNextWeek(int day, String str){
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(TimeUtil.toCommonTime(str));
		int startHour = calendar.get(Calendar.HOUR_OF_DAY);//24小时制
		int startMinute = calendar.get(Calendar.MINUTE);
		int startSecond = calendar.get(Calendar.SECOND);
		return getDateOfNextWeek(day, startHour, startMinute, startSecond);
	}
	/** 一周的星期几对应的日期,如果目标时间已经过了,返回下一周的day,注意一周从上周日开始算,依次为1-7 */
	public static Date getDateOfNextWeek(int day, int hour, int minute, int second){
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.DAY_OF_WEEK, day);
		calendar.set(Calendar.HOUR_OF_DAY, hour);
		calendar.set(Calendar.MINUTE, minute);
		calendar.set(Calendar.SECOND, second);
		Date date = getNowDate();
		while(calendar.getTimeInMillis() < date.getTime()){
			calendar.setTime(new Date(calendar.getTimeInMillis() + 7 * day_millis));
		}
		return calendar.getTime();
	}
	
	public static void main(String[] args){
		int day = Calendar.SUNDAY;
		System.out.println(formatFullDate(getDateOfWeek(day, 0, 0, 0).getTime()));
		System.out.println(formatFullDate(getDateOfNextWeek(day, 0, 0, 0).getTime()));
	}
}
