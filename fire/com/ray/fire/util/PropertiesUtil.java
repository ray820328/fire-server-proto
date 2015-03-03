package com.ray.fire.util;

import java.io.FileInputStream;
import java.util.Properties;

public class PropertiesUtil {
	public static Properties read(String file) throws Exception{
		FileInputStream input = new FileInputStream(file);
		Properties properties = new Properties();
		try{
			properties.load(input);
		}finally{
			input.close();
		}
		return properties;
	}
	public static String getProperty(Properties properties, String key) throws Exception{
		String value = properties.getProperty(key);
		if(value != null){
			return new String(value.getBytes("ISO-8859-1"), "UTF-8");
		}
		return "";
	}
}
