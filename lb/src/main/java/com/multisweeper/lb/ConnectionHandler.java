package com.multisweeper.lb;

import com.multisweeper.util.Logger;

import java.io.IOException;
import java.net.Socket;

public class ConnectionHandler {

	Socket clientSocket;
	String serverIP;

	public ConnectionHandler(Socket clientSocket, String serverIP) {
		this.clientSocket = clientSocket;
		this.serverIP = serverIP;
	}


	public void startThreads() {
		try {
			Socket serverSocket = new Socket(serverIP, Constants.LISTENER_PORT);
			Logger.log("Connected to server " + serverIP);
			Thread clientThread = new ProxyConnection(clientSocket, serverSocket);
			Thread serverThread = new ProxyConnection(serverSocket, clientSocket);
			clientThread.start();
			serverThread.start();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
