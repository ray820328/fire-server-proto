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
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;

import com.ray.communicate.message.IoHeader;
import com.ray.communicate.message.IoMessage;
import com.ray.fire.util.Log;

public class FireDecoder implements ProtocolDecoder {
	
	public final static String dc = "dccc";
	@Override
	public void decode(IoSession session, IoBuffer buffer, ProtocolDecoderOutput output)
			throws Exception {
//		synchronized(session){//不需要，已经在ProtocolCodecFilter中对session进行了数据保护
		IoBuffer bf = null;
		IoBuffer bfLeft = (IoBuffer)session.getAttribute(dc);
		if(bfLeft != null){
			bf = IoBuffer.allocate(buffer.remaining() + bfLeft.remaining());
			bf.put(bfLeft);
			bf.put(buffer);
		}else{
			bf = IoBuffer.allocate(buffer.remaining());
			bf.put(buffer);
		}
		bf.rewind();
		int position = bf.position();
		int blen = bf.remaining();
		while(blen >= IoHeader.header_length){
			IoMessage message = new IoMessage();
			message.readHeader(bf);
			IoHeader header = message.getHeader();
//			Log.info("Server Decoding:" + header.toString());
			if(header.getBodyLength()>0 && header.getBodyLength()<=IoHeader.header_max_length){
				if(header.getBodyLength() <= blen){
					message.readContent(bf);
					output.write(message);
				}else{
					bf.position(position);
					break;
				}
			}else{
				throw new RuntimeException("decoding: io data is max length!\n" + 
						"buff=" + bf.remaining() + "\n header=" +
						header.toString());
			}
			position = bf.position();
			blen = bf.remaining();
		}
		if(blen > 0){
			if(position > 0){
				IoBuffer bfTemp = IoBuffer.allocate(blen);
				bfTemp.put(bf);
				bfTemp.rewind();
				bf = bfTemp;
			}
			session.setAttribute(dc, bf);
		}else{
			session.removeAttribute(dc);
		}
//		}
	}

	@Override
	public void dispose(IoSession session) throws Exception {

	}

	@Override
	public void finishDecode(IoSession session, ProtocolDecoderOutput output)
			throws Exception {

	}
}