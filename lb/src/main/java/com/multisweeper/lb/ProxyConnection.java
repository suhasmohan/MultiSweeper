package com.multisweeper.lb;

import com.multisweeper.util.Logger;

import java.io.*;
import java.net.Socket;

public class ProxyConnection extends Thread {

	Socket clientSocket;
	Socket serverSocket;

	public ProxyConnection(Socket clientSocket, Socket serverSocket) {
		this.clientSocket = clientSocket;
		this.serverSocket = serverSocket;
	}

	@Override
	public void run() {
		try {
//			BufferedReader reader = new BufferedReader(new InputStreamReader(serverSocket.getInputStream()));
//			PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true);
			BufferedInputStream serverBufInStream = new BufferedInputStream(serverSocket.getInputStream());
			BufferedOutputStream clientBufOutStream = new BufferedOutputStream(clientSocket.getOutputStream());
			//char []buff = new char[69420];
			Logger.log("Starting proxy connection!");
			while (true) {

//				int ret = reader.read(buff,0, 69420);
//				if(ret != -1) {
//					Logger.log("Read data : " + new String(buff));
//					writer.write(buff);
//					writer.flush();
//					Logger.log("Sent data to server!");
//				} else {
//					serverSocket.close();
//					clientSocket.close();
//					return;
//				}

				int message = serverBufInStream.read();
				//System.out.println(message);
				if(message != -1)
				{
					clientBufOutStream.write(message);
					clientBufOutStream.flush();
				}
				else
				{
					serverSocket.close();
					clientSocket.close();
					return;
				}

			}

		} catch (IOException e) {
			try {
				System.out.println("IO Exception! " + e.getMessage());
				serverSocket.close();
				clientSocket.close();
				return;
			} catch (IOException e1) {
				return;
			}

		}

	}

}
