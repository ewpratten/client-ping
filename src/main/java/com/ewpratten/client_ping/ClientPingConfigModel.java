package com.ewpratten.client_ping;

import io.wispforest.owo.config.annotation.Config;
import io.wispforest.owo.config.annotation.Modmenu;

@Modmenu(modId = "client_ping")
@Config(name = "client_ping", wrapperName = "ClientPingConfig")
public class ClientPingConfigModel {

	// The number of seconds to display pings for
	public int pingDisplayTime = 10;

	// Show ping messages in chat
	public boolean showPingsInChat = true;

}
