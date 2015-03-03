package com.ray.test;
/**
 * <p></p>
 * <p>Copyright (c) 2007 Sample King</p>
 * @author Ray
 * @version v1.0
 * <p>Last update (2011-8-10)</p>
 */
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;

import com.ray.communicate.message.IoHeader;
import com.ray.communicate.message.IoMessage;
import com.ray.communicate.server.NioBaseCommand;
import com.ray.communicate.server.bean.IoConnection;
import com.ray.fire.util.Log;
import com.ray.server.logic.CommandCache;

public class NioHandlerClient extends IoHandlerAdapter {

	private IoConnection ioUser = null;
	
	@Override
	public void messageReceived(IoSession session, Object object)throws Exception{
//		FSystem.message_recived++;
		IoMessage msg = (IoMessage)object;
		Log.debug(this.getClass().getName() + " Client Recived:" + msg.toString());
		synchronized(session){//已经同步线程了，在read的时候
			try{
				IoHeader header = msg.getHeader();
				NioBaseCommand command = CommandCache.getCommand(header.getCmd());
				command.execute(ioUser, msg);
			}catch(Exception ex){
				Log.error("NioHandlerClient.messageReceived", ex);
			}
		}
	}
	
	@Override
	public void messageSent(IoSession session, Object message) throws Exception {
		IoMessage msg = (IoMessage)message;
		Log.info("Client Sent: " + msg.getHeader().toString());
	}
	
	@Override
	public void sessionClosed(IoSession session) throws Exception {
		// TODO Auto-generated method stub
		Log.debug("sessionClosed: session.id=" + session.getId());
		super.sessionClosed(session);
		ioUser = null;
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
		ioUser = new IoConnection(0, session);
	}
	 
	@Override
	public void exceptionCaught(IoSession session, Throwable cause)throws Exception{
//		cause.printStackTrace();
		Log.error(cause);
//		super.exceptionCaught(session, cause);
        // Close connection when unexpected exception is caught.
        session.close(true);
	}
}
