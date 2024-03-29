package com.tom.vivecraftcompat;

import net.minecraft.client.gui.GuiGraphics;

import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.gui.overlay.ForgeGui;

import journeymap.client.log.JMLogger;
import journeymap.client.ui.UIManager;

public class JourneyMapOverlay {
	public static void register(RegisterGuiOverlaysEvent event) {
		event.registerAboveAll("journeymap", JourneyMapOverlay::render);
	}

	public static void render(ForgeGui gui, GuiGraphics poseStack, float partialTick, int screenWidth, int screenHeight) {
		try {
			UIManager.INSTANCE.drawMiniMap(poseStack);
		} catch (Throwable var3) {
			JMLogger.throwLogOnce(var3.toString(), var3);
		}
	}
}
