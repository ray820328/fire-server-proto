package com.ray.test.cmd.req;

import com.google.protobuf.Message;
import com.ray.communicate.message.IoHeader;
import com.ray.communicate.message.IoMessage;
import com.ray.communicate.server.NioBaseCommand;
import com.ray.communicate.server.bean.IoConnection;
import com.ray.message.java.SystemCodeProto.SystemCode;
import com.ray.message.java.client.ClientCmdProto.ClientCmd;
import com.ray.test.bean.Player;
import com.ray.test.service.PlayerService;

public abstract class AbstractTcpRequest extends NioBaseCommand {

	public AbstractTcpRequest(){
//		setCatcher(new ClientCmdCatcher());
	}
	
	@Override 
	protected int done(IoConnection user, IoMessage message) throws Exception {
		Player player = null;
		IoHeader header = message.getHeader();
		try{
			int code = 0;//HallServer.getGServer().isCloseId(header.getCmd());
			if(code == SystemCode.SYS_SUCCESS_VALUE){
				if(needLogin() && header.getCmd() != ClientCmd.PLAYER_CONNECT_VALUE 
						&& header.getCmd() != ClientCmd.PLAYER_REGISTER_VALUE){
					if(header.getUid() > 0){		
						player = PlayerService.getPlayer(header.getUid());
					}
					if(player==null && header.getCmd()!=ClientCmd.NET_HEART_BEAT_VALUE){
						return SystemCode.PLAYER_NO_LOGIN_VALUE;
					}else{
						if(player != null){
							player.setAccessTime(System.currentTimeMillis());
						}
					}
				}
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
	
	protected boolean needLogin(){
		return false;
	}
	
	protected abstract int done(Player player, IoConnection ioUser, Message message, IoHeader sourceHeader) throws Exception;
	
	protected abstract Message parseMessage(IoMessage message) throws Exception;
	
}
