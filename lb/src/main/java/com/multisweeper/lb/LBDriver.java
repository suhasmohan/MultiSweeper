package com.multisweeper.lb;

import com.multisweeper.failure.MSServerFailureDetection;
import com.multisweeper.failure.MinesweeperGroupFailureDetector;

import static java.lang.Thread.sleep;

public class LBDriver {

	public static void main(String[] args) {
		startFailureDetector();
		try {
			sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		LoadBalancer lb = new LoadBalancer(Constants.LISTENER_PORT);
		lb.startLB();
	}

	private static void startFailureDetector() {
		MSServerFailureDetection failureDetection = new MinesweeperGroupFailureDetector(0L);

		Thread t = new Thread(failureDetection);

		t.start();
	}

}
