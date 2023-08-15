package com.ewpratten.client_ping.communication;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

import org.jetbrains.annotations.Nullable;

import com.ewpratten.client_ping.Globals;
import com.google.gson.Gson;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;

public class Party {

	private final boolean whitelist;
	private ArrayList<UUID> members;

	public Party(boolean whitelist) {
		this.whitelist = whitelist;
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
		if (this.whitelist) {
			return this.members.contains(uuid);
		} else {
			return true;
		}
	}

	public void broadcastChatToAllMembers(MinecraftClient client, String message) {
		// Get the network handler
		ClientPlayNetworkHandler netHandler = client.getNetworkHandler();

		// Log the event
		Globals.LOGGER.info("Sending message to all party members: " + message);

		// Send a `/msg` to each player
		if (this.whitelist) {
			for (UUID uuid : this.members) {
				// Get the username of this UUID
				String username = netHandler.getPlayerListEntry(uuid).getProfile().getName();

				// Run the msg command
				// TODO: This might need to be `sendCommand`
				netHandler.sendChatCommand(String.format("/msg %s %s", username, message));
			}
		} else {
			// If we are configured to broadcast, just send the message to global chat
			netHandler.sendChatMessage(message);
		}
	}

	public static @Nullable Party fromJson(String json) {
		Gson gson = new Gson();
		return gson.fromJson(json, Party.class);
	}

	public String toJson() {
		Gson gson = new Gson();
		return gson.toJson(this);
	}

	@Override
	public String toString() {
		return String.format("Party [members=%s, whitelist=%s]", Arrays.deepToString(this.members.toArray()),
				Boolean.toString(this.whitelist));
	}

}
