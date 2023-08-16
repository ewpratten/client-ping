package com.ewpratten.client_ping.handlers;

import org.quiltmc.qsl.command.api.client.ClientCommandManager;
import org.quiltmc.qsl.command.api.client.ClientCommandRegistrationCallback;
import org.quiltmc.qsl.command.api.client.QuiltClientCommandSource;

import com.ewpratten.client_ping.Globals;
import com.ewpratten.client_ping.gui.PartyManagerGui;
import com.mojang.brigadier.CommandDispatcher;

import net.minecraft.client.MinecraftClient;
import net.minecraft.command.CommandBuildContext;
import net.minecraft.server.command.CommandManager.RegistrationEnvironment;

public class ClientSideCommandRegistrationCallback implements ClientCommandRegistrationCallback {

	@Override
	public void registerCommands(CommandDispatcher<QuiltClientCommandSource> dispatcher,
			CommandBuildContext buildContext, RegistrationEnvironment environment) {
		Globals.LOGGER.info("Registering client-side commands");

		// Register the command to open the party screen
		dispatcher.register(ClientCommandManager.literal("cp-party").executes(context -> {
			Globals.LOGGER.info("Opening party screen");

			// Open the party manager
			MinecraftClient client = MinecraftClient.getInstance();
			client.setScreen(new PartyManagerGui());

			return 0;
		}));
	}

}
