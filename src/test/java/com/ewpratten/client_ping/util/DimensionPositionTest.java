package com.ewpratten.client_ping.util;

import org.junit.jupiter.api.Test;

import net.minecraft.util.math.Vec3i;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DimensionPositionTest {

	@Test
	public void testToString() {
		DimensionPosition pos = new DimensionPosition("overworld", new Vec3i(1, 2, 3));
		assertEquals("overworld:(1, 2, 3)", pos.toString());
	}

	@Test
	public void testFromString() {
		DimensionPosition pos = new DimensionPosition("overworld", new Vec3i(1, 2, 3));
		assertEquals(pos, pos.fromString("overworld:(1, 2, 3)"));
	}

}
