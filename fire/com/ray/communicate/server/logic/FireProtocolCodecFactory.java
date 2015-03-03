package com.ray.communicate.server.logic;
/**
 * <p></p>
 * <p>Copyright (c) 2007 Sample King</p>
 * @author Ray
 * @version v1.0
 * <p>Last update (2011-8-10)</p>
 */
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolEncoder;

public class FireProtocolCodecFactory implements ProtocolCodecFactory {

	private final FireEncoder encoder;
	private final FireDecoder decoder;
	
	public FireProtocolCodecFactory(){
		encoder = new FireEncoder();
		decoder = new FireDecoder();
	}
	
	public ProtocolEncoder getEncoder(IoSession session) {
        return encoder;
    }

    public ProtocolDecoder getDecoder(IoSession session) {
        return decoder;
    }
}
