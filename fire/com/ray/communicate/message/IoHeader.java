package com.ray.communicate.message;

import org.apache.mina.core.buffer.IoBuffer;

import com.ray.utils.util.ValueUtil;

public class IoHeader implements Cloneable {
	public final static int message_begin = 0xffff;
	public final static int header_length = 32;
	public final static int header_max_length = 100 * 1024 * 1024;//Integer.MAX_VALUE;//最大100M
	private long sid = 0;//socketid
	private int uid = 0;//玩家id
	private int code = 0;//错误编码
	private int cmd = 0;//指令
	private int check = 0;//校验码
	private int bodyLength = 0;//长度
	
//	private int gateSessionId = 0;//实际连接通道id

	public IoHeader read(IoBuffer buffer){
		buffer.getInt();
		sid = buffer.getLong();
		uid = buffer.getInt();
		code = buffer.getInt();
		cmd = buffer.getInt();
		check = buffer.getInt();
		bodyLength = buffer.getInt();
		return this;
	}
	
	public IoBuffer write(IoBuffer buffer){
		buffer.putInt(message_begin);
		buffer.putLong(sid);
		buffer.putInt(uid);
		buffer.putInt(code);
		buffer.putInt(cmd);
		buffer.putInt(check);
		buffer.putInt(bodyLength);
		return buffer;
	}

	@Override
	public Object clone() {
		try{
			return super.clone();
		}catch(CloneNotSupportedException e){
			throw new RuntimeException(e);
		}
	}

	public long getSid() {
		return sid;
	}

	public void setSid(long sid) {
		this.sid = sid;
	}

	public int getUid() {
		return uid;
	}

	public void setUid(int uid) {
		this.uid = uid;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public int getCmd() {
		return cmd;
	}

	public void setCmd(int cmd) {
		this.cmd = cmd;
	}

	public int getCheck() {
		return check;
	}

	public void setCheck(int check) {
		this.check = check;
	}

	public int getBodyLength() {
		return bodyLength;
	}

	public void setBodyLength(int bodyLength) {
		this.bodyLength = bodyLength;
	}
	
	public String toString(){
		try{
			return this.getClass().getName() + ": " + ValueUtil.toJsonString(this, null);
		}catch(Exception ex){
			return super.toString();
		}
	}
}
