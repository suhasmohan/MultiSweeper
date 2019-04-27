package com.multisweeper.server.failure;

import com.multisweeper.server.utils.Logger;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.*;
import java.time.ZonedDateTime;
import java.util.*;

/**
 * MinesweeperGroupFailureDetector
 * <p>
 * This class implements failure detectiong functionality of the Minesweeper servers.
 * It uses MinesweeperGroupMulticast class to carry out multicast communication with
 * group members. It keeps lists of alive group members and dead group members.
 * It compares the sequence number echoed back from group members with the expected
 * sequence number to detect failure. When a group member miss
 *
 * @author Seung Hun Lee
 * @version 1.0
 * @since 2019-04-19
 */

public class MinesweeperGroupFailureDetector implements MSServerFailureDetection {
	// constants
	private static final String MESSAGE_TEMPLATE = "%s|%s|%d";
	private static final int GROUP_COMM_PORT = 3333;
	private static final int TOUCH_BASE_MULTICAST_INTERVAL = 500; // in milliseconds
	private static final int MISSED_MESSAGE_TOLERANCE_COUNT = 2;
	// group lists
	private static ArrayList<String> aliveMemberAddrs;
	private static ArrayList<String> deadMemberAddrs;
	// other static objects
	// lastReceivedSeqTable: is used for tracking the sequence number
	// for detecting group member failure
	private static Hashtable<String, Long> lastReceivedSeqTable = new Hashtable<>();
	// This server information
	private static Long msgSeq;
	// worker threads
	private ThreadGroup multicastOps;

	/**
	 * This a constructor for MinesweeperGroupFailureDetector Class Object.
	 *
	 * @param myIPAddr   current instance's IP address
	 * @param msgSeq     starting number of sequence number sent with messages
	 * @param groupAddrs list of IP addresses of group members
	 * @throws IOException On input error.
	 */
	public MinesweeperGroupFailureDetector(Long msgSeq
	) {
		this.setStaticGroupLists();
//		this.setlastReceivedSeqTable();
		MinesweeperGroupMulticast.setMessageQueues();
		MinesweeperGroupMulticast.setServerSocket(GROUP_COMM_PORT);

		MinesweeperGroupFailureDetector.msgSeq = msgSeq;
		this.multicastOps = new ThreadGroup("Multicast Operations");
	}

	// Private methods

	/**
	 * setStaticGroupLists() method instantiates the static variable if
	 * they are not initialized.
	 *
	 * @param groupAddrs list of IP addresses of group members
	 * @return returns nothing.
	 */
	private void setStaticGroupLists() {
		if (MinesweeperGroupFailureDetector.aliveMemberAddrs == null) {
			MinesweeperGroupFailureDetector.aliveMemberAddrs = new ArrayList<String>();
		}
		if (MinesweeperGroupFailureDetector.deadMemberAddrs == null) {
			MinesweeperGroupFailureDetector.deadMemberAddrs = new ArrayList<String>();
		}
	}

//	/**
//	   * setlastReceivedSeqTable() method creates and puts entries into
//	   * lastReceivedSeqTable.
//	   * @return nothing.
//	   */
//	private void setlastReceivedSeqTable() {
//		MinesweeperGroupFailureDetector.lastReceivedSeqTable = new Hashtable<String, Long>();
//		synchronized(MinesweeperGroupFailureDetector.lastReceivedSeqTable) {
//			for (String ipAddr : MinesweeperGroupFailureDetector.groupAddrs) {
//				MinesweeperGroupFailureDetector.lastReceivedSeqTable
//					.put(ipAddr,MinesweeperGroupFailureDetector.msgSeq);
//			}
//		}
//	}
//

	/**
	 * addMessagesToQueue method periodic group multicast messages to send
	 * message queue.
	 *
	 * @return nothing.
	 */
	private void addGroupMulticastMsgsToQueue()
			throws UnsupportedEncodingException, UnknownHostException {
		// create message string
		String messageToGroup = String.format(MESSAGE_TEMPLATE,
				"LIGMA HEART",
				System.getenv("HOSTNAME"),
				MinesweeperGroupFailureDetector.msgSeq);
		// send to each group members
		//for (String ipAddr : MinesweeperGroupFailureDetector.groupAddrs) {
		for (InetAddress broadcastIP : listAllBroadcastAddresses()) {
			Logger.log("Sending msg to " + broadcastIP.getHostAddress());
			DatagramPacket touchBaseMessage =
					new DatagramPacket(messageToGroup.getBytes("UTF-8"),
							messageToGroup.getBytes("UTF-8").length,
							broadcastIP,
							GROUP_COMM_PORT);
			MinesweeperGroupMulticast.addToSendQueue(touchBaseMessage);
		}
		MinesweeperGroupFailureDetector.msgSeq++;
	}

	List<InetAddress> listAllBroadcastAddresses() {
		List<InetAddress> broadcastList = new ArrayList<>();
		try {
			Enumeration<NetworkInterface> interfaces
					= NetworkInterface.getNetworkInterfaces();
			while (interfaces.hasMoreElements()) {
				NetworkInterface networkInterface = interfaces.nextElement();

				if (networkInterface.isLoopback() || !networkInterface.isUp()) {
					continue;
				}

				networkInterface.getInterfaceAddresses().stream()
						.map(a -> {
							if (a.getBroadcast() != null && a.getBroadcast().getHostAddress().startsWith("10.0"))
								return a.getBroadcast();
							return null;
						})
						.filter(Objects::nonNull)
						.forEach(broadcastList::add);
			}
		} catch (SocketException e) {
			e.printStackTrace();
		}
		return broadcastList;

	}

