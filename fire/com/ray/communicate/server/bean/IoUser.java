package com.ray.communicate.server.bean;

import java.util.Set;

import org.apache.mina.core.session.IoSession;

import com.ray.communicate.message.IoMessage;

public class IoUser {
	private int id;
	private IoSession session;//此session不一定与玩家连接一一对应，可能是个gate连接
	private IoUserAttributeMap attributes;
	
	private IoUser(){}
	public IoUser(int id, IoSession session){
		this();
		this.id = id;
		this.session = session;
		attributes = new IoUserAttributeMap();
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
	public IoSession getSession() {
		return session;
	}
	
	
	public final Object getAttribute(Object key) {
        return getAttribute(key, null);
    }

    /**
     * {@inheritDoc}
     */
    public final Object getAttribute(Object key, Object defaultValue) {
        return attributes.getAttribute(this, key, defaultValue);
    }

    /**
     * {@inheritDoc}
     */
    public final Object setAttribute(Object key, Object value) {
        return attributes.setAttribute(this, key, value);
    }

    /**
     * {@inheritDoc}
     */
    public final Object setAttribute(Object key) {
        return setAttribute(key, Boolean.TRUE);
    }

    /**
     * {@inheritDoc}
     */
    public final Object setAttributeIfAbsent(Object key, Object value) {
        return attributes.setAttributeIfAbsent(this, key, value);
    }

    /**
     * {@inheritDoc}
     */
    public final Object setAttributeIfAbsent(Object key) {
        return setAttributeIfAbsent(key, Boolean.TRUE);
    }

    /**
     * {@inheritDoc}
     */
    public final Object removeAttribute(Object key) {
        return attributes.removeAttribute(this, key);
    }

    /**
     * {@inheritDoc}
     */
    public final boolean removeAttribute(Object key, Object value) {
        return attributes.removeAttribute(this, key, value);
    }

    /**
     * {@inheritDoc}
     */
    public final boolean replaceAttribute(Object key, Object oldValue, Object newValue) {
        return attributes.replaceAttribute(this, key, oldValue, newValue);
    }

    /**
     * {@inheritDoc}
     */
    public final boolean containsAttribute(Object key) {
        return attributes.containsAttribute(this, key);
    }

    /**
     * {@inheritDoc}
     */
    public final Set<Object> getAttributeKeys() {
        return attributes.getAttributeKeys(this);
    }

    /**
     * TODO Add method documentation
     */
    public final IoUserAttributeMap getAttributeMap() {
        return attributes;
    }

    /**
     * TODO Add method documentation
     */
    public final void setAttributeMap(IoUserAttributeMap attributes) {
        this.attributes = attributes;
    }
}
