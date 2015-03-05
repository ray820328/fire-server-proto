package com.ray.server;
/**
 * <p></p>
 * <p>Copyright (c) 2007 Sample King</p>
 * @author Ray
 * @version v1.0
 * <p>Last update (2011-8-10)</p>
 */
import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.mina.core.filterchain.DefaultIoFilterChainBuilder;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.executor.ExecutorFilter;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.filter.ssl.SslFilter;
import org.apache.mina.transport.socket.SocketSessionConfig;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;

import com.ray.communicate.server.logic.FireProtocolCodecFactory;
import com.ray.communicate.server.ssl.BogusSslContextFactory;
import com.ray.server.logic.CommandCache;
import com.ray.server.logic.FireNioHandler;
import com.ray.server.logic.UserOrderedThreadPoolExecutor;
import com.ray.utils.util.Log;
import com.ray.utils.util.ThreadManager.FireDefaultThreadFactory;

public class TestServer {

	private int port;
	private boolean useSSL = false;
	private ExecutorService executor;
	
	public void start(int port){
		this.port = port;
		try{
			
			NioSocketAcceptor acceptor = new NioSocketAcceptor();
			DefaultIoFilterChainBuilder chain = acceptor.getFilterChain();
			
//			MdcInjectionFilter mdcInjectionFilter = new MdcInjectionFilter();
//	        chain.addLast("mdc", mdcInjectionFilter);

	        // Add SSL filter if SSL is enabled.
	        if (useSSL) {
	            addSSLSupport(chain);
	        }
			
//			编码解码
			chain.addLast("codec", new ProtocolCodecFilter(new FireProtocolCodecFactory()));
////			日志信息
//			addLogger(chain);
			executor = new UserOrderedThreadPoolExecutor(4, 100, 
		            10, TimeUnit.SECONDS, new FireDefaultThreadFactory("logic"), null);//[10,200,5=1h=01]
//			executor = new ScheduledThreadPoolExecutor(4);//2 * Runtime.getRuntime().availableProcessors());
			chain.addLast("executorPool", new ExecutorFilter(executor));
			 
//			处理
			acceptor.setHandler(new FireNioHandler());
			
			SocketSessionConfig sconfig = acceptor.getSessionConfig();
			sconfig.setTcpNoDelay(true);
			sconfig.setReaderIdleTime(100);
			
//			打开端口
			acceptor.bind(new InetSocketAddress(getPort()));
			Log.info("Bind at " + getPort());
		}catch(Exception e){
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	/**
	 * SSL支持
	 * @param chain
	 * @throws Exception
	 */
    private void addSSLSupport(DefaultIoFilterChainBuilder chain)
            throws Exception {
        SslFilter sslFilter = new SslFilter(BogusSslContextFactory.getInstance(true));
        chain.addLast("sslFilter", sslFilter);
        Log.info("SSL ON");
    }
    /**
     * 日志过滤器
     * @param chain
     * @throws Exception
     */
    private void addLogger(DefaultIoFilterChainBuilder chain)
            throws Exception {
        chain.addLast("logger", new LoggingFilter());
    }
    
    /**
     * 初始化环境
     */
    private void init(String conf)throws Exception{
    	CommandCache.loadCommand(conf);
    }
    
	public int getPort(){
		return port;
	}
	
	public void stopListerning(){
		
	}
	
	public boolean isUseSSL() {
		return useSSL;
	}
	public void setUseSSL(boolean useSSL) {
		this.useSSL = useSSL;
	}

	public static void main(String[] args){
		final TestServer server = new TestServer();
		try{
			server.init("config/command_request.properties");
			server.start(13000);
		}catch(Exception ex){
			ex.printStackTrace();
		}
		
//		Thread statisticThread = new Thread(){
//			public void run(){
//				while(true){
//				
////				synchronized(server){
//					FSystem.gc();
//					try{
//						Thread.sleep(5000L);
//					}catch(Exception ex){
//						Log.error("FSystem.statistic error:", ex);
//					}
//					FSystem.trace();
////				}
//				}
//			}
//		};
//		statisticThread.setPriority(Thread.NORM_PRIORITY);
//		statisticThread.start();
	}
}
