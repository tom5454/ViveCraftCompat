package com.tom.vivecraftcompat;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;

import journeymap.client.event.handlers.HudOverlayHandler;

public class JourneyMapOverlay {
	public static final ResourceLocation ID = ResourceLocation.tryBuild(ViveCraftCompat.MODID, "journeymap");

	public static void register(RegisterGuiLayersEvent event) {
		event.registerAboveAll(ID, JourneyMapOverlay::render);
	}

	public static void render(GuiGraphics gr, DeltaTracker p_348559_) {
		HudOverlayHandler.getInstance().onRenderOverlay(gr);
	}
}
