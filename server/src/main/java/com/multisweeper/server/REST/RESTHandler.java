package com.multisweeper.server.REST;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Request;
import spark.Response;

public class RESTHandler {
	static Logger log = LoggerFactory.getLogger(RESTHandler.class);
	public Object handleClick(Request req, Response res){
		Click clickData = new Gson().fromJson(req.body(), Click.class);

		return "{status: \"success\"}";
	}
}
