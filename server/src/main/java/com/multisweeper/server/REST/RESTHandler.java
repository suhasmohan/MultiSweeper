package com.multisweeper.server.REST;

import com.google.gson.Gson;
import com.multisweeper.server.logic.Board;
import com.multisweeper.server.logic.InitBoardFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Request;
import spark.Response;

public class RESTHandler {
  private static final Logger log = LoggerFactory.getLogger(RESTHandler.class);
  private static Board board = Board.fromFile();

  public static Object handleClick(Request req, Response res) {
    Click clickData = new Gson().fromJson(req.body(), Click.class);
    RESTHandler.log.info(
        String.format(
            "Got click row: %d, col: %d, operation: %s",
            clickData.getRow(), clickData.getCol(), clickData.getType()));

    // TODO - Add 2PC call here
    if (clickData.getType().equals("restart")) {
      InitBoardFile.main(new String[1]);
      RESTHandler.board = Board.fromFile();
    } else if (clickData.getType().equals("open")) {
      RESTHandler.board.tileOpen(clickData.getRow(), clickData.getCol());
    } else if (clickData.getType().equals("flag")) {
      RESTHandler.board.tileFlag(clickData.getRow(), clickData.getCol());
    }

    return "{status: \"success\"}";
  }

  public static Object getBoard(Request req, Response res) {

    // TODO - Get board and convert to JSON
    return RESTHandler.board.toJson();
  }
}
