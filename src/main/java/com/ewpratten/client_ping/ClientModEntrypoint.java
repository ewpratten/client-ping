package com.ewpratten.client_ping;

import com.ewpratten.client_ping.ping_logic.PingDispatcher;

import net.fabricmc.api.ClientModInitializer;

public class ClientModEntrypoint implements ClientModInitializer {

	// Ping dispatcher (handles ping creation from this client)
	private PingDispatcher pingDispatcher;

	@Override
	public void onInitializeClient() {
		Globals.LOGGER.info("Running client-side initialization");

		// Create and register the ping dispatcher
		this.pingDispatcher = new PingDispatcher();
		this.pingDispatcher.register();

	}

}
