package com.tom.vivecraftcompat;

import net.minecraft.client.gui.GuiGraphics;

import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.gui.overlay.ForgeGui;

import snownee.jade.api.IJadeProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.overlay.OverlayRenderer;
import snownee.jade.util.WailaExceptionHandler;

public class JadeOverlay {

	public static void register(RegisterGuiOverlaysEvent event) {
		event.registerAboveAll("jade", JadeOverlay::render);
	}

	public static void render(ForgeGui gui, GuiGraphics guiGraphics, float partialTick, int screenWidth, int screenHeight) {
		try {
			OverlayRenderer.renderOverlay478757(guiGraphics);
		} catch (final Throwable e) {
			WailaExceptionHandler.handleErr(e, (IJadeProvider) null, (ITooltip) null, (String) null);
		}
	}
}
