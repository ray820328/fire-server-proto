package com.ray.communicate.message;

import org.apache.mina.core.buffer.IoBuffer;

public class IoContent {
	private byte[] body;
	public IoContent(){
		
	}
	public IoContent(byte[] body){
		this.body = body;
	}
	public byte[] getBody() {
		return body;
	}
	public void setBody(byte[] body) {
		this.body = body;
	}
	public IoContent read(IoBuffer buffer, int len){
		body = new byte[len];
		buffer.get(body);
		return this;
	}
	public IoBuffer write(IoBuffer buffer){
		buffer.put(body);
		return buffer;
	}
	public int getLen(){
		return body.length;
	}
}
