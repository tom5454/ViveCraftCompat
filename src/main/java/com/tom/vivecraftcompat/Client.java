package com.tom.vivecraftcompat;

import java.io.File;

import org.vivecraft.client.gui.settings.GuiMainVRSettings;
import org.vivecraft.client_vr.provider.ControllerType;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;

import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLPaths;

import com.tom.cpl.config.ModConfigFile;
import com.tom.vivecraftcompat.overlay.FloatingGui;
import com.tom.vivecraftcompat.overlay.OverlayConfig;
import com.tom.vivecraftcompat.overlay.OverlayManager;
import com.tom.vivecraftcompat.overlay.OverlayManager.Layer;
import com.tom.vivecraftcompat.overlay.OverlaySettingsGui;

import dev.tr7zw.firstperson.FirstPersonModelCore;

public class Client {
	public static ModConfigFile config;
	private static final Component CAM_BTN = new TranslatableComponent("vivecraft.gui.movethirdpersoncam");
	private static final Component OVERLAY_BTN = new TranslatableComponent("vivecraftcompat.gui.overlays");
	private static final Component RENDERING_BTN = new TranslatableComponent("vivecraft.options.screen.stereorendering.button");
	private static final Component FP_CONFIG_BTN = new TranslatableComponent("vivecraftcompat.gui.firstpersonmod.config");

	public static void init() {
		registerOverlays();
		MinecraftForge.EVENT_BUS.register(OverlayManager.class);
		MinecraftForge.EVENT_BUS.register(Client.class);

		config = new ModConfigFile(new File(FMLPaths.CONFIGDIR.get().toFile(), "vivecraftcompat.json"));
		OverlayConfig.loadOverlays();
	}

	@SubscribeEvent
	public static void initGui(ScreenEvent.InitScreenEvent.Post evt) {
		if (VRMode.isVR() && evt.getScreen() instanceof PauseScreen) {
			for (GuiEventListener l : evt.getListenersList()) {
				if(l instanceof Button b) {
					if(b.getMessage().equals(CAM_BTN)) {
						b = new Button(b.x + b.getWidth() + 5, b.y, 100, 20, OVERLAY_BTN, __ -> {
							Layer layer = new Layer(i -> new FloatingGui(OverlaySettingsGui::new, i));
							layer.spawnOverlay(ControllerType.RIGHT);
							OverlayManager.addLayer(layer);
							Minecraft.getInstance().setScreen(null);
						});
						evt.addListener(b);
						break;
					}
				}
			}
		} else if(evt.getScreen() instanceof GuiMainVRSettings) {
			for (GuiEventListener l : evt.getListenersList()) {
				if(l instanceof Button b) {
					if(ModList.get().isLoaded("firstpersonmod") && b.getMessage().equals(RENDERING_BTN)) {
						b = new Button(b.x, b.y + 126, 150, 20, FP_CONFIG_BTN, __ -> {
							Minecraft.getInstance().setScreen(FirstPersonModelCore.instance.createConfigScreen(evt.getScreen()));
						});
						evt.addListener(b);
						break;
					}
				}
			}
		}
	}

	public static void preInit() {
	}

	public static void registerOverlays() {
		if(ModList.get().isLoaded("jade"))JadeOverlay.register();
		if(ModList.get().isLoaded("journeymap"))JourneyMapOverlay.register();
		if(ModList.get().isLoaded("theoneprobe"))TOPOverlay.register();
	}
}
