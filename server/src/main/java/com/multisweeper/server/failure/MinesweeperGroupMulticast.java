package com.multisweeper.server.failure;

import com.multisweeper.server.utils.Logger;

import java.io.IOException;
import java.net.*;
import java.time.ZonedDateTime;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

/**
 * MinesweeperGroupMulticast
 * <p>
 * This class implements multicast functionality of the Minesweeper servers.
 * The messages are in FIFO ordering. The ordering is achieved through
 * basic multicast (threaded) and lamport process clock sequence piggybacked
 * to each message to the group. The algorithm is written with an assumption
 * that the group is a closed and non-overlapping.
 *
 * @author Seung Hun Lee
 * @version 1.0
 * @since 2019-04-19
 */

public class MinesweeperGroupMulticast implements Runnable {
	// constants
	private static final int PACKET_BUFF_SIZE = 100;
	// UDP variables
	private static DatagramSocket udpSocket;
	private static String myIPAddr;
	// message buffer
	private static Queue<DatagramPacket> receivedPacketQueue;
	private static Queue<DatagramPacket> sendPacketQueue;
	private static Set<String> receivedMessages;
	// mode switches
	private boolean sendOp;
	private boolean receiveOp;
	private boolean msgProcessOp;
	private static DatagramSocket socket = null;

	/**
	 * This a constructor for MinesweeperGroupMulticast Class Object.
	 *
	 * @param sendOp       setting this to true and other two to false
	 *                     will set the object being instantiated to send operation
	 *                     thread worker
	 * @param receiveOp    setting this to true and other two to false
	 *                     will set the object being instantiated to receive operation
	 *                     thread worker
	 * @param msgProcessOp setting this to true and other two to false
	 *                     will set the object being instantiated to message processing
	 *                     operation thread worker
	 */
	public MinesweeperGroupMulticast(boolean sendOp,
									 boolean receiveOp, boolean msgProcessOp) {
		this.sendOp = sendOp;
		this.receiveOp = receiveOp;
		this.msgProcessOp = msgProcessOp;
	}

	/**
	 * setServerSocket(): method sets up the server socket.
	 *
	 * @param portNum port number for socket binding
	 * @return nothing
	 */
	public static void setServerSocket(int portNum) {
		try {
			MinesweeperGroupMulticast.udpSocket = new DatagramSocket(portNum);
		} catch (SocketException e) {
			e.printStackTrace();
		}
		System.out.printf("Server is running at port: %d\n", portNum);
	}

	/**
	 * setIPAddr(): method records the IP address of the socket.
	 *
	 * @param ipAddr the IP address of the socket
	 * @return nothing
	 */
	public static void setIPAddr(String ipAddr) {
		MinesweeperGroupMulticast.myIPAddr = ipAddr;
	}

	/**
	 * setMessageQueues(): method instantiates the send and receive message queues.
	 *
	 * @return nothing
	 */
	public static void setMessageQueues() {
		MinesweeperGroupMulticast.receivedPacketQueue = new LinkedList<DatagramPacket>();
		MinesweeperGroupMulticast.sendPacketQueue = new LinkedList<DatagramPacket>();
	}

	/**
	 * addToSendQueue(): method adds an outgoing datagram packet
	 * to the send message queue.
	 *
	 * @param dgp datagram packet to add to the queue
	 * @return nothing
	 */
	public static void addToSendQueue(DatagramPacket dgp) {
		synchronized (MinesweeperGroupMulticast.sendPacketQueue) {
			MinesweeperGroupMulticast.sendPacketQueue.add(dgp);
		}
	}

	/**
	 * broadcast(): method broadcasts a datagram packet
	 * to the subnet.
	 *
	 * @param packet datagram packet to send
	 * @return nothing
	 */
	public void broadcast(
			DatagramPacket packet) throws IOException {
		socket = new DatagramSocket();
		socket.setBroadcast(true);
		socket.send(packet);
		socket.close();
	}

