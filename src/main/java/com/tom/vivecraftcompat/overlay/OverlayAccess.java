package com.tom.vivecraftcompat.overlay;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.gui.GuiLayerManager;
import net.neoforged.neoforge.client.gui.GuiLayerManager.NamedLayer;

public class OverlayAccess {
	private static Field layerManager, layers;
	private static Map<ResourceLocation, NamedLayer> layerMap;
	static {
		try {
			layerManager = Gui.class.getDeclaredField("layerManager");
			layerManager.setAccessible(true);
			layers = GuiLayerManager.class.getDeclaredField("layers");
			layers.setAccessible(true);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static List<NamedLayer> getLayers() {
		try {
			return (List<NamedLayer>) layers.get(layerManager.get(Minecraft.getInstance().gui));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static Map<ResourceLocation, NamedLayer> getLayerMap() {
		if (layerMap == null) {
			layerMap = getLayers().stream().collect(Collectors.toMap(NamedLayer::name, Function.identity()));
		}
		return layerMap;
	}
}
