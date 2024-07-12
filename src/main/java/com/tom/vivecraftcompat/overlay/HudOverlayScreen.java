package com.tom.vivecraftcompat.overlay;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;
import net.neoforged.neoforge.client.gui.GuiLayerManager.NamedLayer;
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
	public void render(GuiGraphics poseStack, int pMouseX, int pMouseY, float pt) {
		if(this.minecraft.player == null || this.minecraft.gameMode == null || this.minecraft.level == null || !isEnabled())return;
		var dt = minecraft.getTimer();
		overlays.forEach(id -> {
			NamedLayer entry = OverlayAccess.getLayerMap().get(id);
			if(entry != null) {
				try {
					if (!NeoForge.EVENT_BUS.post(new RenderGuiLayerEvent.Pre(poseStack, dt, entry.name(), entry.layer())).isCanceled()) {
						entry.layer().render(poseStack, dt);
						NeoForge.EVENT_BUS.post(new RenderGuiLayerEvent.Post(poseStack, dt, entry.name(), entry.layer()));
					}
				} catch (Exception e) {
					LOGGER.error("Error rendering overlay '{}'", entry.name(), e);
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