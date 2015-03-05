package com.ray.communicate;

import com.ray.utils.util.Log;

public class FSystem {

	public static byte[] encryptKey = {0x39, 0x71};
	public static byte[] decryptKey = {0x39, 0x71};
	
	public static int message_recived = 0;
	public static int message_sent = 0;
	
	/**
	 * 跟踪程序当前状态
	 */
	public static void trace(){
		Log.info("\r\n********* trace begin ********\r\n" +
				"Message Received: " + message_recived + "\r\n" + 
				"Message Sent:     " + message_sent + "\r\n" +
				"Memory used:      " + (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1024 + "KB\r\n"+
				"********* trace end   ********");
	}
	
	/**
	 * 统计程序运行变化
	 */
	public static void statistic(){
		
	}
	
	public static void gc(){
		Runtime.getRuntime().gc();
		Runtime.getRuntime().gc();
		Runtime.getRuntime().gc();
	}
}