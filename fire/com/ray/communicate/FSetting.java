package com.ray.communicate;

import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.Properties;

public class FSetting {

	public static final String configFile="./config/system_configurations.properties";
	private static Properties props = new Properties();
	
	public static int logLevel = 10;
	public static String[] server_urls;
	public static String request_function;
	
	public static int request_interval;
//	init status, only send one time
	public static boolean[] notifyStates;
	
	public static int port;
	public static String server_handler;
	
	public static String email_host;
	public static int email_port;
	public static boolean email_ssl;
	public static boolean email_auth;
	public static String email_username;
	public static String email_password;
	public static String email_from_default;
	public static String email_subject;
	public static String email_message_pre;
	public static String email_message_post;
	public static String[] email_tos;
	
	static {
//		
//		try{
////			InputStream file=Configs.class.getResourceAsStream(configFile);
////			props.load(file);
//			RandomAccessFile raf = new RandomAccessFile(configFile, "r");
//			long filePointer = 0;
//			long length = raf.length();
//			while (filePointer < length){
//				String s = raf.readLine();
//				if (!s.startsWith("#") && s.trim().length()>0 && (s.indexOf("=")!=-1)){
//					s = new String(s);
//					s = encodingChinese(s);
//					String[] ss = s.split("=");
//					if(!s.trim().endsWith("=")){
//						props.put(ss[0].trim(),ss[1].trim());
//					}else{
//						props.put(ss[0].trim(),"");
//					}
//				}
//				filePointer = raf.getFilePointer();
//			}
//			raf.close();
//			
////			服务器配置
//			port = Integer.valueOf(props.getProperty("game_port"));
//			server_handler = props.getProperty("server_handler");
//			logLevel = Integer.valueOf(props.getProperty("log_level", "0"));		
//					
////			其他
//			server_urls = props.getProperty("server_urls", "").split(",");
//			for(int i=0; i<server_urls.length; i++){
//				server_urls[i] = server_urls[i].trim();
//			}
//			notifyStates = new boolean[server_urls.length];
//			for(int i=0; i<notifyStates.length; i++){
//				notifyStates[i] = false;
//			}
//			
//			request_function = props.getProperty("request_function");
//			request_interval = Integer.valueOf(props.getProperty("request_interval", "0"));
//			
//			email_host = props.getProperty("email_host");
//			email_port = props.getProperty("email_port")==null ? 25 : 
//				Integer.valueOf(props.getProperty("email_port"));
//			email_ssl = props.getProperty("email_ssl")==null ? false : 
//				Boolean.valueOf(props.getProperty("email_ssl"));
//			email_auth = props.getProperty("email_auth")==null ? false : 
//				Boolean.valueOf(props.getProperty("email_auth"));
//			email_username = props.getProperty("email_username");
//			email_password = props.getProperty("email_password");
//			email_from_default = props.getProperty("email_from_default");
//			email_subject = props.getProperty("email_subject");
//			email_message_pre = props.getProperty("email_message_pre");
//			email_message_post = props.getProperty("email_message_post");
//			email_tos = props.getProperty("email_tos", "").split(",");
//			for(int i=0; i<email_tos.length; i++){
//				email_tos[i] = email_tos[i].trim();
//			}
////			file.close();
//		}catch(Exception e){
//			e.printStackTrace();
//		}
	}
	
	public static Properties loadSystemProperties(){
		Properties properties=new Properties();
		try{
			InputStream file=FSetting.class.getResourceAsStream(configFile);
			properties.load(file);
		}catch(Exception e){
			e.printStackTrace();
		}
		return properties;
	}
	
	public static void updatePropertyFile(Properties property,String fileName)throws Exception{
		//SecurityManager security=System.getSecurityManager();
		//security.checkWrite(fileName);
		String enter=String.valueOf((char)0x0a);
		StringBuffer file=new StringBuffer();
		RandomAccessFile raf = new RandomAccessFile(fileName,"rw");
		long filePointer = 0;
		while (filePointer < raf.length()){
			String s = raf.readLine();
			if(s.startsWith("#")){
				file.append(s+enter);
			}else if(s.trim().length()==0){
				file.append(enter);
			}else if(s.indexOf("=")==-1){
				file.append(s+enter);
			}else{
				String[] ss=s.split("=");
				s=encodingChinese(s);
				String value=property.getProperty(ss[0].trim());
				if(value!=null && !value.trim().equals(ss[0].trim())){
					//	需要修改的属性
					String updateStr=ss[0]+"="+value;
					file.append(updateStr+enter);
					//	raf.seek(filePointer);
					//	raf.write(updateStr.getBytes("iso-8859-1"),0,updateStr.length());
					//	raf.setLength(raf.length()+(s.length()-updateStr.length()));
				}else{
					file.append(s+enter);
				}
			}
			filePointer=raf.getFilePointer();
		}
		byte[] source=file.toString().getBytes("iso-8859-1");
		raf.setLength(source.length);
		raf.seek(0);
		raf.write(source);
		raf.close();
	}
	
	public static String encodingChinese(String str){
		String s = null;
		byte temp[];
		if (str == null){
			return new String("");
		}
		try{
			temp = str.getBytes("iso-8859-1");
			s = new String(temp);
		}catch(java.io.UnsupportedEncodingException e){
			System.out.println(e.toString());
		}
		return s;
	}
	
	public static String getValue(String key){
		return props.getProperty(key, null);
	}
	
	/**
	 * 销毁资源文件
	 */
	public static void destroy(){
		props.clear();
		props = null;
	}
	
	public static void main(String[] args){
		System.out.println("server_url=" + server_urls[0]);
	}
}
