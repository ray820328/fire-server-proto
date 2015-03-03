package com.ray.test;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.executor.ExecutorFilter;
import org.apache.mina.filter.executor.OrderedThreadPoolExecutor;

import com.ray.communicate.client.socket.NioClient;
import com.ray.communicate.client.socket.NioClientBindComplete;
import com.ray.communicate.message.IoHeader;
import com.ray.communicate.message.IoMessage;
import com.ray.communicate.server.logic.FireDecoder;
import com.ray.communicate.server.logic.FireEncoder;
import com.ray.fire.util.Log;
import com.ray.message.java.client.ClientCmdProto.ClientCmd;
import com.ray.message.java.client.test.TestMsgProto.TestMsg;
import com.ray.server.logic.CommandCache;

public class TestClient {

	private static AtomicInteger userIdGenerator = new AtomicInteger(0);
	
	public static void main(String[] args){
		int count = 1;
		Thread[] threads = new Thread[count];
		for (int i = 0; i < count; i++) {
			threads[i] = new Thread() {
				private AtomicInteger idGenerator = new AtomicInteger(0);
				
				public void run() {
					try{
						final int userId = userIdGenerator.incrementAndGet();
						NioHandlerClient nioHandler = new NioHandlerClient();
						NioClient client = new NioClient("config/log4j.properties", "config/client.properties", nioHandler, false);
						client.addFirstFilter("codec", new ProtocolCodecFilter(new FireEncoder(), new FireDecoder()));
//						client.addFirstFilter("executor", new ExecutorFilter(new ScheduledThreadPoolExecutor(2 * Runtime.getRuntime().availableProcessors())));
						client.addFirstFilter("executor", new ExecutorFilter(new OrderedThreadPoolExecutor(1, 1, 
					            10, TimeUnit.SECONDS, Executors.defaultThreadFactory(), null)));
								
						NioClientBindComplete bindComplete = new NioClientBindComplete(){
							public void complete(final NioClient cl){
								Log.debug("client.bindComplete.complete.");
								new Thread(new Runnable(){
									public void run(){
										while(true){
											try{//发送消息
//												HeartBeatMsg.Builder builder = HeartBeatMsg.newBuilder();
//												builder.setServerTime(0);
												for(int i=0; i<5; i++){
													TestMsg.Builder builder = TestMsg.newBuilder();
													builder.setId(idGenerator.incrementAndGet());
													builder.setTime((int)(System.currentTimeMillis()/1000));
													IoMessage req = new IoMessage();
													IoHeader header = new IoHeader();
													header.setCmd(ClientCmd.TEST_COMMUNICATION_VALUE);
													header.setUid(userId);
													header.setCode(builder.getId());
													req.setHeader(header);
													req.setBody(builder.build().toByteArray());
													req.prepare();
													cl.write(req);
												}

												Thread.sleep(1000);
											}catch(Exception e){
												e.printStackTrace();
											}
										}
									}
								}).start();
							}
							public void heartbeat(NioClient client){
								Log.debug("client.bindComplete.heartbeat.");
							}
						};
						client.setBindComplete(bindComplete);
						
			//			客户端响应命令
						CommandCache.loadCommand("config/command_response.properties");
						
						client.bind();
					}catch(Exception ex){
						ex.printStackTrace();
					}
				}
			};
			
			threads[i].start();
		}
	}
}
