package com.ewpratten.client_ping.logic;

import xaero.common.minimap.waypoints.Waypoint;

public class PingWaypoint extends Waypoint {
	public Ping inner;

	public PingWaypoint(Ping ping) {
		super((int) ping.position().x, (int) ping.position().y, (int) ping.position().z,
				String.format("Ping: %s", ping.owner()), "!", 14, 0,
				true);
		this.inner = ping;
	}
}
