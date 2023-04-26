package com.ewpratten.client_ping;

import org.lwjgl.glfw.GLFW;
import org.quiltmc.qsl.lifecycle.api.client.event.ClientTickEvents;

import com.mojang.blaze3d.platform.InputUtil;

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBind;
import net.minecraft.text.Text;

public class PingDispatcher {

	private KeyBind keyBind;

	// Create a ping dispatcher and connect to the ping key bind
	public PingDispatcher() {
		// Register the default ping key bind
		ClientPingMod.LOGGER.info("Setting up ping key bind");
		this.keyBind = KeyBindingHelper.registerKeyBinding(new KeyBind("key.client_ping.ping",
				InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_Z, "category.client_ping.main"));
	}

	// Registers an event handler that does the local ping creation logic every time
	// the ping key is pressed
	public void register() {
		// Perform checks every tick
		ClientPingMod.LOGGER.info("Registering ping creation event handler");
		ClientTickEvents.END.register(client -> {
			// Check if the ping key was pressed
			while (this.keyBind.wasPressed()) {
				ClientPingMod.LOGGER.info("Ping!");
				client.player.sendMessage(Text.literal("Key 1 was pressed!"), false);
			}
		});
	}

}
