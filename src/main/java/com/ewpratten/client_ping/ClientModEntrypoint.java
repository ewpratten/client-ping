package com.ewpratten.client_ping;

import org.quiltmc.qsl.lifecycle.api.client.event.ClientLifecycleEvents;

import com.ewpratten.client_ping.logic.PingDispatcher;

import net.fabricmc.api.ClientModInitializer;
import net.minecraft.network.message.MessageType;

public class ClientModEntrypoint implements ClientModInitializer {

	// Ping dispatcher (handles ping creation from this client)
	private PingDispatcher pingDispatcher;

	@Override
	public void onInitializeClient() {
		Globals.LOGGER.info("Running client-side initialization");

		// Create and register the ping dispatcher
		this.pingDispatcher = new PingDispatcher();
		this.pingDispatcher.registerCallbacks();

	}
}
