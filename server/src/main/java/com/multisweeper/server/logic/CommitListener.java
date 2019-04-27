package com.multisweeper.server.logic;
import com.multisweeper.server.Main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.io.DataInputStream;
import java.io.DataOutputStream;

public class CommitListener extends Thread {

	private int port;
	//private Map<String, Transaction> phaseMap = new HashMap<>();
	private int ongoing_primary_key = -1000;
	private int ongoing_secondary_key = -1000;


	public CommitListener(int port) {
		this.port = port;
	}

	@Override
	public void run() {
		ServerSocket socket;
		Socket clientSocket;
		//BufferedReader reader;

		try {
			// Create a server socket and bind to port. Set connection backlog to 1
			//Logger.log("Starting server socket on port " + port);
			socket = new ServerSocket(port, 1);
			while (true) {
				// Wait and accept a connection
				clientSocket = socket.accept();
				String response="";
				//Logger.log("Got commit connection!");
				// Get a communication stream associated with the socket
				
				// PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true);
				// reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

                DataInputStream dis = new DataInputStream(clientSocket.getInputStream());
                DataOutputStream dos = new DataOutputStream(clientSocket.getOutputStream());


				//String req = reader.readLine();
				String received_message = dis.readUTF();

				String received_messages[] = received_message.split("|");

				//Phase 1 for put

				if(received_messages[0].equals("FLAG")){
					response = "commit";
				}

				else if (received_messages[0].equals("PUT")){
					int primary_tile_key= Integer.parseInt(received_messages[1]);
					int secondary_tile_key = Integer.parseInt(received_messages[2]);


					if (primary_tile_key==this.ongoing_primary_key){
						response = "abort";
					}
					else if(secondary_tile_key==this.ongoing_secondary_key){
						response = "abort";
					}
					else{
						response = "commit";

						//making the proposed key as the ongoing_key untill it is committed
						this.ongoing_primary_key = primary_tile_key;
						this.ongoing_secondary_key = secondary_tile_key;
					}
				}

				else if(received_messages[0].equals("ABORT")){

					this.ongoing_primary_key = -1000;
					this.ongoing_secondary_key = -1000;

				}

				else if(received_messages[0].equals("COMMIT")){
					// implement the put operation for a given key/tile
					int row= Integer.parseInt(received_messages[1]);
					int col = Integer.parseInt(received_messages[2]);
					Main.gameBoard.tileOpen(row,col);

				}

				dos.writeUTF(response);

				dos.flush();
				dos.close();
				dis.close();
				clientSocket.close();
			}

		} catch (IOException e) {
			// Thrown during Socket creation or Read/Write operation
			// Eg - Port already in use by another application
			//Logger.log("I/O exception. Error = " + e.getMessage());
			e.printStackTrace();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

