package com.ray.communicate.client.socket;


public interface NioClientBindComplete {
	public void complete(NioClient client);
	public void heartbeat(NioClient client);
}
