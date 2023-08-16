package com.ewpratten.client_ping.communication;

import java.io.File;
import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;

import org.apache.commons.io.FileUtils;

import com.ewpratten.client_ping.Globals;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;

public class PartyManager {

	private static PartyManager instance;
	private MinecraftClient mc;
	private HashMap<String, Party> parties = new HashMap<String, Party>();

	public static PartyManager getInstance() {
		if (instance == null) {
			instance = new PartyManager();
		}
		return instance;
	}

	private PartyManager() {
		this.mc = MinecraftClient.getInstance();
	}

	public Party getCurrentParty() {

		// Get a unique string describing the current server
		String serverId = this.getCurrentServerIdentifier();

		// If the party exists in memory, return it
		if (this.parties.containsKey(serverId)) {
			return this.parties.get(serverId);
		}

		// Otherwise, we might need to load the party from disk
		File partyConfig = this.getPartyConfigLocation(serverId);
		Globals.LOGGER.info("Attempting to load party from: " + partyConfig.getAbsolutePath());
		try {
			if (partyConfig.exists()) {
				// Read the config file
				String json = FileUtils.readFileToString(partyConfig, "UTF-8");

				// Deserialize
				Party party = Party.fromJson(json);

				// Cache and return
				Globals.LOGGER.info("Loaded party from disk: " + party.toString());
				this.parties.put(serverId, party);
				return party;
			}
		} catch (IOException e) {
			Globals.LOGGER.warn(
					"Failed to read party config file from disk. It probably doesn't exist. Creating new party.. Error: "
							+ e.getMessage());
		}

		// If we get here, we need to create a new party
		Party party = new Party(!mc.isInSingleplayer());
		this.parties.put(serverId, party);
		return party;
	}

	public void replicateCurrentPartyToDisk() throws IOException {
		Globals.LOGGER.info("Replicating party to disk");

		// Get the current party
		Party party = this.getCurrentParty();

		// Get a unique string describing the current server
		String serverId = this.getCurrentServerIdentifier();

		// Get the location of the config file
		File partyConfig = this.getPartyConfigLocation(serverId);
		Globals.LOGGER.info("Writing party to: " + partyConfig.getAbsolutePath());

		// Serialize the party
		String json = party.toJson();

		// Write the config file
		FileUtils.writeStringToFile(partyConfig, json, "UTF-8");
	}

	private String getCurrentServerIdentifier() {
		if (this.mc.isInSingleplayer()) {
			// Use the current save name
			return this.mc.getServer().getSaveProperties().getWorldName();

		} else {
			// Use the server address + port
			ClientPlayNetworkHandler netHandler = this.mc.getNetworkHandler();
			return netHandler.getServerInfo().address;
		}
	}

	private File getPartyConfigLocation(String serverId) {
		String b64Encoded = Base64.getEncoder().encodeToString(serverId.getBytes());
		return this.mc.runDirectory.toPath().resolve("config").resolve("client_ping").resolve("parties")
				.resolve(b64Encoded + ".json")
				.toFile();
	}

}
