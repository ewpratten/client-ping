package com.ewpratten.client_ping.handlers;

import java.io.IOException;

import org.quiltmc.qsl.networking.api.client.ClientPlayConnectionEvents.Disconnect;

import com.ewpratten.client_ping.Globals;
import com.ewpratten.client_ping.communication.PartyManager;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;

public class PlayerDisconnectHandler implements Disconnect {

	@Override
	public void onPlayDisconnect(ClientPlayNetworkHandler handler, MinecraftClient client) {
		Globals.LOGGER.info("Player disconnected. Saving state..");

		// Write the current party info to disk
		try {
			PartyManager.getInstance().replicateCurrentPartyToDisk();
		} catch (IOException e) {
			Globals.LOGGER.error("Failed to save party state to disk", e);
		}
	}

}
