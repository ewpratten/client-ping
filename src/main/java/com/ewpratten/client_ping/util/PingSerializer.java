package com.ewpratten.client_ping.util;

import java.util.Base64;

import org.jetbrains.annotations.Nullable;

import com.ewpratten.client_ping.logic.Ping;
import com.google.gson.Gson;

public class PingSerializer {

	private static final String PREAMBLE = "CLP:";

	public static String serialize(Ping ping) {
		// Initialize serializer
		Gson gson = new Gson();

		// Serialize and encode the object
		String json = gson.toJson(ping);
		String encoded = Base64.getEncoder().encodeToString(json.getBytes());

		// Build the output string
		return String.format("%s%s",PREAMBLE, encoded);
	}

	public static @Nullable Ping deserialize(String s) {
		// If the string doesn't start with the preamble, return null
		if (!s.startsWith(PREAMBLE)) {
			return null;
		}

		// Otherwise, try to deserialize
		String stripped = s.substring(PREAMBLE.length());
		byte[] decoded = Base64.getDecoder().decode(stripped);
		String json = new String(decoded);
		Gson gson = new Gson();
		return gson.fromJson(json, Ping.class);
	}

}
