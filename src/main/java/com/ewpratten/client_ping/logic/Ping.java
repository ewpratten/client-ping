package com.ewpratten.client_ping.logic;

import java.util.regex.Pattern;

import org.jetbrains.annotations.Nullable;

import com.ewpratten.client_ping.Globals;

import net.minecraft.util.math.Vec3i;
import xaero.common.minimap.waypoints.Waypoint;
import lombok.Getter;

public class Ping extends Waypoint {
	@Getter
	private final String owner;
	@Getter
	private final String dimension;
	@Getter
	private final Vec3i position;
	@Getter
	private final long timestamp;

	public Ping(String owner, String dimension, Vec3i position, long timestamp) {
		// Pass data to super class
		super(
				// Waypoint position
				position.getX(), position.getY(), position.getZ(),
				// Waypoint name
				String.format("Ping: %s", owner),
				// Icon
				"!",
				// Color code
				14,
				// Temporary
				0, true);

		// Save key data for later
		this.owner = owner;
		this.dimension = dimension;
		this.position = position;
		this.timestamp = timestamp;
	}

	@Override
	public String toString() {
		return String.format("Ping { owner: %s, dimension: %s, position: %s, timestamp: %d }", this.owner,
				this.dimension, this.position.toString(), this.timestamp);
	}

	// private static final Pattern DESERIALIZATION_PATTERN = Pattern.compile("Ping
	// at \\{(.+)\\}",
	// Pattern.CASE_INSENSITIVE);

	// // Serialize a ping in chat-ready format
	// public String serializeForChat() {
	// return String.format("Ping at {%s}", this.position.toString());
	// }

	// // Deserialize a ping from a string and owner
	// public static @Nullable Ping deserialize(String serialized, String owner) {
	// var matcher = DESERIALIZATION_PATTERN.matcher(serialized);
	// if (matcher.matches()) {
	// try {
	// return new Ping(owner, DimensionPosition.fromString(matcher.group(1)),
	// System.currentTimeMillis());
	// } catch (IllegalArgumentException e) {
	// Globals.LOGGER.error("Failed to deserialize ping", e);
	// return null;
	// }
	// } else {
	// return null;
	// }
	// }
}
