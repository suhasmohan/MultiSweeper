package com.multisweeper.server.failure;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Hashtable;

/**
* MinesweeperGroupFailureDetector
*
* This class implements failure detectiong functionality of the Minesweeper servers.
* It uses MinesweeperGroupMulticast class to carry out multicast communication with
* group members. It keeps lists of alive group members and dead group members.  
* It compares the sequence number echoed back from group members with the expected 
* sequence number to detect failure. When a group member miss
*
* @author  Seung Hun Lee
* @version 1.0
* @since   2019-04-19
*/

public class MinesweeperGroupFailureDetector implements MSServerFailureDetection {
	// constants
	private static final String MESSAGE_TEMPLATE = "%s|%s|%d";
	private static final int GROUP_COMM_PORT = 3333;
	private static final int TOUCH_BASE_MULTICAST_INTERVAL = 500; // in milliseconds
	private static final int MISSED_MESSAGE_TOLERANCE_COUNT = 2;
	// group lists
	private static ArrayList<String> groupAddrs;
	private static ArrayList<String> aliveMemberAddrs;
	private static ArrayList<String> deadMemberAddrs;
	// other static objects
	// lastReceivedSeqTable: is used for tracking the sequence number
	// for detecting group member failure
	private static Hashtable<String,Integer> lastReceivedSeqTable;
	// This server information
	private String myIPAddr;
	private int msgSeq;
	// worker threads
	private ThreadGroup multicastOps;
	
	/**
	   * This a constructor for MinesweeperGroupFailureDetector Class Object.
	   * @param myIPAddr current instance's IP address
	   * @param msgSeq starting number of sequence number sent with messages
	   * @param groupAddrs list of IP addresses of group members
	   * @exception IOException On input error.
	   */
	public MinesweeperGroupFailureDetector(String myIPAddr,int msgSeq, 
			ArrayList<String> groupAddrs) throws IOException {
		this.setStaticGroupLists(groupAddrs);
		this.setlastReceivedSeqTable();
		MinesweeperGroupMulticast.setIPAddr(myIPAddr);
		MinesweeperGroupMulticast.setMessageQueues();
		MinesweeperGroupMulticast.setServerSocket(GROUP_COMM_PORT);
		this.myIPAddr = myIPAddr;
		this.msgSeq = msgSeq;
		this.multicastOps = new ThreadGroup("Multicast Operations");
	}

	// Private methods
	/**
	   * setStaticGroupLists() method instantiates the static variable if
	   * they are not initialized.
	   * @param groupAddrs list of IP addresses of group members
	   * @return returns nothing.
	   */
	private void setStaticGroupLists(ArrayList<String> groupAddrs) throws IOException {
		if (MinesweeperGroupFailureDetector.groupAddrs == null) {
			MinesweeperGroupFailureDetector.groupAddrs = groupAddrs;
		}
		if (MinesweeperGroupFailureDetector.aliveMemberAddrs == null) {
			MinesweeperGroupFailureDetector.aliveMemberAddrs = new ArrayList<String>();
		}
		if (MinesweeperGroupFailureDetector.deadMemberAddrs == null) {
			MinesweeperGroupFailureDetector.deadMemberAddrs = new ArrayList<String>();
		}
	}
	
	/**
	   * setlastReceivedSeqTable() method creates and puts entries into  
	   * lastReceivedSeqTable.
	   * @return nothing.
	   */
	private void setlastReceivedSeqTable() {
		for (String ipAddr : MinesweeperGroupFailureDetector.groupAddrs) {
			MinesweeperGroupFailureDetector.lastReceivedSeqTable.put(ipAddr,this.msgSeq);
		}
	}
	
	/**
	   * addMessagesToQueue method periodic group multicast messages to send
	   * message queue.
	   * @return nothing.
	   */
	private void addGroupMulticastMsgsToQueue() 
			throws UnsupportedEncodingException, UnknownHostException {
		// create message string
		String messageToGroup = String.format(MESSAGE_TEMPLATE,this.myIPAddr,
				System.getenv("HOSTNAME"),this.msgSeq);
		// send to each group members
		for (String ipAddr : MinesweeperGroupFailureDetector.groupAddrs) {
			DatagramPacket touchBaseMessage = 
					new DatagramPacket(messageToGroup.getBytes("UTF-8"),
							messageToGroup.getBytes("UTF-8").length,
								InetAddress.getByName(ipAddr),
									GROUP_COMM_PORT);
			MinesweeperGroupMulticast.addToSendQueue(touchBaseMessage);
		}
		this.msgSeq++;
	}
	
