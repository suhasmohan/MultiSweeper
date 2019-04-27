package com.multisweeper.server.logic;


import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.io.DataInputStream;
import java.io.DataOutputStream;

public class GroupMessageHandler extends Thread {

	private static List<String> responses = new ArrayList<>();
	int port=3005;
	String my_ip;
	String message;
	//private static Object lock = new Object();

	public GroupMessageHandler(String ip, String message) {
		this.my_ip = ip;
		this.message = message;
	}

	@Override
	public void run() {

		Socket socket;
		//PrintWriter writer;
		//BufferedReader reader;
		DataOutputStream dos;
		DataInputStream dis;

		//String hostname = "localhost";

		try {
			socket = new Socket(my_ip, port);
			//Logger.log(String.format("Connected to Server %s:%d", hostname, port));
			// Get the output stream of the socket. This is used to send data to the server
			// writer = new PrintWriter(socket.getOutputStream(), true);
			// reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			dos = new DataOutputStream(socket.getOutputStream());
			dis = new DataInputStream(socket.getInputStream());

			// writer.println(message);
			// writer.flush();
			dos.writeUTF(this.message);

			//String resp = reader.readLine();
			//Logger.log(String.format("Got response %s from %s:%d ", resp, hostname, port));
			
			String resp = dis.readUTF();

			responses.add(resp);
			
			dos.close();
			dis.close();
			socket.close();
		} catch (IOException e) {
			// Thrown during Socket creation or Read/Write operation
			// Eg - Port already in use by another application
			//Logger.log("I/O exception. Error = " + e.getMessage());
			e.printStackTrace();
		}
	}

	public static List<String> getResponses(){
		return responses;
	}

	public static void clearResponses(){
		responses.clear();
	}
}