	/**
	 * parseMessageToArray(): method parses received message
	 * into a string array.
	 * index ->
	 * 0: hearbeat message
	 * 1: message origin Docker container ID
	 * 2: message sequence
	 *
	 * @return a string array of parsed message values
	 */
	private String[] parseMessageToArray(DatagramPacket receivedPacket) {
		String message = new String(receivedPacket.getData(), 0,
				receivedPacket.getLength());
		return message.split("|", 3);
	}

	/**
	 * handleExceptions(): static method handles exceptions thrown by
	 * group multicast operations.
	 *
	 * @param ex exception being thrown.
	 * @return nothing.
	 */
	private void handleExceptions(Exception ex) {
		if (ex instanceof IOException) {
			System.out.printf("We got a IO exception: %s", ex.getMessage());
		} else {
			ex.printStackTrace();
		}
	}

	// MSServerMulticast interface methods

	/**
	 * sendMessages(): multicasts messages to group members.
	 * Message format: "<message>|<container-id>|<sequence-number>"
	 *
	 * @return nothing
	 */
	private void sendMessages() {
		while (true) {
			try {
				// if there is a message in the send message queue
				synchronized (MinesweeperGroupMulticast.sendPacketQueue) {
					if (!MinesweeperGroupMulticast.sendPacketQueue.isEmpty()) {
						DatagramPacket sendPacket =
								MinesweeperGroupMulticast.sendPacketQueue.remove();
						//MinesweeperGroupMulticast.udpSocket.send(sendPacket);
						Logger.log("Sending broadcast message!");
						broadcast(sendPacket);
					}
				}

			} catch (Exception err) {
				this.handleExceptions(err);
			}
		}
	}

	/**
	 * receiveMessages(): receive messages from group members.
	 * Message format: "<message>|<container-id>|<sequence-number>"
	 *
	 * @return nothing
	 */
	private void receiveMessages() {
		while (true) {
			try {
				DatagramPacket receivePacket =
						new DatagramPacket(new byte[PACKET_BUFF_SIZE],
								PACKET_BUFF_SIZE);
				// blocking receive statement
				MinesweeperGroupMulticast.udpSocket.receive(receivePacket);
				Logger.log("Received heartbeat from " + receivePacket.getAddress().getHostAddress());
				synchronized (MinesweeperGroupMulticast.receivedPacketQueue) {
					MinesweeperGroupMulticast.receivedPacketQueue.add(receivePacket);
				}
			} catch (Exception err) {
				this.handleExceptions(err);
			}
		}
	}

	/**
	 * processMessages(): process messages by parsing and comparing liveness sequence
	 * number in each message.
	 * Message format: "<message>|<container-id>|<sequence-number>"
	 *
	 * @return nothing
	 */
	private void processMessages() {
		while (true) {
			try {
				// when there is a message to be processed
				synchronized (MinesweeperGroupMulticast.receivedPacketQueue) {
					if (!MinesweeperGroupMulticast.receivedPacketQueue.isEmpty()) {
						DatagramPacket receivedPacket =
								MinesweeperGroupMulticast.receivedPacketQueue.remove();
						// parse the message into String array (always length of 3)
						// [<IP-address>,<Container-ID>,<sequence-number>]
						String[] parsedMessage = this.parseMessageToArray(receivedPacket);
						// update the entry with the sender's IP address as key in the
						// last seen sequence tracking table
						MinesweeperGroupFailureDetector.updatelastReceivedValue(
								receivedPacket.getAddress().toString(),
									ZonedDateTime.now().toInstant().toEpochMilli());

					}
				}
			} catch (Exception err) {
				this.handleExceptions(err);
			}
		}
	}

	/**
	 * run(): runs the algorithm for sending, receiving, processing messages
	 * with a group of minesweeper servers
	 *
	 * @return nothing
	 */
	public void run() {
		if (this.sendOp) {
			this.sendMessages();
		} else if (this.receiveOp) {
			this.receiveMessages();
		} else if (this.msgProcessOp) {
			this.processMessages();
		}
	}
}