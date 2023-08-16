package com.ewpratten.client_ping.logic;

import org.jetbrains.annotations.Nullable;

import com.ewpratten.client_ping.Globals;
import com.ewpratten.client_ping.proto.SerializablePing;
import com.github.fzakaria.ascii85.Ascii85;

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

	public Ping(String owner, SerializablePing sp) {
		this(owner, sp, System.currentTimeMillis());
	}

	public Ping(String owner, SerializablePing sp, long timestamp) {
		this(owner, sp.getDimension(), new Vec3i(sp.getX(), sp.getY(), sp.getZ()), timestamp);
	}

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
		return String.format("Ping[owner=%s, dimension=%s, position=%s, timestamp=%d]", this.owner,
				this.dimension, this.position.toString(), this.timestamp);
	}

	public String serialize() {
		// Stuff the data into a protobuf message
		SerializablePing sp = SerializablePing.newBuilder()
				.setDimension(this.dimension)
				.setX(this.position.getX())
				.setY(this.position.getY())
				.setZ(this.position.getZ())
				.build();

		// Base64 encode the message
		String encoded = Ascii85.encode(sp.toByteArray());

		// Build the output string
		return String.format("%s%s", PREAMBLE, encoded);
	}

	public static @Nullable Ping deserialize(String owner, String serialized) {
		// If the serialized string does not start with the preamble, it is not a ping
		if (!serialized.startsWith(PREAMBLE)) {
			return null;
		}

		// Remove the preamble from the serialized string
		String encoded = serialized.substring(PREAMBLE.length());

		// Decode the serialized string
		byte[] decoded = Ascii85.decode(encoded);

		// Parse the protobuf message
		SerializablePing sp;
		try {
			sp = SerializablePing.parseFrom(decoded);
		} catch (Exception e) {
			Globals.LOGGER.error("Failed to parse protobuf message", e);
			return null;
		}

		// Create a new ping object
		return new Ping(owner, sp);
	}

}
