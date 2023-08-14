package com.ewpratten.client_ping;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

public class Globals {

	// Global logger
	public static final Logger LOGGER = LoggerFactory.getLogger("Client Ping");

	// Global config data
	public static final ClientPingConfig CONFIG = ClientPingConfig.createAndLoad();

	/**
	 * Gets the maximum number of milliseconds a ping may stay on screen for before
	 * being pruned
	 *
	 * @return Maximum lifetime in milliseconds
	 */
	public static long getMaxPingLifetime() {
		return Globals.CONFIG.pingDisplayTime() * 1000;
	}

	/**
	 * Gets the minimum number of milliseconds allowed between two pings by the same
	 * player
	 *
	 * @return Minimum ping interval
	 */
	public static long getMinPingInterval() {
		return Globals.CONFIG.minPingInterval() * 1000;
	}

}
