package com.ray.communicate.server.bean;

import java.util.Set;

import org.apache.mina.core.future.IoFutureListener;
import org.apache.mina.core.session.IoSession;

import com.ray.communicate.message.IoMessage;

public class IoConnection {
	private IoSession session;
	private IoAttributeMap attributes;
	
	private IoConnection(){
		attributes = new IoAttributeMap();
	}
	public IoConnection(long id, IoSession session){
		this();
		this.session = session;
	}
	public void write(IoMessage message){
		session.write(message);
	}
	public void writeClose(IoMessage message){
		if(session!=null && !session.isClosing()){
			session.write(message).addListener(IoFutureListener.CLOSE);
		}
	}
	public IoSession getSession() {
		return session;
	}
	
	public final Object getAttribute(Object key) {
        return getAttribute(key, null);
    }

    public final Object getAttribute(Object key, Object defaultValue) {
        return attributes.getAttribute(this, key, defaultValue);
    }

    public final Object setAttribute(Object key, Object value) {
        return attributes.setAttribute(this, key, value);
    }

    public final Object setAttribute(Object key) {
        return setAttribute(key, Boolean.TRUE);
    }

    public final Object setAttributeIfAbsent(Object key, Object value) {
        return attributes.setAttributeIfAbsent(this, key, value);
    }

    public final Object setAttributeIfAbsent(Object key) {
        return setAttributeIfAbsent(key, Boolean.TRUE);
    }

    public final Object removeAttribute(Object key) {
        return attributes.removeAttribute(this, key);
    }

    public final boolean removeAttribute(Object key, Object value) {
        return attributes.removeAttribute(this, key, value);
    }

    public final boolean replaceAttribute(Object key, Object oldValue, Object newValue) {
        return attributes.replaceAttribute(this, key, oldValue, newValue);
    }

    public final boolean containsAttribute(Object key) {
        return attributes.containsAttribute(this, key);
    }

    public final Set<Object> getAttributeKeys() {
        return attributes.getAttributeKeys(this);
    }

    public final IoAttributeMap getAttributeMap() {
        return attributes;
    }

    public final void setAttributeMap(IoAttributeMap attributes) {
        this.attributes = attributes;
    }
}
