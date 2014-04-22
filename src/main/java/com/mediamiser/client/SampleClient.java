// Copyright (c) 2014 MediaMiser Ltd. All rights reserved.
package com.mediamiser.client;

import java.io.IOException;
import java.net.URI;
import java.util.Properties;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Feature;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.client.Client;

import org.glassfish.jersey.client.oauth1.AccessToken;
import org.glassfish.jersey.client.oauth1.ConsumerCredentials;
import org.glassfish.jersey.client.oauth1.OAuth1ClientSupport;

/**
 * A sample <a href="http://oauthbible.com/#oauth-10a-one-legged">one-legged OAuth1.0a</a> client using Jersey for MediaMiser's RESTful APIs.
 * 
 * @author Chris Fournier <chris.fournier@mediamiser.com>
 */
public class SampleClient {

	protected static final String	PROPERTY_CONSUMER_KEY			= "consumer.key";
	protected static final String	PROPERTY_CONSUMER_SECRET		= "consumer.secret";
	protected static final String	PROPERTY_ACCESS_TOKEN_KEY		= "access.token.key";
	protected static final String	PROPERTY_ACCESS_TOKEN_SECRET	= "access.token.secret";

	protected final Client			client;
	protected final URI				uri;

	/**
	 * Create a sample client for a specific URI.
	 * 
	 * @param uri
	 *            URI of the RESTful API that will serve requests.
	 * @throws IOException
	 *             thrown if the properties file cannot be read.
	 */
	public SampleClient(final URI uri, final Properties properties) throws IOException {

		// Set URI
		this.uri = uri;

		// Load properties
		final String consumerKey = properties.getProperty(PROPERTY_CONSUMER_KEY);
		final String consumerSecret = properties.getProperty(PROPERTY_CONSUMER_SECRET);
		final String accessTokenKey = properties.getProperty(PROPERTY_ACCESS_TOKEN_KEY);
		final String accessTokenSecret = properties.getProperty(PROPERTY_ACCESS_TOKEN_SECRET);

		// Create credentials
		final ConsumerCredentials consumerCredentials = new ConsumerCredentials(consumerKey.trim(), consumerSecret.trim());
		final AccessToken accessToken = new AccessToken(accessTokenKey.trim(), accessTokenSecret.trim());

		// Build OAuth1.0a feature from credentials
		final Feature filterFeature = OAuth1ClientSupport.builder(consumerCredentials)
			.feature()
			.accessToken(accessToken)
			.build();

		// Create OAuth1.0a client
		client = ClientBuilder.newClient();
		client.register(filterFeature);
	}

	/**
	 * Creates and executes a request to get the list of topics that are owned by this authenticated client.
	 * 
	 * @return the server's response this request
	 */
	public Response getTopics() {
		return client.target(uri)
			.request(MediaType.APPLICATION_JSON_TYPE)
			.get();
	}

}
