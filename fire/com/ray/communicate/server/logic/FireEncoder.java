package com.ray.communicate.server.logic;
/**
 * <p></p>
 * <p>Copyright (c) 2007 Sample King</p>
 * @author Ray
 * @version v1.0
 * <p>Last update (2011-8-10)</p>
 */
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoderAdapter;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;

import com.ray.communicate.message.IoMessage;

public class FireEncoder extends ProtocolEncoderAdapter {
    
    public FireEncoder(){
    	
    }
    
	public void encode(IoSession session, Object object,
            ProtocolEncoderOutput out) throws Exception {
		try{
			synchronized (session) {
				IoMessage message = (IoMessage)object;
				if(!message.isSerialized()){
					message.prepare();
				}
				IoBuffer buffer = IoBuffer.allocate(message.getLen());
				message.write(buffer);
				buffer.flip();
				if(session != null && !session.isClosing()){
					out.write(buffer);
				}
			}
		}catch(Exception e){
			throw e;
		}
	}
}