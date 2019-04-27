package com.multisweeper.server.logic;

import com.multisweeper.server.Main;

import java.util.*;
import java.io.*;
import java.net.*;


//class ServerClass implements ServerInterface{
class ServerClass{	
	int self_port;
	String self_ip;
	ArrayList<String> ips= new ArrayList<String>(List.of("localhost","localhost","localhost"));
	ArrayList<Integer> ports= new ArrayList<Integer>(List.of(3000,3001,3002));

	ServerClass(int port){
		this.self_port=port;
	}

//	public static void main(String args[]){
//		//reading from file to initiate a board
//
//		// Starting Group communication
//
//		//Starting 2Phase Commit
//
//		//Starting HTTP Server
//
//	}

	public void updateIpAddresses(){

	}

	public void playerMove(String type, int row, int col){

		int main_key = Main.gameBoard.getKeys(row,col);
		int secondary_key = Main.gameBoard.getSingleKeys(row, col);
		String mess;
		// starting 2 phase commit


		try {
			//The second and the third part are the primary and the secondary key
			if (type.equals("CLICK")) {

				mess = "PUT"
						+ "|"
						+ Integer.toString(main_key)
						+ "|"
						+ Integer.toString(secondary_key);
			}
			else{
				mess = "FLAG"
						+ "|"
						+ Integer.toString(main_key)
						+ "|"
						+ Integer.toString(secondary_key);
			}

			multiCast(mess);
			List<String> responses = GroupMessageHandler.getResponses();
			//checking if any of the votes are "abort"
			for(String resp: responses){
				if (resp.equals("abort")){
					multiCast("ABORT");
					GroupMessageHandler.clearResponses();
					System.out.println("2PC unsuccessful");
					return;
				}
			}
			GroupMessageHandler.clearResponses();

			//otherwise sending a commit multicast to all the servers
			//message now contains row and col value instead of the keys

			multiCast("COMMIT|"+Integer.toString(row)+"|"+Integer.toString(col) );
			GroupMessageHandler.clearResponses();

			System.out.println("2PC successful");
		}catch(Exception e){
			e.printStackTrace();
		}

	}


	private void multiCast(String message) throws InterruptedException {
		List<Thread> threadList = new ArrayList<>();
		for (String ip : this.ips) {
			Thread t = new GroupMessageHandler(ip, message);
			threadList.add(t);
			t.start();
		}

		for (Thread t : threadList) {
			//Logger.log("Waiting for thread " + t.getName());
			t.join();
		}
		//Logger.log("Got responses from all servers!");
	}

	public Board getBoardReplica(){
		return Main.gameBoard;
	}


}