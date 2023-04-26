package com.ewpratten.client_ping.mixins;

import java.util.UUID;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.network.message.MessageType;
import net.minecraft.text.Text;

@Mixin(InGameHud.class)
public class MessageReceivedMixin {

	@Inject(method = "onChatMessage", at = @At("RETURN"), cancellable = true)
	public void onChatMessage(MessageType kind, Text message, UUID senderUuid, CallbackInfo callbackInfo) {

	}

}
