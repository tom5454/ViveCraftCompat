package com.tom.vivecraftcompat;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.event.RegisterGuiOverlaysEvent;
import net.neoforged.neoforge.client.gui.overlay.ExtendedGui;

import journeymap.client.log.JMLogger;
import journeymap.client.ui.UIManager;

public class JourneyMapOverlay {
	public static void register(RegisterGuiOverlaysEvent event) {
		event.registerAboveAll(new ResourceLocation(ViveCraftCompat.MODID, "journeymap"), JourneyMapOverlay::render);
	}

	public static void render(ExtendedGui gui, GuiGraphics poseStack, float partialTick, int screenWidth, int screenHeight) {
		try {
			UIManager.INSTANCE.drawMiniMap(poseStack);
		} catch (Throwable var3) {
			JMLogger.throwLogOnce(var3.toString(), var3);
		}
	}
}
