package com.ewpratten.client_ping;

import io.wispforest.owo.config.annotation.Config;
import io.wispforest.owo.config.annotation.Modmenu;

@Modmenu(modId = "client_ping")
@Config(name = "client_ping", wrapperName = "ClientPingConfig")
public class ConfigModel {

	// The number of seconds to display pings for
	public int pingDisplayTime = 10;

	// The ping rate limit (both outbound and inbound) (seconds)
	public int minPingInterval = 1;

	// Show ping messages in chat
	public boolean showPingsInChat = true;

	// Only accept incoming pings from party
	public boolean onlyAcceptPartyPings = false;

}
