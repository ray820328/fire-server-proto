package com.ray.communicate.server.bean;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class IoUserCache<T extends IoUser> {
	private final Map<Object, T> cache = new ConcurrentHashMap<Object, T>();
	
	@SuppressWarnings("unchecked")
	public void addUser(IoUser user){
		cache.put(user.getId(), (T)user);
	}
	
	@SuppressWarnings("unchecked")
	public void putUser(Object key, IoUser user){
		cache.put(key, (T)user);
	}
	
	public T removeUser(Object key){
		return cache.remove(key);
	}
	
	public T getUser(Object key){
		return cache.get(key);
	}
	
	public Collection<T> values(){
		return Collections.unmodifiableCollection(cache.values());
	}
	
	public boolean isEmpty(){
		return cache.isEmpty();
	}
}
