package com.ewpratten.client_ping.logic;

import org.lwjgl.glfw.GLFW;
import org.quiltmc.qsl.lifecycle.api.client.event.ClientTickEvents;

import com.ewpratten.client_ping.Globals;
import com.mojang.blaze3d.platform.InputUtil;

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBind;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.RaycastContext.FluidHandling;
import net.minecraft.world.RaycastContext.ShapeType;

public class PingDispatcher {

	private MinecraftClient client;
	private PingRegistry registry = PingRegistry.getInstance();
	private KeyBind keyBind;
	private long lastPingTimestamp = 0;
	private final long PING_COOLDOWN = 1000;

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
	public void registerCallbacks() {
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
		Vec3d cameraPosition = this.client.player.getCameraPosVec(1.0F);
		Vec3d cameraRay1000Blocks = this.client.player.getRotationVec(1.0F).multiply(1000.0D).add(cameraPosition);
		BlockHitResult hitResult = this.client.world.raycast(new RaycastContext(cameraPosition, cameraRay1000Blocks,
				ShapeType.COLLIDER, FluidHandling.NONE, this.client.player));
		Vec3d hitPosition = hitResult.getPos();

		// If there is no hit, skip
		if (hitResult.getType() == HitResult.Type.MISS) {
			return;
		}

		// Floor the hit position
		hitPosition = new Vec3d(Math.floor(hitPosition.getX()), Math.floor(hitPosition.getY()),
				Math.floor(hitPosition.getZ()));

		// The hit position is always too high
		hitPosition = new Vec3d(hitPosition.getX(), hitPosition.getY() - 1, hitPosition.getZ());

		// Create a ping object
		this.lastPingTimestamp = System.currentTimeMillis();
		Ping ping = new Ping(this.client.player.getName().getString(), hitPosition, lastPingTimestamp);

		// Broadcast the ping
		Globals.LOGGER.info("Player pinged at " + hitPosition.toString());
		String chatMessage = ping.serializeForChat();

		// Create and send a chat message to the server
		this.client.getNetworkHandler().sendChatMessage(chatMessage);

		// Inform the registry of the new ping
		this.registry.register(ping);

	}

}
