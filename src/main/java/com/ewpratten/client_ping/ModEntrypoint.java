package com.ewpratten.client_ping;

import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.ModInitializer;

public class ModEntrypoint implements ModInitializer {

	@Override
	public void onInitialize(ModContainer mod) {
		Globals.LOGGER.info("Mod Loaded. Today will be a great day!");
	}

}
