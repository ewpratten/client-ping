package com.ewpratten.client_ping.communication;

import java.util.ArrayList;
import java.util.UUID;

import org.jetbrains.annotations.Nullable;

import com.ewpratten.client_ping.Globals;
import com.ewpratten.client_ping.util.interfaces.FromJson;
import com.ewpratten.client_ping.util.interfaces.ToJson;
import com.google.gson.Gson;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;

public class Party implements ToJson, FromJson<Party> {

	private ArrayList<UUID> members;

	public Party() {
		this.members = new ArrayList<UUID>();
	}

	public void addMember(UUID uuid) {
		Globals.LOGGER.info("Adding player to party: " + uuid.toString());
		this.members.add(uuid);
	}

	public void removeMember(UUID uuid) {
		Globals.LOGGER.info("Removing player from party: " + uuid.toString());
		this.members.remove(uuid);
	}

	public boolean isMember(UUID uuid) {
		return this.members.contains(uuid);
	}

	public void broadcastChatToAllMembers(MinecraftClient client, String message) {
		// Get the network handler
		ClientPlayNetworkHandler netHandler = client.getNetworkHandler();

		// Send a `/msg` to each player
		for (UUID uuid : this.members) {
			// Get the username of this UUID
			String username = netHandler.getPlayerListEntry(uuid).getProfile().getName();

			// Run the msg command
			// TODO: This might need to be `sendCommand`
			netHandler.sendChatCommand(String.format("/msg %s %s", username, message));
		}
	}

	@Override
	public @Nullable Party fromJson(String json) {
		Gson gson = new Gson();
		return gson.fromJson(json, Party.class);
	}

	@Override
	public String toJson() {
		Gson gson = new Gson();
		return gson.toJson(this);
	}
}
