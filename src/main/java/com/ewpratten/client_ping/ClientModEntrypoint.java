package com.ewpratten.client_ping;

import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.client.ClientModInitializer;

import com.ewpratten.client_ping.logic.PingDispatcher;

public class ClientModEntrypoint implements ClientModInitializer {

	// Ping dispatcher (handles ping creation from this client)
	private PingDispatcher pingDispatcher;

	@Override
	public void onInitializeClient(ModContainer mod) {
		Globals.LOGGER.info("Running client-side initialization");

		// Create and register the ping dispatcher
		this.pingDispatcher = new PingDispatcher();
		this.pingDispatcher.registerCallbacks();

	}

}
