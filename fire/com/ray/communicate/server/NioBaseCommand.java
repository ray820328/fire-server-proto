package com.ray.communicate.server;

import com.ray.communicate.message.IoHeader;
import com.ray.communicate.message.IoMessage;
import com.ray.communicate.server.bean.IoConnection;
import com.ray.fire.util.Log;

public abstract class NioBaseCommand implements BaseCommand {
	
	public void execute(IoConnection user, Object message)
			throws Exception {
		exec(user, (IoMessage)message);
	}
	
	private void exec(IoConnection user, IoMessage message) throws Exception{
		try{
			StringBuffer logbf = new StringBuffer();
			long now = System.currentTimeMillis();
			IoHeader header = message.getHeader();
			logbf.append(header.getUid()).append("|").append(this.getClass().getSimpleName() +"|"+header.getCmd()).append("|").append(header.getCode());
//			Response response = new Response();
//			response.setHeader((IoHeader)header.clone());
			int code = done(user, message);
//			response.setCode(code);
			doneComplete(user, message);
			long useTime = System.currentTimeMillis() - now;
			logbf.append("|").append(code).append("|").append(useTime);
			if(useTime >= 0){//debug
//				if(header.getCmd() != 601){//心跳|普通打坐|挂机时间奖励
//					&& header.getCmd()!=14402 && header.getCmd()!=-21004
					Log.info(logbf.toString());
//				}
			}
		}catch(Exception e){
			Log.error(e);
			throw e;
		}
	}
	
	protected abstract int done(IoConnection user, IoMessage message) throws Exception;
	
	
	protected void doneComplete(IoConnection user, IoMessage message){
//		session.write(response.toIoMessage());
	}
}
