package com.ewpratten.client_ping.logic;

import java.util.regex.Pattern;

import org.jetbrains.annotations.Nullable;

import com.ewpratten.client_ping.Globals;
import com.ewpratten.client_ping.util.DimensionPosition;

public record Ping(String owner, DimensionPosition position, long timestamp) {

	private static final Pattern DESERIALIZATION_PATTERN = Pattern.compile("Ping at \\{(.+)\\}",
			Pattern.CASE_INSENSITIVE);

	// Serialize a ping in chat-ready format
	public String serializeForChat() {
		return String.format("Ping at {%s}", this.position.toString());
	}

	// Deserialize a ping from a string and owner
	public static @Nullable Ping deserialize(String serialized, String owner) {
		var matcher = DESERIALIZATION_PATTERN.matcher(serialized);
		if (matcher.matches()) {
			try {
				return new Ping(owner, DimensionPosition.fromString(matcher.group(1)), System.currentTimeMillis());
			} catch (IllegalArgumentException e) {
				Globals.LOGGER.error("Failed to deserialize ping", e);
				return null;
			}
		} else {
			return null;
		}
	}
}
