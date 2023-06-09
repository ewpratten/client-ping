package com.ewpratten.client_ping.logic;

import java.util.ArrayList;

import org.jetbrains.annotations.Nullable;

import com.ewpratten.client_ping.Globals;

import xaero.common.XaeroMinimapSession;
import xaero.common.minimap.waypoints.WaypointSet;
import xaero.common.minimap.waypoints.WaypointWorld;
import xaero.common.minimap.waypoints.WaypointsManager;

public class XaeroBridge {

	/**
	 * Gets access to the internal Xaero object that manages waypoints for the
	 * current world
	 *
	 * @return The current waypoint set, or null if it could not be found
	 */
	private static @Nullable WaypointSet getCurrentWaypointSet() {
		// Try to get the current minimap session
		XaeroMinimapSession currentSession = XaeroMinimapSession.getCurrentSession();
		if (currentSession == null) {
			return null;
		}

		// Try to get the waypoint manager
		WaypointsManager manager = currentSession.getWaypointsManager();
		if (manager == null) {
			Globals.LOGGER.error("Failed to get waypoint manager");
			return null;
		}

		// Try to get the waypoint set for the current world
		WaypointWorld currentWorld = manager.getCurrentWorld();
		if (currentWorld == null) {
			Globals.LOGGER.error("Failed to get current world");
			return null;
		}

		return currentWorld.getCurrentSet();
	}

	/**
	 * Syncs all active pings to Xaero's Minimap for rendering
	 */
	public static void sync() {

		// Get a list of all active pings in the player's current dimension
		ArrayList<Ping> activePings = PingRegistry.getInstance().getActivePingsInCurrentDimension();

		// Get the current world's waypoint set
		WaypointSet waypoints = getCurrentWaypointSet();
		if (waypoints == null) {
			return;
		}

		// Remove all waypoints that are missing a corresponding ping
		waypoints.getList().removeIf(waypoint -> (waypoint instanceof PingWaypoint)
				&& !activePings.contains(((PingWaypoint) waypoint).inner));

		// Add all pings that are missing a corresponding waypoint
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
