package com.ewpratten.client_ping.util.interfaces;

import org.jetbrains.annotations.Nullable;

public interface FromJson<T> {
	public @Nullable T fromJson(String json);
}
