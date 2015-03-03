package com.ray.test.cmd.resp;

import com.google.protobuf.Message;
import com.ray.communicate.message.IoHeader;
import com.ray.communicate.message.IoMessage;
import com.ray.communicate.server.NioBaseCommand;
import com.ray.communicate.server.bean.IoConnection;
import com.ray.message.java.SystemCodeProto.SystemCode;
import com.ray.message.java.client.ClientCmdProto.ClientCmd;
import com.ray.test.bean.Player;
import com.ray.test.service.PlayerService;

public abstract class AbstractTcpResponse extends NioBaseCommand {

	public AbstractTcpResponse(){
//		setCatcher(new ClientCmdCatcher());
	}
	
	@Override 
	protected int done(IoConnection user, IoMessage message) throws Exception {
		Player player = null;
		IoHeader header = message.getHeader();
		try{
			int code = 0;//HallServer.getGServer().isCloseId(header.getCmd());
			if(code == SystemCode.SYS_SUCCESS_VALUE){
//				String name = String.valueOf(message.getHeader().getCmd());
				code = done(player, user, parseMessage(message), message.getHeader());
				if(code == SystemCode.SYS_SUCCESS_VALUE){
					
				}
			}
			return code;
		}finally{
			
		}
	}
	
	protected void response(IoConnection ioUser, IoHeader sourceHeader, byte[] data){
		response(ioUser, sourceHeader, data, null);
	}
	protected void response(IoConnection ioUser, IoHeader sourceHeader, byte[] data, int[] uids){
		IoMessage res = new IoMessage();
		res.setHeader((IoHeader)sourceHeader.clone());
		res.setBody(data);
		res.prepare();
		if(uids != null){
			for(int uid : uids){
				res.addForwardId(uid);
			}
		}
		response(ioUser, res);
	}
	protected void response(IoConnection ioUser, IoMessage message){
		ioUser.write(message);
	}
	
	protected abstract int done(Player player, IoConnection ioUser, Message message, IoHeader sourceHeader) throws Exception;
	
	protected abstract Message parseMessage(IoMessage message) throws Exception;
	
}
