package com.multisweeper.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Logger {

	/**
	 * Log message with timestamp
	 *
	 * @param message Message to be logged
	 */
	public static void log(String message) {
		String timestamp = new SimpleDateFormat("HH.mm.ss.SSS").format(new Date());
		System.out.printf("%s\t%s\n", timestamp, message);
	}
}