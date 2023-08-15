package com.ewpratten.client_ping.util;

import org.jetbrains.annotations.Nullable;

import net.minecraft.client.MinecraftClient;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.RaycastContext.FluidHandling;
import net.minecraft.world.RaycastContext.ShapeType;

public class RayTrace {

	public static @Nullable Vec3i getRayTracedHitTarget(MinecraftClient client) {
		// Find the block coordinate of the player's crosshair raycast
		Vec3d cameraPosition = client.player.getCameraPosVec(1.0F);
		Vec3d cameraRay1000Blocks = client.player.getRotationVec(1.0F).multiply(1000.0D).add(cameraPosition);
		BlockHitResult hitResult = client.world.raycast(new RaycastContext(cameraPosition, cameraRay1000Blocks,
				ShapeType.COLLIDER, FluidHandling.NONE, client.player));
		Vec3i hitPosition = new Vec3i((int) Math.floor(hitResult.getPos().x), (int) Math.floor(hitResult.getPos().y),
				(int) Math.floor(hitResult.getPos().z));

		// If there is no hit, skip
		if (hitResult.getType() == HitResult.Type.MISS) {
			return null;
		}

		// The hit position is always too high
		return new Vec3i(hitPosition.getX(), hitPosition.getY() - 1, hitPosition.getZ());

	}

}
