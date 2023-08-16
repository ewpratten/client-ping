package com.ewpratten.client_ping.gui;

import org.jetbrains.annotations.NotNull;

import io.wispforest.owo.ui.base.BaseOwoScreen;
import io.wispforest.owo.ui.component.Components;
import io.wispforest.owo.ui.container.Containers;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.core.Color;
import io.wispforest.owo.ui.core.HorizontalAlignment;
import io.wispforest.owo.ui.core.Insets;
import io.wispforest.owo.ui.core.OwoUIAdapter;
import io.wispforest.owo.ui.core.Sizing;
import io.wispforest.owo.ui.core.Surface;
import io.wispforest.owo.ui.core.VerticalAlignment;
import io.wispforest.owo.ui.util.CommandOpenedScreen;
import net.minecraft.text.Text;

public class PartyManagerGui extends BaseOwoScreen<FlowLayout> implements CommandOpenedScreen {

	@Override
	protected @NotNull OwoUIAdapter<FlowLayout> createAdapter() {
		return OwoUIAdapter.create(this, Containers::horizontalFlow);
	}

	@Override
	protected void build(FlowLayout rootComponent) {
		// Dark BG, content centered
		rootComponent.surface(Surface.VANILLA_TRANSLUCENT)
				.horizontalAlignment(HorizontalAlignment.CENTER)
				.verticalAlignment(VerticalAlignment.CENTER);

		// Central panel with party controls
		rootComponent.child(
				Containers.verticalFlow(Sizing.fill(50), Sizing.fill(90))
						// Title
						.child(Components.label(Text.translatable("client_ping.ui.party_manager.title"))
								.color(Color.BLACK)
								.margins(Insets.of(0, 10, 0, 0)))

						// Panel BG
						.padding(Insets.of(10))
						.surface(Surface.PANEL)
						.verticalAlignment(VerticalAlignment.CENTER)
						.horizontalAlignment(HorizontalAlignment.CENTER));
	}

}
