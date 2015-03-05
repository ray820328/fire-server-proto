package com.ray.server.logic;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import com.ray.communicate.server.IFireNioCommand;
import com.ray.utils.util.PropertiesUtil;

public class CommandCache {
	private final static Map<Integer, IFireNioCommand> cache = new HashMap<Integer, IFireNioCommand>();
	public static void init(String config) throws Exception{
		Properties properties = PropertiesUtil.read(config);
		String local = (String)properties.getProperty("local");
		loadCommand(local);
		if(properties.containsKey("external")){
			String external = (String)properties.getProperty("external");
			loadCommand(external);
		}
		
	}
	
	public static void loadCommand(String config) throws Exception{
		Properties properties = PropertiesUtil.read(config);
		for(Object key : properties.keySet()){
			int id = Integer.parseInt((String)key);
			String clazz = (String)properties.get(key);
			cache.put(id, (IFireNioCommand)Class.forName(clazz).newInstance());
		}
	}
	
	@SuppressWarnings("unchecked")
	public static <T>T getCommand(int cid){
		return (T)cache.get(cid);
	}
}
