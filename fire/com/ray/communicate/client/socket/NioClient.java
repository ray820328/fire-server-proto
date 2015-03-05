package com.ray.communicate.client.socket;

import java.net.InetSocketAddress;
import java.util.Properties;

import javax.net.ssl.SSLContext;

import org.apache.log4j.PropertyConfigurator;
import org.apache.mina.core.filterchain.IoFilter;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.future.IoFutureListener;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.filter.ssl.SslFilter;
import org.apache.mina.transport.socket.nio.NioSocketConnector;
import org.apache.mina.core.service.IoHandler;

import com.ray.communicate.message.IoMessage;
import com.ray.communicate.server.ssl.BogusSslContextFactory;
import com.ray.server.logic.CommandCache;
import com.ray.utils.util.PropertiesUtil;

public class NioClient {
	private NioSocketConnector connector;
	private IoSession session;
	private int id;
	private String host;
	private int port;
	private NioClientBindComplete bindComplete;
	private boolean immediately = false;
	private boolean useSsl = false;
	public NioClient(String configLog4j, String configClient, IoHandler ioHandler, boolean useSsl) throws Exception{
		PropertyConfigurator.configure(configLog4j);
		   
		Properties properties = PropertiesUtil.read(configClient);
		connector = new NioSocketConnector();
		id = Integer.parseInt(properties.getProperty("client.id"));
		host = properties.getProperty("client.host");
		port = Integer.parseInt(properties.getProperty("client.port"));
		connector.setHandler(ioHandler);
		
		if(useSsl){
            SSLContext sslContext = BogusSslContextFactory.getInstance(false);
            SslFilter sslFilter = new SslFilter(sslContext);
            sslFilter.setUseClientMode(true);
            connector.getFilterChain().addFirst("sslFilter", sslFilter);
        }
		connector.getSessionConfig().setTcpNoDelay(true);
		LoggingFilter loggingFilter = new LoggingFilter();   
		addLastFilter("logging", loggingFilter);
	}
	
	public void addFirstFilter(String name, IoFilter filter){
		 connector.getFilterChain().addFirst(name, filter);
	}
	public void addLastFilter(String name, IoFilter filter){
		 connector.getFilterChain().addLast(name, filter);
	}
	public void resetSession(){
		this.session = null;
	}
	public void bind() throws Exception{
	    while(!immediately && session == null){
		    try{
		    	ConnectFuture future = connector.connect(new InetSocketAddress(host, port));  
			    future.awaitUninterruptibly();
		    	session = future.getSession();
		    	session.setAttribute("client", this);
		    }catch (Exception e) {
		    	e.printStackTrace();
		    	Thread.sleep(3000L);
			}
	    }
	    if(!immediately && bindComplete!=null){
	    	bindComplete.complete(this);
	    }
	}
	
	public void close(boolean immediately) throws Exception{
		this.immediately = immediately;
		if(session != null && !session.isClosing()){
			session.close(immediately);
		}
	}
	
	public void setImmediately(boolean immediately){
		this.immediately = immediately;
	}
	
	public void write(IoMessage message){
		session.write(message);
	}
	
	@SuppressWarnings("rawtypes")
	public void write(IoMessage message, IoFutureListener listener){
		session.write(message).addListener(listener);
	}
	
	public IoSession getSession(){
		return session;
	}
	
	public int getId(){
		return id;
	}

	public NioClientBindComplete getBindComplete() {
		return bindComplete;
	}
	public void setBindComplete(NioClientBindComplete bindComplete) {
		this.bindComplete = bindComplete;
	}
}