package com.ray.utils.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ray.communicate.FSetting;

/**
 * <p></p>
 * <p>Copyright (c) 2007 Sample King</p>
 * @author Ray
 * @version v1.0
 * <p>Last update (2010-8-10)</p>
 */

public class Log {

	public static final String line_break = "\r\n";
	public static int LOG_LEVEL = FSetting.logLevel;
	
	public static final int DEBUG = 0;
	public static final int INFO = 10;
	public static final int WARNING = 20;
	public static final int ERROR = 30;
	public static final int FATAL = 40;
	
	private final static Logger logger = LoggerFactory.getLogger("NET");
	private final static Logger error = LoggerFactory.getLogger("ERROR");
	
	public static void debug(String message){
		if(DEBUG < LOG_LEVEL){
			return;
		}
		logger.debug(message);
	}
	
	public static void info(String message){
		if(INFO < LOG_LEVEL){
			return;
		}
		logger.info(message);
	}
	
	public static void warning(String message){
		if(WARNING < LOG_LEVEL){
			return;
		}
		logger.warn(message);
	}
	
	public static void error(Object message){
		if(ERROR < LOG_LEVEL){
			return;
		}
		if(message instanceof Throwable){
			error("Error", (Throwable)message);
		}else{
			error.error(message.toString());
		}
	}
	
	public static void error(String message, Throwable ex){
		if(ERROR < LOG_LEVEL){
			return;
		}
		error.error(message, ex);
	}
	
	public static void fatal(String message){
		if(FATAL < LOG_LEVEL){
			return;
		}
		error.error(message);
	}
	
	public static void output(String message){
		System.out.println(message);
	}
	
	public static void debug(String message, Object[] objs){
		StringBuilder sb = new StringBuilder(message);
		if(objs != null){
			int i = 0;
			for(Object obj : objs){
				sb.append(line_break).append(i++).append("= ").append(obj.toString());
			}
		}
		debug(sb.toString());
	}
}
