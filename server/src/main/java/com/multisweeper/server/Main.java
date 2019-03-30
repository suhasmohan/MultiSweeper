package com.multisweeper.server;
import com.multisweeper.server.utils.Constants;

import static spark.Spark.*;
import static spark.debug.DebugScreen.*;

public class Main {
	public static void main(String[] args) {
		int port = Constants.PORT;
		port(port);

		staticFiles.location("/public");
		staticFiles.expireTime(600L);
		enableDebugScreen();

		get("/hello", (req, res) -> "Hello World");

		System.out.println("Server started on port " + port);
	}
}