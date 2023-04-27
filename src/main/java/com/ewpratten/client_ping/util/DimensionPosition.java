package com.ewpratten.client_ping.util;

import java.util.regex.Pattern;

import net.minecraft.util.math.Vec3i;

// Represents a position in a specific dimension
public record DimensionPosition(String dimension, Vec3i position) {

	private static final Pattern DESERIALIZATION_PATTERN = Pattern.compile("(.+):\\((\\d+), (\\d+), (\\d+)\\)",
			Pattern.CASE_INSENSITIVE);

	public int getX() {
		return this.position().getX();
	}

	public int getY() {
		return this.position().getY();
	}

	public int getZ() {
		return this.position().getZ();
	}

	@Override
	public String toString() {
		return String.format("%s:(%d, %d, %d)", this.dimension(), this.position().getX(), this.position().getY(),
				this.position().getZ());
	}

	public static DimensionPosition fromString(String string) throws IllegalArgumentException {
		var matcher = DESERIALIZATION_PATTERN.matcher(string);
		if (matcher.matches()) {
			return new DimensionPosition(matcher.group(1), new Vec3i(Integer.parseInt(matcher.group(2)),
					Integer.parseInt(matcher.group(3)), Integer.parseInt(matcher.group(4))));
		} else {
			throw new IllegalArgumentException("Invalid string format");
		}
	}
}
