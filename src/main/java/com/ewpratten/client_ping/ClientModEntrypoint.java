package com.ewpratten.client_ping;

import java.util.EnumSet;

import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.client.ClientModInitializer;
import org.quiltmc.qsl.chat.api.QuiltChatEvents;
import org.quiltmc.qsl.chat.api.QuiltMessageType;
import org.quiltmc.qsl.lifecycle.api.client.event.ClientTickEvents;

import com.ewpratten.client_ping.handlers.CreatePingHandler;
import com.ewpratten.client_ping.handlers.InboundChatHandler;
import com.ewpratten.client_ping.logic.XaeroBridge;
import com.ewpratten.client_ping.util.TickBasedScheduledTask;

public class ClientModEntrypoint implements ClientModInitializer {

	@Override
	public void onInitializeClient(ModContainer mod) {
		Globals.LOGGER.info("Running client-side initialization");

		// Hook in to various parts of the game
		Globals.LOGGER.info("Registering event handlers");
		ClientTickEvents.END.register(new CreatePingHandler());
		QuiltChatEvents.CANCEL.register(EnumSet.of(QuiltMessageType.CHAT), new InboundChatHandler());

		// Occasionally re-sync between the ping registry and Xaero's Minimap
		new TickBasedScheduledTask(() -> {
			XaeroBridge.sync();
		}, 1000);

	}

}
