package com.tom.vivecraftcompat.overlay;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.event.RenderGuiOverlayEvent;
import net.neoforged.neoforge.client.gui.overlay.ExtendedGui;
import net.neoforged.neoforge.client.gui.overlay.GuiOverlayManager;
import net.neoforged.neoforge.client.gui.overlay.IGuiOverlay;
import net.neoforged.neoforge.client.gui.overlay.NamedGuiOverlay;
import net.neoforged.neoforge.common.NeoForge;

import com.mojang.logging.LogUtils;

import com.tom.vivecraftcompat.overlay.OverlayManager.Layer;

public class HudOverlayScreen extends Screen {
	public static final Logger LOGGER = LogUtils.getLogger();
	public List<ResourceLocation> overlays = new ArrayList<>();
	private final String id;
	private String name;
	public boolean outline;
	public Layer layer;
	public OverlayEnable enable = OverlayEnable.ALWAYS;

	public HudOverlayScreen(String id) {
		super(Component.literal(""));
		this.id = id;
	}

	@Override
	public void render(GuiGraphics poseStack, int pMouseX, int pMouseY, float pPartialTick) {
		if(this.minecraft.player == null || this.minecraft.gameMode == null || this.minecraft.level == null || !isEnabled())return;
		int screenWidth = this.minecraft.getWindow().getGuiScaledWidth();
		int screenHeight = this.minecraft.getWindow().getGuiScaledHeight();
		overlays.forEach(id -> {
			NamedGuiOverlay entry = GuiOverlayManager.findOverlay(id);
			if(entry != null) {
				try {
					IGuiOverlay overlay = entry.overlay();
					if (pre(entry, poseStack)) return;
					overlay.render((ExtendedGui) minecraft.gui, poseStack, pPartialTick, screenWidth, screenHeight);
					post(entry, poseStack);
				} catch (Exception e) {
					LOGGER.error("Error rendering overlay '{}'", entry.id(), e);
				}
			}
		});
		if(outline) {
			poseStack.fill(0, 0, width, 1, 0xFFFF0000);
			poseStack.fill(0, 0, 1, height, 0xFFFF0000);
			poseStack.fill(width - 1, 0, width, height, 0xFFFF0000);
			poseStack.fill(0, height - 1, width, height, 0xFFFF0000);
		}
	}

	private boolean pre(NamedGuiOverlay overlay, GuiGraphics poseStack) {
		return NeoForge.EVENT_BUS.post(new RenderGuiOverlayEvent.Pre(minecraft.getWindow(), poseStack, minecraft.getFrameTime(), overlay)).isCanceled();
	}

	private void post(NamedGuiOverlay overlay, GuiGraphics poseStack) {
		NeoForge.EVENT_BUS.post(new RenderGuiOverlayEvent.Post(minecraft.getWindow(), poseStack, minecraft.getFrameTime(), overlay));
	}

	@Override
	public String toString() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public String getId() {
		return id;
	}

	public boolean isEnabled() {
		return enable.test(minecraft);
	}
}