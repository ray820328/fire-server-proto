package com.ray.communicate.server;

import com.ray.communicate.server.bean.IoConnection;

public interface BaseCommand {
	public void execute(IoConnection user, Object message) throws Exception;
}
