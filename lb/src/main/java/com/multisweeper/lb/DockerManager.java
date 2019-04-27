package com.multisweeper.lb;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class DockerManager {

	public boolean removeContainer(String containerId) {
		String[] cmd = new String[] {"docker", "container", "stop", containerId};
		return runCmd(cmd);
	}

	public boolean startContainer() {
		String[] cmd = new String[] {"docker", "service", "update", "--replicas=3", "multisweeper_Server"};
		return runCmd(cmd);
	}

	private boolean runCmd(String[] cmd) {
		try {
			Process process = Runtime.getRuntime().exec(cmd);
			BufferedReader reader = new BufferedReader(
					new InputStreamReader(process.getInputStream()));

			StringBuilder output = new StringBuilder();
			String line;
			while ((line = reader.readLine()) != null) {
				output.append(line + "\n");
			}
			int ret = process.waitFor();
			System.out.println("Return value is... " + ret);
			System.out.println("Output = " + output);
			return ret == 0;
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return false;
	}

	public static void main(String[] args) {
		DockerManager dm = new DockerManager();
		dm.removeContainer(args[0]);
		dm.startContainer();
	}
}
