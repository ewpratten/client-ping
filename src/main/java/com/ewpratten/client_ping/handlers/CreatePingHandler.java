package com.ewpratten.client_ping.handlers;

import org.lwjgl.glfw.GLFW;
import org.quiltmc.qsl.lifecycle.api.client.event.ClientTickEvents.End;

import com.ewpratten.client_ping.Globals;
import com.ewpratten.client_ping.communication.PartyManager;
import com.ewpratten.client_ping.logic.Ping;
import com.ewpratten.client_ping.logic.PingRegistry;
import com.ewpratten.client_ping.util.RayTrace;
import com.mojang.blaze3d.platform.InputUtil;

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBind;
import net.minecraft.util.math.Vec3i;

public class CreatePingHandler implements End, AutoCloseable {

	// Handle on the client
	private final MinecraftClient mc;

	// Key bind for ping creation
	private final KeyBind keyBind;

	// Timestamp of the last ping (used for client-side rate limiting)
	private long lastPingTimestamp = 0;

	public CreatePingHandler() {
		this.mc = MinecraftClient.getInstance();

		// Register ping creation key bind
		this.keyBind = KeyBindingHelper.registerKeyBinding(new KeyBind("key.client_ping.ping",
				InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_Z, "category.client_ping.main"));

	}

	@Override
	public void endClientTick(MinecraftClient client) {
		// Check the time
		long currentTimeMillis = System.currentTimeMillis();

		// If the key is pressed and we are outside of the cooldown
		while (this.keyBind.wasPressed()
				&& (currentTimeMillis - this.lastPingTimestamp > Globals.getMinPingInterval())) {
			Globals.LOGGER.info("Ping button pressed (cooldown passed)");
			this.createPing();
		}
	}

	private void createPing() {

		// Get the hit position
		Vec3i hitPosition = RayTrace.getRayTracedHitTarget(this.mc);
		if (hitPosition == null) {
			Globals.LOGGER.info("User tried pinging nothing");
			return;
		}

		// Create a ping object
		this.lastPingTimestamp = System.currentTimeMillis();
		String currentDimension = this.mc.world.getRegistryKey().getValue().toString();
		Ping ping = new Ping(this.mc.player.getName().getString(),
				currentDimension, hitPosition, lastPingTimestamp);

		// Broadcast the ping
		Globals.LOGGER.info("Player pinged at " + hitPosition.toString());
		String serialized = ping.serialize();

		// Inform the registry of the new ping
		PingRegistry.getInstance().register(ping);

		// Send the ping to all party members
		PartyManager.getInstance().getCurrentParty().broadcastChatToAllMembers(this.mc, serialized);
	}

	@Override
	public void close() throws Exception {
		this.mc.close();
	}

}
