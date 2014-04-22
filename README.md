### About REST-client

[![Build Status](https://travis-ci.org/MediaMiser/rest-client.svg?branch=master)](https://travis-ci.org/MediaMiser/rest-client)
[![Coverage Status](https://coveralls.io/repos/MediaMiser/rest-client/badge.png)](https://coveralls.io/r/MediaMiser/rest-client)

REST-client is an example client for [MediaMiser](http://www.mediamiser.com/)'s [one-legged OAuth1.0a](http://oauthbible.com/#oauth-10a-one-legged) authenticated RESTful APIs.
The goal of this project is to simply demonstrate how to create a [Jersey](https://jersey.java.net/) 2.x-based Java client for accessing MediaMiser APIs.

### Requirements

To build this project, your development environment must have installed:

* [Oracle JDK 7]()
* [Apache Maven 3.x](http://maven.apache.org/)

### Usage

To test locally, one can simply run at a command prompt:

	mvn clean test

To test API credentials given to you by MediaMiser:

1. Edit [`src/main/resources/configuration.properties`](src/main/resources/configuration.properties) to contain your credentials and the MediaMiser API endpoint:

		uri.mediamiser		= https://rest.mediamiser.com/
		consumer.key		= <your consumer key>
		consumer.secret		= <your consumer secret>
		access.token.key	= <your access token key>
		access.token.secret	= <your access token secret>

1. Run at a command prompt:

		mvn clean test


### Licensing

REST-client is licensed under the [BSD 3-Clause license](LICENSE.md).
