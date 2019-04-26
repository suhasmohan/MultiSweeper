package com.multisweeper.server.REST;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Request;
import spark.Response;

public class RESTHandler {
	static Logger log = LoggerFactory.getLogger(RESTHandler.class);

	public Object handleClick(Request req, Response res) {
		Click clickData = new Gson().fromJson(req.body(), Click.class);
		log.info(String.format("Got click row: %d, col: %d, operation: %s", clickData.getRow(), clickData.getCol(), clickData.getType()));

		// TODO - Add 2PC call here
		return "{status: \"success\"}";
	}

	public Object getBoard(Request req, Response res) {
		// TODO - Get board and convert to JSON
		return "{status: \"success\"}";
	}
}
