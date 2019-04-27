package com.multisweeper.lb;

import com.multisweeper.failure.MinesweeperGroupFailureDetector;
import com.multisweeper.util.Logger;

import java.io.IOException;
import java.net.*;
import java.util.List;

public class LoadBalancer {

	int port;
	int currentServer = 0;


	public LoadBalancer(int port) {
		this.port = port;
	}

	public void startLB() {
		System.out.println("Starting the LB");

		ServerSocket socket;
		Socket clientSocket;

		try {
			System.out.println("Starting server socket on port " + port);
			socket = new ServerSocket(port);
			while (true) {
				// Wait and accept a connection
				clientSocket = socket.accept();
				Logger.log(String.format("Got new connection from %s:%d", clientSocket.getInetAddress().getHostAddress(), clientSocket.getPort()));
				List<String> ipAddrs = MinesweeperGroupFailureDetector.getAliveMemberAddrs();
				String serverIp = ipAddrs.get(currentServer % ipAddrs.size());
				Logger.log("Sending connection to " + serverIp);
				ConnectionHandler connectionHandler = new ConnectionHandler(clientSocket, serverIp);
				connectionHandler.startThreads();
				currentServer ++;
				if(currentServer > 65535)
					currentServer = 0;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
