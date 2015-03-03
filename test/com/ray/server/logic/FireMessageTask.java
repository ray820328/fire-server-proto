package com.ray.server.logic;

import com.ray.communicate.message.IoMessage;
import com.ray.communicate.server.bean.IoUser;

public abstract class FireMessageTask implements Runnable {
	private IoMessage ioMessage;
	private IoUser ioUser;
	
	public FireMessageTask(IoUser ioUser, IoMessage ioMessage){
		if (ioUser == null) {
            throw new IllegalArgumentException("IoUser");
        }
        if (ioMessage == null) {
            throw new IllegalArgumentException("IoMessage");
        }
		this.ioMessage = ioMessage;
		this.ioUser = ioUser;
	}

	public IoMessage getIoMessage() {
		return ioMessage;
	}
	public void setIoMessage(IoMessage ioMessage) {
		this.ioMessage = ioMessage;
	}

	public IoUser getIoUser() {
		return ioUser;
	}
	public void setIoUser(IoUser ioUser) {
		this.ioUser = ioUser;
	}
}
