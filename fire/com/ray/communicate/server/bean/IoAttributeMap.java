package com.ray.communicate.server.bean;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class IoAttributeMap<T> {
	private final ConcurrentHashMap<Object, Object> attributes = new ConcurrentHashMap<Object, Object>(4);

    /**
     * {@inheritDoc}
     */
    public Object getAttribute(T obj, Object key, Object defaultValue) {
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
    public Object setAttribute(T obj, Object key, Object value) {
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
    public Object setAttributeIfAbsent(T obj, Object key, Object value) {
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
    public Object removeAttribute(T obj, Object key) {
        if (key == null) {
            throw new IllegalArgumentException("key");
        }

        return attributes.remove(key);
    }

    /**
     * {@inheritDoc}
     */
    public boolean removeAttribute(T obj, Object key, Object value) {
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
    public boolean replaceAttribute(T obj, Object key, Object oldValue, Object newValue) {
        try {
            return attributes.replace(key, oldValue, newValue);
        } catch (NullPointerException e) {
        }

        return false;
    }

    /**
     * {@inheritDoc}
     */
    public boolean containsAttribute(T obj, Object key) {
        return attributes.containsKey(key);
    }

    /**
     * {@inheritDoc}
     */
    public Set<Object> getAttributeKeys(T obj) {
        synchronized (attributes) {
            return new HashSet<Object>(attributes.keySet());
        }
    }

    /**
     * {@inheritDoc}
     */
    public void dispose(T obj) throws Exception {
        // Do nothing
    }
}
