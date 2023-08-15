package com.ewpratten.client_ping.logic;

import java.util.ArrayList;

import com.ewpratten.client_ping.Globals;
import com.ewpratten.client_ping.util.XaeroBridge;

import net.minecraft.client.MinecraftClient;

public class PingRegistry {

	// Instance
	private static PingRegistry instance;

	// A list of all pings currently active
	private ArrayList<Ping> pings = new ArrayList<Ping>();

	private PingRegistry() {
	}

	public static PingRegistry getInstance() {
		if (instance == null) {
			instance = new PingRegistry();
		}
		return instance;
	}

	/**
	 * Tracks a new ping with the registry
	 *
	 * @param ping Ping to register
	 */
	public synchronized void register(Ping ping) {

		// Perform pre-flight cleanup jobs
		long now = System.currentTimeMillis();
		ArrayList<Ping> markedForRemoval = new ArrayList<Ping>();
		for (Ping p : this.pings) {

			// If the user already has a ping, remove the old one
			if (p.getOwner().equals(ping.getOwner())) {
				// If the other user is spamming too fast, we can just drop the ping
				if (now - p.getTimestamp() < Globals.getMinPingInterval()) {
					Globals.LOGGER.debug("Ignoring new ping from " + p.getOwner() + " (too fast)");
					return;
				}

				// Remove the old ping
				Globals.LOGGER.debug("Removing old ping from " + p.getOwner());
				markedForRemoval.add(p);
			}

			// Instead of running a separate prune job, we can just ignore old pings in
			// other parts of the codebase, and quickly drop everything passed the max
			// lifetime here
			if (now - p.getTimestamp() > Globals.getMaxPingLifetime()) {
				Globals.LOGGER.debug("Removing old ping from " + p.getOwner());
				markedForRemoval.add(p);
			}
		}
		this.pings.removeAll(markedForRemoval);

		// Track the new ping
		this.pings.add(ping);

		// Force a re-sync with Xaero's Minimap
		XaeroBridge.sync();
	}

	/**
	 * Gets a list of all currently alive pings in a dimension
	 *
	 * @param dimension Dimension to search in
	 * @return All pings that were created in the last {@link #getMaxPingLifetime()}
	 *         milliseconds
	 */
	public ArrayList<Ping> getActivePingsForDimension(String dimension) {
		long now = System.currentTimeMillis();
		return this.pings.stream()
				// Only get recent pings
				.filter(p -> now - p.getTimestamp() < Globals.getMaxPingLifetime())
				// Only get pings in the correct dimension
				.filter(p -> p.getDimension().equals(dimension))
				// Transform the stream into an arraylist
				.collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
	}

	/**
	 * Gets a list of all currently alive pings in the current dimension
	 *
	 * @return All pings that were created in the last {@link #getMaxPingLifetime()}
	 */
	public ArrayList<Ping> getActivePingsInCurrentDimension() {
		// Request the current dimension name from the Minecraft client
		MinecraftClient client = MinecraftClient.getInstance();
		if (client.world == null) {
			return new ArrayList<Ping>();
		}
		String currentDimension = client.world.getRegistryKey().getValue().toString();

		// Perform ping lookup
		return this.getActivePingsForDimension(currentDimension);
	}

}
