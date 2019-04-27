package com.multisweeper.server.replication;

import com.google.gson.Gson;
import com.multisweeper.server.REST.RESTHandler;
import com.multisweeper.server.failure.MinesweeperGroupFailureDetector;
import com.multisweeper.server.logic.Board;
import com.multisweeper.server.utils.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class ReplicationHandler {

	public void getReplica() {
		// 1. Get list of IP addresses from Failure Detector
		// 2. Make HTTP request to first IP that is not my local IP
		// 3. Update RESTHandler.board with the value

		List<String> ipAddresses = new ArrayList<>(MinesweeperGroupFailureDetector.getAliveMemberAddrs());
		String myIP = getMyIP();
		System.out.println("MY IP = " + myIP);
		for(String ip : ipAddresses) {
			if(!ip.equalsIgnoreCase(myIP)) {
				System.out.println("Getting board from " + ip);
				// Make HTTP Request
				URL url = null;
				try {
					url = new URL("http://"+ip+":8080/api/getBoard");
					HttpURLConnection con = (HttpURLConnection) url.openConnection();
					con.setRequestMethod("GET");
					int status = con.getResponseCode();

					BufferedReader in = new BufferedReader(
							new InputStreamReader(con.getInputStream()));
					String inputLine;
					StringBuffer content = new StringBuffer();
					while ((inputLine = in.readLine()) != null) {
						content.append(inputLine);
					}
					in.close();
					con.disconnect();

					Logger.log("Got replica!");
					Board replicatedBoard = new Gson().fromJson(content.toString(), Board.class);

					RESTHandler.setBoard(replicatedBoard);

				} catch (MalformedURLException e) {
					e.printStackTrace();
				} catch (ProtocolException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}

			}
		}

	}

	private String getMyIP() {
		String ip;
		try {
			Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
			while (interfaces.hasMoreElements()) {
				NetworkInterface iface = interfaces.nextElement();
				// filters out 127.0.0.1 and inactive interfaces
				if (iface.isLoopback() || !iface.isUp())
					continue;

				Enumeration<InetAddress> addresses = iface.getInetAddresses();
				while(addresses.hasMoreElements()) {
					InetAddress addr = addresses.nextElement();
					ip = addr.getHostAddress();
					if(ip.startsWith("10.0"))
						return ip;
				}
			}
		} catch (SocketException e) {
			throw new RuntimeException(e);
		}
		return null;
	}
}
