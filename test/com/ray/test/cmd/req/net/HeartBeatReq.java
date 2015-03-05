package com.ray.test.cmd.req.net;

import com.google.protobuf.Message;
import com.ray.communicate.message.IoHeader;
import com.ray.communicate.message.IoMessage;
import com.ray.communicate.server.bean.IoConnection;
import com.ray.message.java.client.net.HeartBeatMsgProto.HeartBeatMsg;
import com.ray.test.bean.Player;
import com.ray.test.cmd.req.AbstractTcpRequest;
import com.ray.utils.util.Log;

public class HeartBeatReq extends AbstractTcpRequest {

	@Override
	protected int done(Player player, IoConnection ioUser, Message message, IoHeader sourceHeader) throws Exception {
		HeartBeatMsg msg = (HeartBeatMsg)message;
		Log.debug("HeartBeatReq: " + msg.getServerTime());
		
//		返回
		HeartBeatMsg.Builder builder = HeartBeatMsg.newBuilder();
		builder.setServerTime((int)(System.currentTimeMillis() / 1000));
		response(ioUser, sourceHeader, builder.build().toByteArray());
		return 0;
	}

	@Override
	protected Message parseMessage(IoMessage message) throws Exception {
		return HeartBeatMsg.parseFrom(message.getBody());
	}

}