	/**
	 * updateGroupStatusLists() method re-occupy the alive members and
	 * dead members lists.
	 *
	 * @return nothing.
	 */
	private void updateGroupStatusLists() {
		// replace lists with a new empty lists
		MinesweeperGroupFailureDetector.aliveMemberAddrs = new ArrayList<String>();
		MinesweeperGroupFailureDetector.deadMemberAddrs = new ArrayList<String>();
		// look through lastReceivedSeqTable and re-occupy lists
		synchronized (MinesweeperGroupFailureDetector.lastReceivedSeqTable) {
			for (String ipAddr : MinesweeperGroupFailureDetector.lastReceivedSeqTable.keySet()) {
				Long receivedSeq = MinesweeperGroupFailureDetector.lastReceivedSeqTable.get(ipAddr);
				// when sequence number in the message within tolerance bound
				if ((MinesweeperGroupFailureDetector.msgSeq - (receivedSeq + 1))
						< MISSED_MESSAGE_TOLERANCE_COUNT) {
					synchronized (MinesweeperGroupFailureDetector.deadMemberAddrs) {
						MinesweeperGroupFailureDetector.deadMemberAddrs.add(ipAddr);
					}
				} else {
					synchronized (MinesweeperGroupFailureDetector.aliveMemberAddrs) {
						MinesweeperGroupFailureDetector.aliveMemberAddrs.add(ipAddr);
					}
				}
			}
		}
	}

	/**
	 * beginMulticastGroupComm() method instantiates the MinesweeperGroupMulticast
	 * static variable and begins the multicast group communication between
	 * Minesweeper game servers.
	 *
	 * @param groupAddrs list of IP addresses of group members
	 * @return returns nothing.
	 */
	private void beginMulticastGroupComm() {
		// instantiate worker threads
		// Thread for send operation
		new Thread(this.multicastOps, new MinesweeperGroupMulticast(true, false, false)).start();
		// Thread for receive operation
		new Thread(this.multicastOps, new MinesweeperGroupMulticast(false, true, false)).start();
		// Thread for process operation
		new Thread(this.multicastOps, new MinesweeperGroupMulticast(false, false, true)).start();
		// start server
		while (true) {
			try {
				// add messages to queue
				this.addGroupMulticastMsgsToQueue();
				// wait 500ms for the group members to reply
				Thread.sleep(TOUCH_BASE_MULTICAST_INTERVAL);
				// update alive/dead lists
				//this.updateGroupStatusLists();
				Logger.log("ALive servers = " + getAliveMemberAddrs().toString());
			} catch (Exception err) {
				this.handleExceptions(err);
			}
		}
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
		} else if (ex instanceof UnsupportedEncodingException) {
			System.out.printf("We got a Unsupported Encoding Exception: %s", ex.getMessage());
		} else if (ex instanceof UnknownHostException) {
			System.out.printf("We got a Unknown Host Exception: %s", ex.getMessage());
		} else {
			ex.printStackTrace();
		}
	}

	// public methods

	/**
	 * updatelastReceivedValue(): method is used to update the last seen
	 * sequence number for each group member.
	 *
	 * @return nothing
	 */
	public static void updatelastReceivedValue(String ipAddr, Long seq) {
		synchronized (MinesweeperGroupFailureDetector.lastReceivedSeqTable) {
			MinesweeperGroupFailureDetector.lastReceivedSeqTable.put(ipAddr, seq);
		}
	}

	/**
	 * getGroupAddrsList(): method gets the list of group
	 * members' IP addresses without this server's IP address
	 * @return
	 */
//	public static ArrayList<String> getGroupAddrsList() {
//		return MinesweeperGroupFailureDetector.groupAddrs;
//	}

	// MinesweeperServerFailureDetection interface methods


	/**
	 * getAliveMemberAddrs(): returns alive group members' ports.
	 *
	 * @return a array of port numbers of alive group members
	 */
	public static ArrayList<String> getAliveMemberAddrs() {
		ArrayList<String> aliveMembers = new ArrayList<>();
		long currentTimeStamp = ZonedDateTime.now().toInstant().toEpochMilli();
		for (Map.Entry<String, Long> entry : lastReceivedSeqTable.entrySet()) {
			if (entry.getValue() > currentTimeStamp - (TOUCH_BASE_MULTICAST_INTERVAL * MISSED_MESSAGE_TOLERANCE_COUNT)) {
				aliveMembers.add(entry.getKey());
			}
		}
		return aliveMembers;
	}

	/**
	 * getDeadMemberAddrs(): returns dead group members' ports.
	 *
	 * @return a array of port numbers of dead group members
	 */
	public ArrayList<String> getDeadMemberAddrs() {
		return MinesweeperGroupFailureDetector.deadMemberAddrs;
	}

	/**
	 * run(): starts a failure detection algorithm running on
	 * a thread.
	 *
	 * @return nothing
	 */
	public void run() {
		this.beginMulticastGroupComm();
	}
}