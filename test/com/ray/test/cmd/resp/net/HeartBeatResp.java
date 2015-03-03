package com.ray.test.cmd.resp.net;

import com.google.protobuf.Message;
import com.ray.communicate.message.IoHeader;
import com.ray.communicate.message.IoMessage;
import com.ray.communicate.server.bean.IoConnection;
import com.ray.fire.util.Log;
import com.ray.message.java.client.net.HeartBeatMsgProto.HeartBeatMsg;
import com.ray.test.bean.Player;
import com.ray.test.cmd.resp.AbstractTcpResponse;

public class HeartBeatResp extends AbstractTcpResponse {

	@Override
	protected int done(Player player, IoConnection ioUser, Message message, IoHeader sourceHeader) throws Exception {
		HeartBeatMsg msg = (HeartBeatMsg)message;
		Log.debug("serverTime: " + msg.getServerTime());
		return 0;
	}

	@Override
	protected Message parseMessage(IoMessage message) throws Exception {
		return HeartBeatMsg.parseFrom(message.getBody());
	}

}
