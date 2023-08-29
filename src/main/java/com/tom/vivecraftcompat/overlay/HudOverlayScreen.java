package com.tom.vivecraftcompat.overlay;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.TextComponent;

import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.gui.ForgeIngameGui;
import net.minecraftforge.client.gui.IIngameOverlay;
import net.minecraftforge.client.gui.OverlayRegistry;
import net.minecraftforge.client.gui.OverlayRegistry.OverlayEntry;
import net.minecraftforge.common.MinecraftForge;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.logging.LogUtils;

import com.tom.vivecraftcompat.overlay.OverlayManager.Layer;

public class HudOverlayScreen extends Screen {
	public static final Logger LOGGER = LogUtils.getLogger();
	public List<String> overlays = new ArrayList<>();
	private final String id;
	private String name;
	public boolean outline;
	public Layer layer;
	public OverlayEnable enable = OverlayEnable.ALWAYS;

	public HudOverlayScreen(String id) {
		super(new TextComponent(""));
		this.id = id;
	}

	@Override
	public void render(PoseStack poseStack, int pMouseX, int pMouseY, float pPartialTick) {
		if(this.minecraft.player == null || this.minecraft.gameMode == null || this.minecraft.level == null || !isEnabled())return;
		int screenWidth = this.minecraft.getWindow().getGuiScaledWidth();
		int screenHeight = this.minecraft.getWindow().getGuiScaledHeight();
		overlays.forEach(id -> {
			OverlayEntry entry = OverlayRegistry.orderedEntries().stream().filter(e -> e.getDisplayName().equals(id)).findFirst().orElse(null);
			if(entry != null) {
				try {
					if (pre(entry.getOverlay(), poseStack)) return;
					entry.getOverlay().render((ForgeIngameGui) minecraft.gui, poseStack, pPartialTick, screenWidth, screenHeight);
					post(entry.getOverlay(), poseStack);
				} catch (Exception e) {
					LOGGER.error("Error rendering overlay '{}'", entry.getDisplayName(), e);
				}
			}
		});
		if(outline) {
			fill(poseStack, 0, 0, width, 1, 0xFFFF0000);
			fill(poseStack, 0, 0, 1, height, 0xFFFF0000);
			fill(poseStack, width - 1, 0, width, height, 0xFFFF0000);
			fill(poseStack, 0, height - 1, width, height, 0xFFFF0000);
		}
	}

	private boolean pre(IIngameOverlay overlay, PoseStack poseStack) {
		RenderGameOverlayEvent parent = new RenderGameOverlayEvent(poseStack, minecraft.getFrameTime(), minecraft.getWindow());
		return MinecraftForge.EVENT_BUS.post(new RenderGameOverlayEvent.PreLayer(poseStack, parent, overlay));
	}

	private void post(IIngameOverlay overlay, PoseStack poseStack) {
		RenderGameOverlayEvent parent = new RenderGameOverlayEvent(poseStack, minecraft.getFrameTime(), minecraft.getWindow());
		MinecraftForge.EVENT_BUS.post(new RenderGameOverlayEvent.PostLayer(poseStack, parent, overlay));
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