package com.ray.communicate.server.bean;

import org.apache.mina.core.future.IoFutureListener;
import org.apache.mina.core.session.IoSession;

import com.ray.communicate.message.IoMessage;

public class IoConnection {
	private IoSession session;
	
	private IoConnection(){}
	public IoConnection(int id, IoSession session){
		this();
		this.session = session;
	}
	public void write(IoMessage message){
		session.write(message);
	}
	public void writeClose(IoMessage message){
		if(session!=null && !session.isClosing()){
			session.write(message).addListener(IoFutureListener.CLOSE);
		}
	}
	public IoSession getSession() {
		return session;
	}
}
