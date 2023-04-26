package com.ewpratten.client_ping.ping_logic;

import org.lwjgl.glfw.GLFW;
import org.quiltmc.qsl.lifecycle.api.client.event.ClientTickEvents;

import com.ewpratten.client_ping.Globals;
import com.mojang.blaze3d.platform.InputUtil;

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBind;
import net.minecraft.text.Text;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class PingDispatcher {

	private MinecraftClient client;
	private PingRegistry registry = PingRegistry.getInstance();
	private KeyBind keyBind;
	private long lastPingTimestamp = 0;
	private final long PING_COOLDOWN = 5000;

	// Create a ping dispatcher and connect to the ping key bind
	public PingDispatcher() {
		// Register the default ping key bind
		Globals.LOGGER.info("Setting up ping key bind");
		this.keyBind = KeyBindingHelper.registerKeyBinding(new KeyBind("key.client_ping.ping",
				InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_Z, "category.client_ping.main"));

		// Get the client instance
		this.client = MinecraftClient.getInstance();
	}

	// Registers an event handler that does the local ping creation logic every time
	// the ping key is pressed
	public void register() {
		// Perform checks every tick
		Globals.LOGGER.info("Registering ping creation event handler");
		ClientTickEvents.END.register(client -> {
			// If the key is pressed and we are outside of the cooldown
			while (this.keyBind.wasPressed()
					&& (System.currentTimeMillis() - this.lastPingTimestamp > this.PING_COOLDOWN)) {
				this.createPing();
			}
		});
	}

	private void createPing() {
		// Find the block coordinate of the player's crosshair raycast
		HitResult hitResult = this.client.crosshairTarget;
		Vec3d hitPosition;

		switch (hitResult.getType()) {
			case BLOCK:
				BlockPos blockPosition = ((BlockHitResult) hitResult).getBlockPos();
				hitPosition = new Vec3d(blockPosition.getX(), blockPosition.getY(), blockPosition.getZ());
				break;
			case ENTITY:

				hitPosition = ((EntityHitResult) hitResult).getPos();
				break;
			default:
				// If we hit nothing, ping nothing
				Globals.LOGGER.info("Player tried to ping nothing");
				return;
		}

		// Create a ping object
		this.lastPingTimestamp = System.currentTimeMillis();
		Ping ping = new Ping(this.client.player.getUuid(), hitPosition, lastPingTimestamp);

		// Broadcast the ping
		Globals.LOGGER.info("Player pinged at " + hitPosition.toString());
		String chatMessage = ping.serializeForChat();
		this.client.player.sendMessage(Text.literal(chatMessage), false);

		// Inform the registry of the new ping
		this.registry.register(ping);

	}

}
