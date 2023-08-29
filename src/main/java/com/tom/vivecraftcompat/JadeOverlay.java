package com.tom.vivecraftcompat;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.world.phys.HitResult.Type;

import net.minecraftforge.client.gui.ForgeIngameGui;
import net.minecraftforge.client.gui.OverlayRegistry;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.vertex.PoseStack;

import mcp.mobius.waila.Waila;
import mcp.mobius.waila.WailaClient;
import mcp.mobius.waila.api.config.WailaConfig.ConfigOverlay;
import mcp.mobius.waila.api.config.WailaConfig.DisplayMode;
import mcp.mobius.waila.gui.OptionsScreen;
import mcp.mobius.waila.overlay.OverlayRenderer;
import mcp.mobius.waila.overlay.RayTracing;
import mcp.mobius.waila.overlay.WailaTickHandler;

public class JadeOverlay {

	public static void register() {
		OverlayRegistry.registerOverlayTop("Jade", JadeOverlay::render);
	}

	public static void render(ForgeIngameGui gui, PoseStack poseStack, float partialTick, int screenWidth, int screenHeight) {
		if (WailaTickHandler.instance().tooltipRenderer != null) {
			if (Waila.CONFIG.get().getGeneral().shouldDisplayTooltip()) {
				if (Waila.CONFIG.get().getGeneral().getDisplayMode() != DisplayMode.HOLD_KEY
						|| WailaClient.showOverlay.isDown()) {
					Minecraft mc = Minecraft.getInstance();
					if (mc.level != null) {
						if (RayTracing.INSTANCE.getTarget() != null) {
							if (mc.screen != null) {
								if (!(mc.screen instanceof OptionsScreen)) {
									return;
								}

								Rect2i position = WailaTickHandler.instance().tooltipRenderer.getPosition();
								ConfigOverlay overlay = Waila.CONFIG.get().getOverlay();
								Window window = mc.getWindow();
								double x = mc.mouseHandler.xpos() * window.getGuiScaledWidth()
										/ window.getScreenWidth();
								double y = mc.mouseHandler.ypos() * window.getGuiScaledHeight()
										/ window.getScreenHeight();
								x += position.getWidth() * overlay.tryFlip(overlay.getAnchorX());
								y += position.getHeight() * overlay.getAnchorY();
								if (position.contains((int) x, (int) y)) {
									return;
								}
							}

							if (!mc.gui.getTabList().visible && mc.getOverlay() == null && !mc.options.hideGui) {
								if (!mc.options.renderDebug
										|| !Waila.CONFIG.get().getGeneral().shouldHideFromDebug()) {
									OverlayRenderer.ticks += mc.getDeltaFrameTime();
									if (RayTracing.INSTANCE.getTarget().getType() != Type.MISS) {
										OverlayRenderer.renderOverlay(WailaTickHandler.instance().tooltipRenderer, poseStack);
									}
								}
							}
						}
					}
				}
			}
		}
	}
}
