// Copyright (c) 2014 MediaMiser Ltd. All rights reserved.
package com.mediamiser.client;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.security.Principal;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.glassfish.jersey.jdkhttp.JdkHttpServerFactory;
import org.glassfish.jersey.jsonp.JsonProcessingFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.oauth1.DefaultOAuth1Provider;
import org.glassfish.jersey.server.oauth1.OAuth1ServerFeature;

import com.google.common.collect.Sets;
import com.mediamiser.resource.Topics;
import com.sun.net.httpserver.HttpServer;

public class SampleClientTest {

	private static final Logger			LOG					= LoggerFactory.getLogger(SampleClientTest.class);

	protected static final String		PROPERTY_FILENAME	= "configuration.properties";
	protected static final String		PROPERTY_URI		= "uri.mediamiser";
	protected static final int			DELAY_SECONDS		= 0;
	protected static final URI			URI;
	protected static final Properties	PROPERTIES;

	protected static HttpServer			SERVER				= null;

	static {
		PROPERTIES = loadProperties(PROPERTY_FILENAME);
		URI = UriBuilder.fromUri(PROPERTIES.getProperty(PROPERTY_URI))
			.build();
	}

	@BeforeClass
	public static void createAndStartServer() throws IOException {

		// If the URI is for a local server, create a local server
		if ("localhost".equals(URI.getHost())) {

			// Configure
			final ResourceConfig configuration = new ResourceConfig(Topics.class);
			configuration.register(createOAuth1ServerFeature());
			configuration.register(JsonProcessingFeature.class);

			// Create and start
			LOG.info("Starting HTTP server at {}", URI);
			SERVER = JdkHttpServerFactory.createHttpServer(URI, configuration);

		} else {
			LOG.info("Skipping HTTP server startup; contacting external server at {}", URI);
		}
	}

	@AfterClass
	public static void stopServer() throws IOException {
		if (SERVER != null) {
			LOG.info("Stopping HTTP server");
			SERVER.stop(DELAY_SECONDS);
		}
	}

	protected static OAuth1ServerFeature createOAuth1ServerFeature() throws IOException {

		// Create and configure provider
		final DefaultOAuth1Provider oAuthProvider = new DefaultOAuth1Provider();

		// Load properties for the sample consumer
		final String consumerName = "sample";
		final String consumerKey = PROPERTIES.getProperty(SampleClient.PROPERTY_CONSUMER_KEY);
		final String consumerSecret = PROPERTIES.getProperty(SampleClient.PROPERTY_CONSUMER_SECRET);
		final String accessTokenKey = PROPERTIES.getProperty(SampleClient.PROPERTY_ACCESS_TOKEN_KEY);
		final String accessTokenSecret = PROPERTIES.getProperty(SampleClient.PROPERTY_ACCESS_TOKEN_SECRET);
		final Set<String> roles = Sets.newHashSet("user");

		// These are not needed for this simple example OAuth1.0a server
		final String callbackUrl = null;
		final Principal principal = null;
		final MultivaluedMap<String, String> attributes = new MultivaluedHashMap<String, String>();

		// Register the consumer and access token
		oAuthProvider.registerConsumer(consumerName, consumerKey, consumerSecret, attributes);
		oAuthProvider.addAccessToken(accessTokenKey, accessTokenSecret, consumerKey, callbackUrl, principal, roles, attributes);

		// Create and return configured feature
		return new OAuth1ServerFeature(oAuthProvider);
	}

	protected static Properties loadProperties(final String filepath) {
		final Properties properties = new Properties();
		try {
			final InputStream stream = Thread.currentThread()
				.getContextClassLoader()
				.getResourceAsStream(filepath);
			properties.load(stream);
		} catch (IOException e) {
			throw new Error("Could not load properties from " + filepath);
		}
		return properties;
	}

	@Test
	public void testTopics() throws IOException, InterruptedException {

		// Create topics resource URI
		final URI uri = UriBuilder.fromUri(URI)
			.path(Topics.class)
			.build();

		// Obtain the client's response
		final Response response = new SampleClient(uri, PROPERTIES).getTopics();

		// Test the response
		final String message = String.format("Server responded with HTTP %d: %s", response.getStatus(), response.getStatusInfo()
			.getReasonPhrase());
		Assert.assertEquals(message, response.getStatus(), Response.Status.OK.getStatusCode());

		// Parse the response
		LOG.info("Request succeeded with HTTP status code {}: {}", response.getStatus(), response.getStatusInfo()
			.getReasonPhrase());

		// If the response was successful, view the topics returned
		final List<JsonObject> topics = response.readEntity(JsonArray.class)
			.getValuesAs(JsonObject.class);
		LOG.info("Found {} topics", topics.size());
		Assert.assertTrue("At least one topic should be returned", topics.size() > 0);

		// Log each topic ID
		for (JsonObject object : topics) {
			final String id = object.getString("id");
			LOG.info("Found topic id={}", id);
		}
	}

}
