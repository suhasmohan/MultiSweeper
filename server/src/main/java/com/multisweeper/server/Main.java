package com.multisweeper.server;

import com.multisweeper.server.REST.RESTHandler;
import com.multisweeper.server.utils.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Request;

import static spark.Spark.*;
import static spark.debug.DebugScreen.enableDebugScreen;

public class Main {

  private static final Logger log = LoggerFactory.getLogger(Main.class);

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
