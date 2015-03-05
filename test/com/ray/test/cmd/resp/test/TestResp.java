package com.ray.test.cmd.resp.test;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.google.protobuf.Message;
import com.ray.communicate.message.IoHeader;
import com.ray.communicate.message.IoMessage;
import com.ray.communicate.server.bean.IoConnection;
import com.ray.message.java.client.test.TestMsgProto.TestMsg;
import com.ray.test.bean.Player;
import com.ray.test.cmd.resp.AbstractTcpResponse;
import com.ray.utils.util.Log;

public class TestResp extends AbstractTcpResponse {
	private Map<IoConnection, Integer> lastReceivedIds = new ConcurrentHashMap<IoConnection, Integer>();
//	private StringBuilder history = new StringBuilder();
	
	@Override
	protected int done(Player player, IoConnection ioUser, Message message, IoHeader sourceHeader) throws Exception {
		
		TestMsg msg = (TestMsg)message;
		Integer lastReceivedId = lastReceivedIds.get(ioUser);
		if(lastReceivedId == null){
			lastReceivedId = 1;//id从1开始顺序接收到
			lastReceivedIds.put(ioUser, lastReceivedId);
		}
//		history.append(msg.getId()).append(", ");
		StringBuilder info = new StringBuilder();
		info.append("uid=").append(sourceHeader.getUid()).append(", id=").append(msg.getId()).
		append(", lastReceivedId=").append(lastReceivedId).append(", cost=").
		append(System.currentTimeMillis()/1000-msg.getTime());
		Log.info(info.toString());
		if(msg.getId() != lastReceivedId){
//			Log.info("history=" + history.toString());
			throw new RuntimeException("接收顺序错乱: " + info.toString());
		}
		lastReceivedId++;
		lastReceivedIds.put(ioUser, lastReceivedId);
		return 0;
	}

	@Override
	protected Message parseMessage(IoMessage message) throws Exception {
		return TestMsg.parseFrom(message.getBody());
	}

}
