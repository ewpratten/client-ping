package com.ewpratten.client_ping.ping_logic;

import java.util.UUID;

import org.jetbrains.annotations.Nullable;

import net.minecraft.util.math.Vec3d;

public record Ping(UUID owner, Vec3d position, long timestamp) {

	// Serialize a ping in chat-ready format
	public String serializeForChat() {
		return String.format("Ping at {%.2f, %.2f, %.2f}", this.position.getX(), this.position.getY(), this.position.getZ());
	}

	// Deserialize a ping from a string and owner
	public @Nullable Ping deserialize(String serialized, UUID owner) {
		String[] parts = serialized.split(" ");
		if (parts.length != 5) {
			return null;
		}
		String[] coords = parts[3].split(",");
		if (coords.length != 3) {
			return null;
		}
		double x = Double.parseDouble(coords[0]);
		double y = Double.parseDouble(coords[1]);
		double z = Double.parseDouble(coords[2]);
		return new Ping(owner, new Vec3d(x, y, z), System.currentTimeMillis());
	}
}
