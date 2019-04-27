package com.multisweeper.server;

import com.multisweeper.server.REST.RESTHandler;

import com.multisweeper.server.failure.MinesweeperGroupFailureDetector;
import com.multisweeper.server.failure.MinesweeperGroupMulticast;

import com.multisweeper.server.failure.MSServerFailureDetection;
import com.multisweeper.server.failure.MinesweeperGroupFailureDetector;

import com.multisweeper.server.logic.Board;
import com.multisweeper.server.logic.CommitListener;
import com.multisweeper.server.logic.InitBoardFile;
import com.multisweeper.server.utils.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Request;

import java.io.IOException;
import java.util.ArrayList;

import static spark.Spark.*;
import static spark.debug.DebugScreen.enableDebugScreen;

public class Main {

  public static Board gameBoard;
  private static Logger log = LoggerFactory.getLogger(Main.class);
  // initializing the gameBoard

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

    // building the game board text file
    InitBoardFile.main(new String[1]);
    Main.gameBoard = Board.fromFile();

    //STARTING FAILURE DETECTOR

    ArrayList<String> temp = new ArrayList<>();

    MinesweeperGroupFailureDetector failure_detector_obj = null;
    try {
      failure_detector_obj = new MinesweeperGroupFailureDetector("change this",0,temp);
    } catch (IOException e) {
      e.printStackTrace();
    }
    Thread failure_detector = new Thread(failure_detector_obj);
    failure_detector.run();




    //STARTING COMMIT LISTENER
    Thread c_listener = new CommitListener(3005);
    c_listener.run();

    staticFiles.location("/public");
    staticFiles.expireTime(600L);
    enableDebugScreen();

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


    MSServerFailureDetection failureDetection = new MinesweeperGroupFailureDetector(0L);

    Thread t = new Thread(failureDetection);

    t.start();

  }
}
