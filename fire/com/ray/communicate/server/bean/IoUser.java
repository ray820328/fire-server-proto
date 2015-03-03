package com.ray.communicate.server.bean;

import java.util.Set;

import org.apache.mina.core.session.IoSession;

import com.ray.communicate.message.IoMessage;

public class IoUser {
	private int id;
	private long sid;
	private IoSession session;//此session不一定与玩家连接一一对应，可能是个gate连接
	private IoAttributeMap attributes;
	
	private IoUser(){}
	public IoUser(int id, IoSession session){
		this();
		this.id = id;
		attributes = new IoAttributeMap();
		setSession(session);
	}
	public void write(IoMessage message){
		session.write(message);
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public long getSid() {
		return sid;
	}
	public void setSession(IoSession session) {
		this.session = session;
		if(session != null){
			this.sid = session.getId();
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
