package com.ray.communicate.server.bean;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class IoUserAttributeMap {
	private final ConcurrentHashMap<Object, Object> attributes = new ConcurrentHashMap<Object, Object>(4);

    /**
     * {@inheritDoc}
     */
    public Object getAttribute(IoUser ioUser, Object key, Object defaultValue) {
        if (key == null) {
            throw new IllegalArgumentException("key");
        }

        if (defaultValue == null) {
            return attributes.get(key);
        }

        Object object = attributes.putIfAbsent(key, defaultValue);

        if (object == null) {
            return defaultValue;
        } else {
            return object;
        }
    }

    /**
     * {@inheritDoc}
     */
    public Object setAttribute(IoUser ioUser, Object key, Object value) {
        if (key == null) {
            throw new IllegalArgumentException("key");
        }

        if (value == null) {
            return attributes.remove(key);
        }

        return attributes.put(key, value);
    }

    /**
     * {@inheritDoc}
     */
    public Object setAttributeIfAbsent(IoUser ioUser, Object key, Object value) {
        if (key == null) {
            throw new IllegalArgumentException("key");
        }

        if (value == null) {
            return null;
        }

        return attributes.putIfAbsent(key, value);
    }

    /**
     * {@inheritDoc}
     */
    public Object removeAttribute(IoUser ioUser, Object key) {
        if (key == null) {
            throw new IllegalArgumentException("key");
        }

        return attributes.remove(key);
    }

    /**
     * {@inheritDoc}
     */
    public boolean removeAttribute(IoUser ioUser, Object key, Object value) {
        if (key == null) {
            throw new IllegalArgumentException("key");
        }

        if (value == null) {
            return false;
        }

        try {
            return attributes.remove(key, value);
        } catch (NullPointerException e) {
            return false;
        }
    }

    /**
     * {@inheritDoc}
     */
    public boolean replaceAttribute(IoUser ioUser, Object key, Object oldValue, Object newValue) {
        try {
            return attributes.replace(key, oldValue, newValue);
        } catch (NullPointerException e) {
        }

        return false;
    }

    /**
     * {@inheritDoc}
     */
    public boolean containsAttribute(IoUser ioUser, Object key) {
        return attributes.containsKey(key);
    }

    /**
     * {@inheritDoc}
     */
    public Set<Object> getAttributeKeys(IoUser ioUser) {
        synchronized (attributes) {
            return new HashSet<Object>(attributes.keySet());
        }
    }

    /**
     * {@inheritDoc}
     */
    public void dispose(IoUser ioUser) throws Exception {
        // Do nothing
    }
}
