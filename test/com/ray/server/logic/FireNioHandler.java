package com.ray.server.logic;
/**
 * <p></p>
 * <p>Copyright (c) 2007 Sample King</p>
 * @author Ray
 * @version v1.0
 * <p>Last update (2011-8-10)</p>
 */
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;

import com.ray.communicate.message.IoHeader;
import com.ray.communicate.message.IoMessage;
import com.ray.communicate.server.NioBaseCommand;
import com.ray.communicate.server.bean.IoConnection;
import com.ray.communicate.server.bean.IoUser;
import com.ray.fire.util.Log;

public class FireNioHandler extends IoHandlerAdapter {

	public static FireNioHandler instance = null;
	private final Map<Long, IoConnection> connections = new ConcurrentHashMap<Long, IoConnection>();
	//必须是account id，且账号验证不在此逻辑里
	private final Map<Integer, IoUser> ioUsers = new ConcurrentHashMap<Integer, IoUser>();
	private final UserOrderedThreadPoolExecutor executor = new UserOrderedThreadPoolExecutor(10, 100, 
            1, TimeUnit.MINUTES, Executors.defaultThreadFactory(), null);
	
	public FireNioHandler(){
		instance = this;
	}
	
	@Override
	public void messageReceived(final IoSession session, Object object)throws Exception{
//		FSystem.message_recived++;
		final IoMessage msg = (IoMessage)object;
		Log.debug(this.getClass().getName() + " Server Recived:" + msg.toString());
		
		IoHeader header = msg.getHeader();
		final NioBaseCommand command = CommandCache.getCommand(header.getCmd());
		final IoConnection connection = connections.get(session.getId());
		IoUser ioUser = ioUsers.get(header.getUid());
		if(header.getUid()>0 && ioUser==null){//!ioUsers.containsKey(header.getUid())){//需要按user顺序执行
			ioUser = new IoUser(header.getUid(), session);
			ioUsers.put(ioUser.getId(), ioUser);
		}
		FireMessageTask task = new FireMessageTask(ioUser, msg){
			public void run(){
				{//除非按session顺序执行，session已经与玩家一一对应，否则必须按user消息顺序执行
					try{
						command.execute(connection, msg);
					}catch(Exception ex){
						Log.error("MessageExecuting", ex);
					}
				}
			}
		};
		
		executor.execute(task);
	}
	
	@Override
	public void messageSent(IoSession session, Object message) throws Exception {
		IoMessage msg = (IoMessage)message;
		Log.info("Server Sent: " + msg.getHeader().toString());
	}
	
	@Override
	public void sessionClosed(IoSession session) throws Exception {
		// TODO Auto-generated method stub
		Log.debug("sessionClosed: session.id=" + session.getId());
		super.sessionClosed(session);
		connections.remove(session.getId());
	}

	@Override
	public void sessionCreated(IoSession session) throws Exception {
		Log.debug("sessionCreated: session.id=" + session.getId());
		super.sessionCreated(session);
	}

	@Override
	public void sessionIdle(IoSession session, IdleStatus status)
			throws Exception {
		super.sessionIdle(session, status);
		if(status == IdleStatus.READER_IDLE){
			session.close(true);
		}
	}

	@Override
	public void sessionOpened(IoSession session) throws Exception {
		Log.debug("sessionOpened: session.id=" + session.getId());
		super.sessionOpened(session);
		IoConnection ioUser = new IoConnection(0, session);
		connections.put(session.getId(), ioUser);
	}
	 
	@Override
	public void exceptionCaught(IoSession session, Throwable cause)throws Exception{
//		cause.printStackTrace();
		Log.error(cause);
//		super.exceptionCaught(session, cause);
        // Close connection when unexpected exception is caught.
        session.close(true);
	}
//	
//	public void broadcast(IoMessage message) {
//		FSystem.message_sent++;
//        synchronized (sessions) {
//            for (IoSession session : sessions) {
//                if (session.isConnected()) {
//                    session.write(message);
//                }
//            }
//        }
//    }

	public Map<Long, IoConnection> getConnections() {
		return connections;
	}
	public Map<Integer, IoUser> getIoUsers() {
		return ioUsers;
	}
}
