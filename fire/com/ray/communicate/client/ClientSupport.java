package com.ray.communicate.client;

import java.net.SocketAddress;

import javax.net.ssl.SSLContext;

import org.apache.mina.core.filterchain.IoFilter;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.service.IoHandler;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.filter.logging.MdcInjectionFilter;
import org.apache.mina.filter.ssl.SslFilter;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

import com.ray.communicate.message.IoMessage;
import com.ray.communicate.server.logic.FireProtocolCodecFactory;
import com.ray.communicate.server.ssl.BogusSslContextFactory;

/**
 * A simple chat client for a given user.
 *
 * @author <a href="http://mina.apache.org">Apache MINA Project</a>
 */
public class ClientSupport {
    private final IoHandler handler;

    private final String name;

    private IoSession session;

    public ClientSupport(String name, IoHandler handler) {
        if (name == null) {
            throw new IllegalArgumentException("Name can not be null");
        }
        this.name = name;
        this.handler = handler;
    }

    public boolean connect(NioSocketConnector connector, SocketAddress address,
            boolean useSsl) {
        if (session != null && session.isConnected()) {
            throw new IllegalStateException(
                    "Already connected. Disconnect first.");
        }

        try {
            IoFilter LOGGING_FILTER = new LoggingFilter();

            IoFilter CODEC_FILTER = new ProtocolCodecFilter(new FireProtocolCodecFactory());
            
            connector.getFilterChain().addLast("mdc", new MdcInjectionFilter());
            connector.getFilterChain().addLast("codec", CODEC_FILTER);
//            connector.getFilterChain().addLast("logger", LOGGING_FILTER);

            if (useSsl) {
                SSLContext sslContext = BogusSslContextFactory.getInstance(false);
                SslFilter sslFilter = new SslFilter(sslContext);
                sslFilter.setUseClientMode(true);
                connector.getFilterChain().addFirst("sslFilter", sslFilter);
            }

            connector.setHandler(handler);
            ConnectFuture future1 = connector.connect(address);
            future1.awaitUninterruptibly();
            if (!future1.isConnected()) {
                return false;
            }
            session = future1.getSession();

            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * 向服务器发送消息
     * @param msg
     */
    public void send(IoMessage msg){
    	session.write(msg);
    }
    
    public void send(byte[] datas){
    	session.write(datas);
    }

//    public void login() {
//        session.write("LOGIN " + name);
//    }

//    public void broadcast(String message) {
//        session.write("BROADCAST " + message);
//    }

    public void quit() {
        if (session != null) {
//            if (session.isConnected()) {
////                session.write("QUIT");
//                // Wait until the chat ends.
//                session.getCloseFuture().awaitUninterruptibly();
//            }
            session.close(true);
        }
    }
}