package com.ewpratten.client_ping;

import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.ModInitializer;
import org.quiltmc.qsl.base.api.entrypoint.client.ClientModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClientPingMod implements ModInitializer, ClientModInitializer {

	// Global logger
	public static final Logger LOGGER = LoggerFactory.getLogger("Client Ping");

	// Ping dispatcher (handles ping creation from this client)
	private PingDispatcher pingDispatcher;

	@Override
	public void onInitialize(ModContainer mod) {
		LOGGER.info("Mod Loaded. Today will be a great day!");

	}

	@Override
	public void onInitializeClient(ModContainer container) {

		// Create and register the ping dispatcher
		this.pingDispatcher = new PingDispatcher();
		this.pingDispatcher.register();

	}

}
