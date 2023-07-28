package com.tom.vivecraftcompat;

import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.gui.overlay.ForgeGui;

import com.mojang.blaze3d.vertex.PoseStack;

import snownee.jade.overlay.OverlayRenderer;
import snownee.jade.util.WailaExceptionHandler;

public class JadeOverlay {

	public static void register(RegisterGuiOverlaysEvent event) {
		event.registerAboveAll("jade", JadeOverlay::render);
	}

	public static void render(ForgeGui gui, PoseStack poseStack, float partialTick, int screenWidth, int screenHeight) {
		try {
			OverlayRenderer.renderOverlay478757(poseStack);
		} catch (Throwable e) {
			WailaExceptionHandler.handleErr(e, null, null);
		}
	}
}
