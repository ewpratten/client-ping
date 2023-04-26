package com.ewpratten.client_ping.logic;

import java.util.Arrays;
import java.util.UUID;

import org.jetbrains.annotations.Nullable;

import com.ewpratten.client_ping.Globals;

import net.minecraft.util.math.Vec3d;

public record Ping(String owner, Vec3d position, long timestamp) {

	// Serialize a ping in chat-ready format
	public String serializeForChat() {
		return String.format("Ping at {%.2f, %.2f, %.2f}", this.position.getX(), this.position.getY(),
				this.position.getZ());
	}

	// Deserialize a ping from a string and owner
	public static @Nullable Ping deserialize(String serialized, String owner) {

		// Check if the message is a ping
		if (!serialized.startsWith("Ping at {") || !serialized.endsWith("}")) {
			Globals.LOGGER.info("BAD STRING FORMAT: " + serialized);
			return null;
		}

		// Remove the prefix and suffix
		serialized = serialized.substring(9, serialized.length() - 1);

		// Split into parts
		String[] parts = serialized.split(", ");
		Globals.LOGGER.info(Arrays.toString(parts));

		// Check if there are enough parts
		if (parts.length != 3) {
			return null;
		}

		// Parse the parts
		double x = Double.parseDouble(parts[0]);
		double y = Double.parseDouble(parts[1]);
		double z = Double.parseDouble(parts[2]);

		// Create the ping
		return new Ping(owner, new Vec3d(x, y, z), System.currentTimeMillis());
	}
}
