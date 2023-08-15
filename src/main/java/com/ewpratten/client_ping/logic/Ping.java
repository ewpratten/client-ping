package com.ewpratten.client_ping.logic;

import java.util.Base64;
import java.util.regex.Pattern;

import org.jetbrains.annotations.Nullable;

import com.ewpratten.client_ping.Globals;
import com.ewpratten.client_ping.communication.Party;
import com.google.gson.Gson;

import net.minecraft.util.math.Vec3i;
import xaero.common.minimap.waypoints.Waypoint;
import lombok.Getter;

public class Ping extends Waypoint {
	// Username of the player who created the ping
	@Getter
	private final String owner;
	// Name of the dimension the ping was created in
	@Getter
	private final String dimension;
	// Position of the ping
	@Getter
	private final Vec3i position;
	// Timestamp of the ping
	@Getter
	private final long timestamp;

	// Preamble used to identify serialized pings
	private static final String PREAMBLE = "CLP:";

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

	public Ping cloneWithNewTimestamp(long timestamp) {
		return new Ping(this.owner, this.dimension, this.position, timestamp);
	}

	@Override
	public String toString() {
		return String.format("Ping[owner=%s, dimension=%s, position=%s, timestamp=%d]", this.owner,
				this.dimension, this.position.toString(), this.timestamp);
	}

	public String serialize() {

		// Serialize and encode the object
		Gson gson = new Gson();
		String json = gson.toJson(this);
		String encoded = Base64.getEncoder().encodeToString(json.getBytes());

		// Build the output string
		return String.format("%s%s", PREAMBLE, encoded);
	}

	public static @Nullable Ping deserialize(String s) {
		// If the string doesn't start with the preamble, return null
		if (!s.startsWith(PREAMBLE)) {
			return null;
		}

		// Strip the preamble
		String stripped = s.substring(PREAMBLE.length());

		// Base64 decode
		byte[] decoded = Base64.getDecoder().decode(stripped);
		String json = new String(decoded);

		// Attempt to deserialize the payload
		Gson gson = new Gson();
		Ping ping = gson.fromJson(json, Ping.class);

		// If the ping is null, return null
		if (ping == null) {
			return null;
		}

		// Otherwise, we will override the timestamp to now
		// This prevents another user from spamming by crafting bad timestamps
		return ping.cloneWithNewTimestamp(System.currentTimeMillis());
	}

}
