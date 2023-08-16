package com.ewpratten.client_ping;

import java.util.EnumSet;

import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.client.ClientModInitializer;
import org.quiltmc.qsl.chat.api.QuiltChatEvents;
import org.quiltmc.qsl.chat.api.QuiltMessageType;
import org.quiltmc.qsl.command.api.client.ClientCommandRegistrationCallback;
import org.quiltmc.qsl.lifecycle.api.client.event.ClientTickEvents;
import org.quiltmc.qsl.networking.api.client.ClientPlayConnectionEvents;

import com.ewpratten.client_ping.handlers.ClientSideCommandRegistrationCallback;
import com.ewpratten.client_ping.handlers.CreatePingHandler;
import com.ewpratten.client_ping.handlers.InboundChatHandler;
import com.ewpratten.client_ping.handlers.PlayerDisconnectHandler;
import com.ewpratten.client_ping.util.TickBasedScheduledTask;
import com.ewpratten.client_ping.util.XaeroBridge;

public class ClientModEntrypoint implements ClientModInitializer {

	@Override
	public void onInitializeClient(ModContainer mod) {
		Globals.LOGGER.info("Running client-side initialization");

		// Hook in to various parts of the game
		Globals.LOGGER.info("Registering event handlers");
		ClientTickEvents.END.register(new CreatePingHandler());
		QuiltChatEvents.CANCEL.register(EnumSet.of(QuiltMessageType.CHAT), new InboundChatHandler());
		ClientPlayConnectionEvents.DISCONNECT.register(new PlayerDisconnectHandler());
		ClientCommandRegistrationCallback.EVENT.register(new ClientSideCommandRegistrationCallback());

		// Occasionally re-sync between the ping registry and Xaero's Minimap
		new TickBasedScheduledTask(() -> {
			XaeroBridge.sync();
		}, 1000);

	}

}
