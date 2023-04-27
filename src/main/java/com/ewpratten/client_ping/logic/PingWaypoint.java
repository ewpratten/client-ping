package com.ewpratten.client_ping.logic;

import xaero.common.minimap.waypoints.Waypoint;

public class PingWaypoint extends Waypoint {
	public Ping inner;

	public PingWaypoint(Ping ping) {
		super((int) ping.position().getX(), (int) ping.position().getY(), (int) ping.position().getZ(),
				String.format("Ping: %s", ping.owner()), "!", 14, 0,
				true);
		this.inner = ping;
	}
}
