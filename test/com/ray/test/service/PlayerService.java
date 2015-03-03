package com.ray.test.service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.ray.test.bean.Player;

public class PlayerService {
	private static Map<Long, Player> playerCache = new ConcurrentHashMap<Long, Player>();

	public static Player getPlayer(int id){
		return getPlayer(Long.valueOf(id));
	}
	public static Player getPlayer(long id){
		Player item = null;
		if(item == null){
			item = new Player();
			item.setId(id);
			playerCache.put(id, item);
		}
		return item;
	}
}
