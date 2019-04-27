package com.multisweeper.lb;

import java.util.ArrayList;
import java.util.List;

public class ServerList {
	int currentServer = 0;

	List<Server> serverList = new ArrayList();

	public Server getNextServer() {
		Server ret = serverList.get(currentServer % serverList.size());
		currentServer++;
		return ret;
	}

	public void addServer(String hostname, int port) {

	}
}
