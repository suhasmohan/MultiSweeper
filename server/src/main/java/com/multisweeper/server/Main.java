package com.multisweeper.server;
import com.multisweeper.server.utils.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Request;

import static spark.Spark.*;
import static spark.debug.DebugScreen.*;

public class Main {

	static Logger log = LoggerFactory.getLogger(Main.class);

	private static String requestInfoToString(Request request) {
		StringBuilder sb = new StringBuilder();
		sb.append(request.requestMethod());
		sb.append(" " + request.url());
		sb.append(" " + request.body());
		return sb.toString();
	}

	public static void main(String[] args) {
		int port = Constants.PORT;
		port(port);

		staticFiles.location("/public");
		staticFiles.expireTime(600L);
		enableDebugScreen();

		before((request, response) -> {
			log.info(requestInfoToString(request));
		});

		get("/hello", (req, res) -> "Hello World from " + System.getenv("HOSTNAME") );

		System.out.println("Server started on port " + port);
	}
}
