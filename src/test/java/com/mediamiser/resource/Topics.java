// Copyright (c) 2014 MediaMiser Ltd. All rights reserved.
package com.mediamiser.resource;

import javax.annotation.security.RolesAllowed;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * Web APIs to manipulate Topics.
 * 
 * @author Chris Fournier <chris.fournier@mediamiser.com>
 */
@Path("/experimental/topics")
@RolesAllowed("user")
public class Topics {
	
	public static final int TOPIC_COUNT = 10;

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public JsonArray get() throws Exception {
		final JsonArrayBuilder topics = Json.createArrayBuilder();
		
		for (int i = 0; i < TOPIC_COUNT; i++) {
			topics.add(Json.createObjectBuilder().add("id", "topic" + Integer.toString(i)).build());
		}

		return topics.build();
	}

}
