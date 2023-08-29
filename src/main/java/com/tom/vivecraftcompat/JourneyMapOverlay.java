package com.tom.vivecraftcompat;

import net.minecraftforge.client.gui.ForgeIngameGui;
import net.minecraftforge.client.gui.OverlayRegistry;

import com.mojang.blaze3d.vertex.PoseStack;

import journeymap.client.log.JMLogger;
import journeymap.client.ui.UIManager;

public class JourneyMapOverlay {
	public static void register() {
		OverlayRegistry.registerOverlayTop("JourneyMap", JourneyMapOverlay::render);
	}

	public static void render(ForgeIngameGui gui, PoseStack poseStack, float partialTick, int screenWidth, int screenHeight) {
		try {
			UIManager.INSTANCE.drawMiniMap(poseStack);
		} catch (Throwable var3) {
			JMLogger.throwLogOnce(var3.toString(), var3);
		}
	}
}
