package com.tom.vivecraftcompat;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;

import snownee.jade.overlay.OverlayRenderer;
import snownee.jade.util.WailaExceptionHandler;

public class JadeOverlay {
	public static final ResourceLocation ID = ResourceLocation.tryBuild(ViveCraftCompat.MODID, "jade");

	public static void register(RegisterGuiLayersEvent event) {
		event.registerAboveAll(ID, JadeOverlay::render);
	}

	public static void render(GuiGraphics gr, DeltaTracker p_348559_) {
		try {
			OverlayRenderer.renderOverlay478757(gr, p_348559_.getGameTimeDeltaPartialTick(false));
		} catch (Throwable e) {
			WailaExceptionHandler.handleErr(e, null, null, null);
		}
	}
}
