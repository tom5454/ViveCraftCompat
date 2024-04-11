package com.tom.vivecraftcompat;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.event.RegisterGuiOverlaysEvent;
import net.neoforged.neoforge.client.gui.overlay.ExtendedGui;

import snownee.jade.overlay.OverlayRenderer;
import snownee.jade.util.WailaExceptionHandler;

public class JadeOverlay {

	public static void register(RegisterGuiOverlaysEvent event) {
		event.registerAboveAll(new ResourceLocation(ViveCraftCompat.MODID, "jade"), JadeOverlay::render);
	}

	public static void render(ExtendedGui gui, GuiGraphics poseStack, float partialTick, int screenWidth, int screenHeight) {
		try {
			OverlayRenderer.renderOverlay478757(poseStack);
		} catch (Throwable e) {
			WailaExceptionHandler.handleErr(e, null, null);
		}
	}
}
