package com.ewpratten.client_ping.logic;

import java.util.ArrayList;
import java.util.Arrays;

import com.ewpratten.client_ping.Globals;
import com.ewpratten.client_ping.util.TickBasedScheduledTask;

import xaero.common.XaeroMinimapSession;
import xaero.common.minimap.waypoints.WaypointSet;
import xaero.common.minimap.waypoints.WaypointWorld;
import xaero.common.minimap.waypoints.WaypointsManager;

public class PingRegistry {

	// Instance
	private static PingRegistry instance;

	// A list of all pings currently active
	private ArrayList<Ping> pings = new ArrayList<Ping>();

	// Refresh job
	private TickBasedScheduledTask refreshTask;

	private PingRegistry() {
		// Spawn a job that calls refreshWaypoints every second
		this.refreshTask = new TickBasedScheduledTask(() -> {
			this.refreshWaypoints();
		}, 1000);
	}

	public static PingRegistry getInstance() {
		if (instance == null) {
			instance = new PingRegistry();
		}
		return instance;
	}

	private long getMaxPingLifetime(){
		return Globals.CONFIG.pingDisplayTime() * 1000;
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
			if (now - p.timestamp() > this.getMaxPingLifetime()) {
				Globals.LOGGER.debug("Removing old ping from " + p.owner());
				markedForRemoval.add(p);
			}
		}
		this.pings.removeAll(markedForRemoval);

		// Track the new ping
		this.pings.add(ping);

		// Refresh the waypoint list
		this.refreshWaypoints();
	}

	private void refreshWaypoints() {
		// Get the waypoint manager
		XaeroMinimapSession currentSession = XaeroMinimapSession.getCurrentSession();
		if (currentSession != null) {
			WaypointsManager manager = currentSession.getWaypointsManager();
			if (manager == null) {
				Globals.LOGGER.error("Failed to get waypoint manager");
				return;
			}

			// Get the waypoint set for the current world
			WaypointWorld currentWorld = manager.getCurrentWorld();
			if (currentWorld == null) {
				Globals.LOGGER.error("Failed to get current world");
				return;
			}
			WaypointSet waypoints = currentWorld.getCurrentSet();

			// Get a list of all active pings
			long now = System.currentTimeMillis();
			ArrayList<Ping> activePings = this.pings.stream().filter(p -> now - p.timestamp() < this.getMaxPingLifetime())
					.collect(ArrayList::new, ArrayList::add, ArrayList::addAll);

			// Remove any stale pings
			waypoints.getList().removeIf(waypoint -> (waypoint instanceof PingWaypoint)
					&& !activePings.contains(((PingWaypoint) waypoint).inner));

			// Add any new pings
			for (Ping ping : activePings) {
				// If the waypoint already exists, skip it
				if (waypoints.getList().stream().anyMatch(waypoint -> (waypoint instanceof PingWaypoint)
						&& ((PingWaypoint) waypoint).inner.equals(ping))) {
					continue;
				}

				// Add the waypoint
				PingWaypoint pw = new PingWaypoint(ping);
				waypoints.getList().add(pw);
			}
		}
	}

}
