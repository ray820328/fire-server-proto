package com.ray.test.cmd.req.test;

import com.google.protobuf.Message;
import com.ray.communicate.message.IoHeader;
import com.ray.communicate.message.IoMessage;
import com.ray.communicate.server.bean.IoConnection;
import com.ray.message.java.client.test.TestMsgProto.TestMsg;
import com.ray.test.bean.Player;
import com.ray.test.cmd.req.AbstractTcpRequest;
import com.ray.utils.util.Log;

public class TestReq extends AbstractTcpRequest {

	@Override
	protected int done(Player player, IoConnection ioUser, Message message, IoHeader sourceHeader) throws Exception {
		TestMsg msg = (TestMsg)message;
		
		double sleepTime = Math.random() * 20 * 10;
		Log.info(msg.getClass().getSimpleName() + ": uid=" + sourceHeader.getUid() + 
				", mid=" + msg.getId() + ", sleepTime=" + (int)sleepTime);
//		返回
		sourceHeader.setCode(msg.getId());//
		TestMsg.Builder builder = TestMsg.newBuilder();
		builder.setId(msg.getId());//id一样
		builder.setTime(msg.getTime());//时间一样
		response(ioUser, sourceHeader, builder.build().toByteArray());
		
		Thread.sleep((int)sleepTime);//随机x毫秒
		return 0;
	}

	@Override
	protected Message parseMessage(IoMessage message) throws Exception {
		return TestMsg.parseFrom(message.getBody());
	}

}
