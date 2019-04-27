package com.multisweeper.server;

import com.multisweeper.server.REST.RESTHandler;
import com.multisweeper.server.utils.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Request;
import com.multisweeper.server.logic.*;

import static spark.Spark.*;
import static spark.debug.DebugScreen.*;

public class Main {

	static Logger log = LoggerFactory.getLogger(Main.class);
	public static Board gameBoard;
	//initializing the gameBoard




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

		//building the game board text file
		InitBoardFile.main(new String[1]);
		gameBoard = Board.fromFile();

		staticFiles.location("/public");
		staticFiles.expireTime(600L);
		enableDebugScreen();

		before((request, response) -> {
			log.info(requestInfoToString(request));
			response.header("Connection", "close");
		});

		RESTHandler restHandler = new RESTHandler();
		get("/hello", (req, res) -> "Hello World from " + System.getenv("HOSTNAME") );

		post("/api/click",  (req, res) -> restHandler.handleClick(req, res));

		get("/api/board", (req,res) -> restHandler.getBoard(req, res));

		System.out.println("Server started on port " + port);
	}
}
