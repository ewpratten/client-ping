package com.ewpratten.client_ping.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.ChatMessageTag;
import net.minecraft.text.Text;
import net.minecraft.network.message.MessageSignature;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.ewpratten.client_ping.Globals;
import com.ewpratten.client_ping.logic.Ping;
import com.ewpratten.client_ping.logic.PingRegistry;

@Mixin(ChatHud.class)
public class InGameHudMixin {

	@Inject(at = @At("HEAD"), method = "Lnet/minecraft/client/gui/hud/ChatHud;addMessage(Lnet/minecraft/text/Text;Lnet/minecraft/network/message/MessageSignature;Lnet/minecraft/client/gui/hud/ChatMessageTag;)V", cancellable = true)
	public void addMessage(Text message, MessageSignature signature, ChatMessageTag tag, CallbackInfo info) {

		// Get as a regular string
		String messageString = message.getString();

		// If there aren't enough parts, don't do anything
		if (messageString.split(" ").length < 2) {
			return;
		}

		// Try to parse the username from the first chunk
		String username = messageString.split(" ")[0];
		username = username.substring(1, username.length() - 1);

		// Ignore messages from the current player
		MinecraftClient mc = MinecraftClient.getInstance();
		if (username.equals(mc.player.getName().getString())) {
			Globals.LOGGER.info("Dropping ping message from self");
			return;
		}

		// The remainder of the message might be a ping message
		String chatBody = messageString.split(" ", 2)[1];
		Ping parseResult = Ping.deserialize(chatBody, username);
		if (parseResult == null) {
			return;
		}

		// We have a ping message.
		Globals.LOGGER.info("Received ping message from " + username + " at " + parseResult.position().toString());

		// Store in the registry
		PingRegistry.getInstance().register(parseResult);
	}

}
