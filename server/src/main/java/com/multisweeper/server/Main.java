package com.multisweeper.server;

import com.multisweeper.server.REST.RESTHandler;
import com.multisweeper.server.utils.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Request;
import com.multisweeper.server.logic.*;

import static spark.Spark.*;
import static spark.debug.DebugScreen.enableDebugScreen;

public class Main {

<<<<<<< HEAD
  private static final Logger log = LoggerFactory.getLogger(Main.class);
=======
	static Logger log = LoggerFactory.getLogger(Main.class);
	public static Board gameBoard;
	//initializing the gameBoard



>>>>>>> 90626c20001a35e4d846c983ce2b94b334743090

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

<<<<<<< HEAD
    staticFiles.location("/public");
    staticFiles.expireTime(600L);
    enableDebugScreen();
=======
		//building the game board text file
		InitBoardFile.main(new String[1]);
		gameBoard = Board.fromFile();

		staticFiles.location("/public");
		staticFiles.expireTime(600L);
		enableDebugScreen();
>>>>>>> 90626c20001a35e4d846c983ce2b94b334743090

    before(
        (request, response) -> {
          Main.log.info(Main.requestInfoToString(request));
          response.header("Connection", "close");
        });

    RESTHandler restHandler = new RESTHandler();
    get("/hello", (req, res) -> "Hello World from " + System.getenv("HOSTNAME"));

    post("/api/click", (req, res) -> RESTHandler.handleClick(req, res));

    get("/api/getBoard", (req, res) -> RESTHandler.getBoard(req, res));

    System.out.println("Server started on port " + port);
  }
}
