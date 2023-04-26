package com.ewpratten.client_ping.util;

import org.quiltmc.qsl.lifecycle.api.client.event.ClientTickEvents;

public class TickBasedScheduledTask {

	private long lastTimestamp = 0;

	public TickBasedScheduledTask(Runnable task, long interval) {
		ClientTickEvents.END.register(client -> {
			// If the time since the last run is greater than the interval
			if (System.currentTimeMillis() - this.lastTimestamp > interval) {
				// Run the task
				task.run();

				// Update the last run timestamp
				this.lastTimestamp = System.currentTimeMillis();
			}
		});
	}
}
