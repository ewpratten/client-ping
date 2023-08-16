package com.ewpratten.client_ping.handlers;

import org.quiltmc.qsl.chat.api.QuiltChatEvents.Cancel;
import org.quiltmc.qsl.chat.api.types.AbstractChatMessage;
import org.quiltmc.qsl.chat.api.types.ChatS2CMessage;

import com.ewpratten.client_ping.Globals;
import com.ewpratten.client_ping.communication.Party;
import com.ewpratten.client_ping.communication.PartyManager;
import com.ewpratten.client_ping.logic.Ping;
import com.ewpratten.client_ping.logic.PingRegistry;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;

/**
 * Handles inbound chat messages. If possible, extracts ping info from them and
 * processes the data
 */
public class InboundChatHandler implements Cancel, AutoCloseable {
	private final MinecraftClient mc;

	public InboundChatHandler() {
		this.mc = MinecraftClient.getInstance();
	}

	@Override
	public boolean shouldCancelMessage(AbstractChatMessage<?> abstractMessage) {

		// Get the player that sent the message
		PlayerEntity sender = abstractMessage.getPlayer();
		Globals.LOGGER.debug("Message from " + sender.getName().getString());

		// Check if the message is coming from another player
		if (abstractMessage instanceof ChatS2CMessage && sender != this.mc.player) {
			ChatS2CMessage message = (ChatS2CMessage) abstractMessage;
			String content = message.getBody().content();
			Globals.LOGGER.debug("Inbound message: " + content);

			// If the user only wants to accept pings from their party, filter the sender
			if (Globals.CONFIG.onlyAcceptPartyPings()) {
				Party currentParty = PartyManager.getInstance().getCurrentParty();
				if (!currentParty.isMember(sender.getUuid())) {
					Globals.LOGGER.info("Got ping from " + sender.getName() + " but they are not in the user's party");
					Globals.LOGGER.warn("Dropping unwanted ping silently");
					return !Globals.CONFIG.showPingsInChat();
				}
			}

			// If we can deserialize the message, put it in the registry
			Ping ping = Ping.deserialize(sender.getName().getString(), content);
			if (ping != null) {
				Globals.LOGGER.info("Got ping: " + ping);

				// Send the ping to the registry
				PingRegistry.getInstance().register(ping);

				// Handle weather or not to drop the message from the UI
				return !Globals.CONFIG.showPingsInChat();
			}
		}

		return false;
	}

	@Override
	public void close() throws Exception {
		mc.close();
	}

}
