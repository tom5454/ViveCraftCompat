package com.tom.vivecraftcompat;

import org.vivecraft.client.gui.settings.GuiMainVRSettings;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.Screens;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.network.chat.Component;

import dev.tr7zw.firstperson.config.ConfigScreenProvider;

public class Client implements ClientModInitializer {
	private static final Component RENDERING_BTN = Component.translatable("vivecraft.options.screen.stereorendering.button");
	private static final Component FP_CONFIG_BTN = Component.translatable("vivecraftcompat.gui.firstpersonmod.config");

	@Override
	public void onInitializeClient() {
		ScreenEvents.AFTER_INIT.register((mc, screen, sw, sh) -> {
			if(screen instanceof GuiMainVRSettings) {
				for (GuiEventListener l : Screens.getButtons(screen)) {
					if(l instanceof Button b) {
						if(FabricLoader.getInstance().isModLoaded("firstperson") && b.getMessage().equals(RENDERING_BTN)) {
							b = Button.builder(FP_CONFIG_BTN, __ -> {
								Minecraft.getInstance().setScreen(ConfigScreenProvider.createConfigScreen(screen));
							}).bounds(b.getX(), b.getY() + 126, 150, 20).build();
							Screens.getButtons(screen).add(b);
							break;
						}
					}
				}
			}
		});
	}
}
