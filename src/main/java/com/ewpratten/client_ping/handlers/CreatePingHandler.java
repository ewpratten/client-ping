package com.ewpratten.client_ping.handlers;

import org.lwjgl.glfw.GLFW;
import org.quiltmc.qsl.lifecycle.api.client.event.ClientTickEvents.End;

import com.ewpratten.client_ping.Globals;
import com.ewpratten.client_ping.logic.Ping;
import com.ewpratten.client_ping.logic.PingRegistry;
import com.ewpratten.client_ping.util.PingSerializer;
import com.mojang.blaze3d.platform.InputUtil;

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBind;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.RaycastContext.FluidHandling;
import net.minecraft.world.RaycastContext.ShapeType;
import net.minecraft.util.hit.HitResult;

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
		// Find the block coordinate of the player's crosshair raycast
		Vec3d cameraPosition = this.mc.player.getCameraPosVec(1.0F);
		Vec3d cameraRay1000Blocks = this.mc.player.getRotationVec(1.0F).multiply(1000.0D).add(cameraPosition);
		BlockHitResult hitResult = this.mc.world.raycast(new RaycastContext(cameraPosition, cameraRay1000Blocks,
				ShapeType.COLLIDER, FluidHandling.NONE, this.mc.player));
		Vec3i hitPosition = new Vec3i((int) Math.floor(hitResult.getPos().x), (int) Math.floor(hitResult.getPos().y),
				(int) Math.floor(hitResult.getPos().z));

		// If there is no hit, skip
		if (hitResult.getType() == HitResult.Type.MISS) {
			return;
		}

		// The hit position is always too high
		hitPosition = new Vec3i(hitPosition.getX(), hitPosition.getY() - 1, hitPosition.getZ());

		// Create a ping object
		this.lastPingTimestamp = System.currentTimeMillis();
		String currentDimension = this.mc.world.getRegistryKey().getValue().toString();
		Ping ping = new Ping(this.mc.player.getName().getString(),
				currentDimension, hitPosition, lastPingTimestamp);

		// Broadcast the ping
		Globals.LOGGER.info("Player pinged at " + hitPosition.toString());
		String serialized = PingSerializer.serialize(ping);

		// // Create and send a chat message to the server
		// this.mc.getNetworkHandler().sendChatMessage(chatMessage);

		// Inform the registry of the new ping
		PingRegistry.getInstance().register(ping);
	}

	@Override
	public void close() throws Exception {
		this.mc.close();
	}

}
