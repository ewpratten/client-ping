package com.ewpratten.client_ping.ping_logic;

import java.util.ArrayList;

import com.ewpratten.client_ping.Globals;

public class PingRegistry {

	// Instance
	private static PingRegistry instance;

	// A list of all pings currently active
	private ArrayList<Ping> pings = new ArrayList<Ping>();
	private static final long MAX_PING_LIFETIME = 10000;

	private PingRegistry() {
	}

	public static PingRegistry getInstance() {
		if (instance == null) {
			instance = new PingRegistry();
		}
		return instance;
	}

	// Tracks a new ping in the registry
	public synchronized void register(Ping ping) {

		// Perform pre-flight cleanup jobs
		long now = System.currentTimeMillis();
		ArrayList<Ping> markedForRemoval = new ArrayList<Ping>();
		for (Ping p : this.pings) {

			// If the user already has a ping, remove the old one
			if (p.owner().equals(ping.owner())) {
				Globals.LOGGER.debug("Removing old ping from " + p.owner());
				markedForRemoval.add(p);
			}

			// Instead of running a separate prune job, we can just ignore old pings in
			// other parts of the codebase, and quickly drop everything passed the max
			// lifetime here
			if (now - p.timestamp() > MAX_PING_LIFETIME) {
				Globals.LOGGER.debug("Removing old ping from " + p.owner());
				markedForRemoval.add(p);
			}
		}
		this.pings.removeAll(markedForRemoval);

		// Add the new ping
		this.pings.add(ping);
	}

	// Get all active pings
	public ArrayList<Ping> getPings() {
		// Only return pings that are still active
		long now = System.currentTimeMillis();
		return this.pings.stream().filter(p -> now - p.timestamp() < MAX_PING_LIFETIME)
				.collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
	}

}
