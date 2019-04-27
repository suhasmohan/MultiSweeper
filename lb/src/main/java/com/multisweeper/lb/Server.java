package com.multisweeper.lb;

public class Server {
	int port;
	String hostname;

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getHostname() {
		return hostname;
	}

	public void setHostname(String hostname) {
		this.hostname = hostname;
	}

	public Server(int port, String hostname) {
		this.port = port;
		this.hostname = hostname;
	}
}
