package com.ray.communicate.util;

import java.util.Set;

import com.google.protobuf.ByteString;
import com.google.protobuf.Message;
import com.ray.communicate.message.IoHeader;
import com.ray.communicate.message.IoMessage;

public class IoMessageUtil {
	public static IoMessage build(int sid, int cmd, Message message){
		IoMessage ioMessage = new IoMessage();
		IoHeader header = new IoHeader();
		header.setSid(sid);
		header.setCmd(cmd);
		ioMessage.setHeader(header);
		if(message != null){
			ioMessage.setBody(message.toByteArray());
		}else{
			ioMessage.setBody(new byte[0]);
		}
		ioMessage.prepare();
		return ioMessage;
	}
	
	public static IoMessage build(int sid, int cmd){
		IoMessage ioMessage = new IoMessage();
		IoHeader header = new IoHeader();
		header.setSid(sid);
		header.setCmd(cmd);
		ioMessage.setHeader(header);
		return ioMessage;
	}
	
//	public static IoMessage parseClientMsg(IoHeader header, ForwardMsg forwardMsg) throws Exception{
//		IoMessage clientMsg = new IoMessage();
//		clientMsg.setHeader(header);
//		clientMsg.setBody(forwardMsg.getBody().toByteArray());
//		return clientMsg;
//	}
//	
//	public static IoMessage parseFowardMsg(Set<Integer> ids, IoMessage message){
//		ForwardMsg.Builder fbuidler = ForwardMsg.newBuilder();
//		fbuidler.addAllUids(ids);
//		fbuidler.setBody(ByteString.copyFrom(message.getBody()));
//		IoMessage result = new IoMessage();
//		result.setHeader((IoHeader)message.getHeader().clone());
//		result.setBody(fbuidler.build().toByteArray());
//		return result;
//	}
//	
//	public static IoMessage parseFowardMsg(int uid, IoMessage message){
//		ForwardMsg.Builder fbuidler = ForwardMsg.newBuilder();
//		fbuidler.addUids(uid);
//		fbuidler.setBody(ByteString.copyFrom(message.getBody()));
//		IoMessage result = new IoMessage();
//		result.setHeader((IoHeader)message.getHeader().clone());
//		result.setBody(fbuidler.build().toByteArray());
//		return result;
//	}
//	
//	public static IoMessage parseFowardMsgBySid(long sid, Set<Integer> otherUids, IoMessage message){
//		ForwardMsg.Builder fbuidler = ForwardMsg.newBuilder();
//		fbuidler.setSid(sid);
//		if(otherUids != null){
//			fbuidler.addAllUids(otherUids);
//		}
//		fbuidler.setBody(ByteString.copyFrom(message.getBody()));
//		IoMessage result = new IoMessage();
//		result.setHeader((IoHeader)message.getHeader().clone());
//		result.setBody(fbuidler.build().toByteArray());
//		return result;
//	}
	
}
