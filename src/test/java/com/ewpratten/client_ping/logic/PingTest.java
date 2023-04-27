
package com.ewpratten.client_ping.logic;

import org.junit.jupiter.api.Test;

import com.ewpratten.client_ping.util.DimensionPosition;

import net.minecraft.util.math.Vec3i;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PingTest {

	@Test
	public void testSerializeForChat() {
		Ping ping = new Ping("ewpratten", new DimensionPosition("overworld", new Vec3i(1, 2, 3)), 1234567890);
		assertEquals("Ping at {overworld:(1, 2, 3)}", ping.serializeForChat());
	}

	@Test
	public void testDeserialize(){
		Ping deserializedPing = Ping.deserialize("Ping at {overworld:(1, 2, 3)}", "ewpratten");
		Ping testPing = new Ping("ewpratten", new DimensionPosition("overworld", new Vec3i(1, 2, 3)), deserializedPing.timestamp());

		assertEquals(testPing, deserializedPing);
	}
}
