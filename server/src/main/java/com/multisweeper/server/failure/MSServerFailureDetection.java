package com.multisweeper.server.failure;
import java.util.ArrayList;

/**
* MSServerFailureDetection
*
* This interface is for classes that implements failure detection algorithm through
* multicast group communication capability between multiplayer Mineseeper game service 
* servers for failure detection purpose. 
* Mode of communication is TCP or UDP depending on the implementation
* of the class used as a pair that handles the multicast. Multicast function to group
* ensures FIFO message ordering by utilizing a sequence number.
*
* @author  Seung Hun Lee
* @version 1.0
* @since   2019-04-19
*/

public interface MSServerFailureDetection extends Runnable {
	
	/**
	   * getGroupAddrsList method gets the list of group 
	   * members' IP addresses without this server's IP address
	   * @return 
	   */
	//public ArrayList<String> getGroupAddrsList();

	/**
	   * getAliveMemberAddrs(): returns alive group members' ports.
	   * @return a array of port numbers of alive group members
	   */
	public ArrayList<String> getAliveMemberAddrs();

	/**
	   * getDeadMemberAddrs(): returns dead group members' ports.
	   * @return a array of port numbers of dead group members
	   */
	public ArrayList<String> getDeadMemberAddrs();

	/**
	   * run(): starts a failure detection algorithm on a thread.
	   * @return nothing
	   */
	public void run();
}