	/**
	   * updateGroupStatusLists() method re-occupy the alive members and 
	   * dead members lists.
	   * @return nothing.
	   */
	private void updateGroupStatusLists() {
		// replace lists with a new empty lists
		MinesweeperGroupFailureDetector.aliveMemberAddrs = new ArrayList<String>();
		MinesweeperGroupFailureDetector.deadMemberAddrs = new ArrayList<String>();
		// look through lastReceivedSeqTable and re-occupy lists
		for (String ipAddr : MinesweeperGroupFailureDetector.lastReceivedSeqTable.keySet()) {
			int receivedSeq = MinesweeperGroupFailureDetector.lastReceivedSeqTable.get(ipAddr);
			// when sequence number in the message within tolerance bound
			if ((this.msgSeq - (receivedSeq + 1)) < MISSED_MESSAGE_TOLERANCE_COUNT) {
				MinesweeperGroupFailureDetector.deadMemberAddrs.add(ipAddr);
			} else {
				MinesweeperGroupFailureDetector.aliveMemberAddrs.add(ipAddr);
			}
		}
	}
	
	/**
	   * beginMulticastGroupComm() method instantiates the MinesweeperGroupMulticast 
	   * static variable and begins the multicast group communication between
	   * Minesweeper game servers.
	   * @param groupAddrs list of IP addresses of group members
	   * @return returns nothing.
	   */
	private void beginMulticastGroupComm(String myIPAddr,int portNum,
			ArrayList<String> groupAddrs) {
		// instantiate worker threads
		// Thread for send operation
		new Thread(this.multicastOps,new MinesweeperGroupMulticast(true,false,false)).start();
		// Thread for receive operation
		new Thread(this.multicastOps,new MinesweeperGroupMulticast(false,true,false)).start();
		// Thread for process operation
		new Thread(this.multicastOps,new MinesweeperGroupMulticast(false,false,true)).start();
		// start server
		while(true) {
			try {
				// add messages to queue
				this.addGroupMulticastMsgsToQueue();
				// wait 500ms for the group members to reply
				Thread.sleep(TOUCH_BASE_MULTICAST_INTERVAL);
				// update alive/dead lists
				this.updateGroupStatusLists();
			} catch (Exception err) {
				this.handleExceptions(err);
			}
		}
	}
	
	/**
	   * handleExceptions(): static method handles exceptions thrown by 
	   * group multicast operations.
	   * @param ex exception being thrown.
	   * @return nothing.
	   */
	private void handleExceptions(Exception ex) {
		if (ex instanceof IOException) {
			System.out.printf("We got a IO exception: %s",ex.getMessage());
		} else if (ex instanceof UnsupportedEncodingException) {
			System.out.printf("We got a Unsupported Encoding Exception: %s",ex.getMessage());
		} else if (ex instanceof UnknownHostException) {
			System.out.printf("We got a Unknown Host Exception: %s",ex.getMessage());
		} else {
			ex.printStackTrace();
		}
	}
	
	// public methods
	/**
	   * updatelastReceivedValue(): method is used to update the last seen
	   * sequence number for each group member.
	   * @return nothing
	   */
	public static void updatelastReceivedValue(String ipAddr,int seq) {
		MinesweeperGroupFailureDetector.lastReceivedSeqTable.put(ipAddr,seq);
	}

	// MinesweeperServerFailureDetection interface methods

	/**
	   * getGroupAddrsList(): method gets the list of group 
	   * members' IP addresses without this server's IP address
	   * @return 
	   */
	public ArrayList<String> getGroupAddrsList() {
		return MinesweeperGroupFailureDetector.groupAddrs;
	}

	/**
	   * getAliveMemberAddrs(): returns alive group members' ports.
	   * @return a array of port numbers of alive group members
	   */
	public ArrayList<String> getAliveMemberAddrs() {
		return MinesweeperGroupFailureDetector.aliveMemberAddrs;
	}

	/**
	   * getDeadMemberAddrs(): returns dead group members' ports.
	   * @return a array of port numbers of dead group members
	   */
	public ArrayList<String> getDeadMemberAddrs() {
		return MinesweeperGroupFailureDetector.deadMemberAddrs;
	}

	/**
	   * run(): starts a failure detection algorithm running on 
	   * a thread.
	   * @return nothing
	   */
	public void run() {
		this.beginMulticastGroupComm(this.myIPAddr,
				GROUP_COMM_PORT,MinesweeperGroupFailureDetector.groupAddrs);
	}
}