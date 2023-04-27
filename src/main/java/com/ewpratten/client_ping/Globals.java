package com.ewpratten.client_ping;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

public class Globals {

	// Global logger
	public static final Logger LOGGER = LoggerFactory.getLogger("Client Ping");

	// Global config data
	public static final ClientPingConfig CONFIG = ClientPingConfig.createAndLoad();

}
