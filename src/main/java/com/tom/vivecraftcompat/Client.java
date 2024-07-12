package com.tom.vivecraftcompat;

import java.io.File;

import org.vivecraft.client.gui.settings.GuiMainVRSettings;
import org.vivecraft.client_vr.provider.ControllerType;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.network.chat.Component;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.event.ScreenEvent;
import net.neoforged.neoforge.common.NeoForge;

import com.tom.cpl.config.ModConfigFile;
import com.tom.vivecraftcompat.overlay.FloatingGui;
import com.tom.vivecraftcompat.overlay.OverlayConfig;
import com.tom.vivecraftcompat.overlay.OverlayManager;
import com.tom.vivecraftcompat.overlay.OverlayManager.Layer;
import com.tom.vivecraftcompat.overlay.OverlaySettingsGui;

public class Client {
	public static ModConfigFile config;
	private static final Component CAM_BTN = Component.translatable("vivecraft.gui.movethirdpersoncam");
	private static final Component OVERLAY_BTN = Component.translatable("vivecraftcompat.gui.overlays");
	private static final Component RENDERING_BTN = Component.translatable("vivecraft.options.screen.stereorendering.button");
	private static final Component FP_CONFIG_BTN = Component.translatable("vivecraftcompat.gui.firstpersonmod.config");

	public static void init() {
		NeoForge.EVENT_BUS.register(OverlayManager.class);
		NeoForge.EVENT_BUS.register(Client.class);

		config = new ModConfigFile(new File(FMLPaths.CONFIGDIR.get().toFile(), "vivecraftcompat.json"));
		OverlayConfig.loadOverlays();
	}

	@SubscribeEvent
	public static void initGui(ScreenEvent.Init.Post evt) {
		if (VRMode.isVR() && evt.getScreen() instanceof PauseScreen) {
			for (GuiEventListener l : evt.getListenersList()) {
				if(l instanceof Button b) {
					if(b.getMessage().equals(CAM_BTN)) {
						b = Button.builder(OVERLAY_BTN, __ -> {
							Layer layer = new Layer(i -> new FloatingGui(OverlaySettingsGui::new, i));
							layer.spawnOverlay(ControllerType.RIGHT);
							OverlayManager.addLayer(layer);
							Minecraft.getInstance().setScreen(null);
						}).bounds(b.getX() + b.getWidth() + 5, b.getY(), 100, 20).build();
						evt.addListener(b);
						break;
					}
				}
			}
		} else if(evt.getScreen() instanceof GuiMainVRSettings) {
			/*for (GuiEventListener l : evt.getListenersList()) {
				if(l instanceof Button b) {
					if(ModList.get().isLoaded("firstperson") && b.getMessage().equals(RENDERING_BTN)) {
						b = Button.builder(FP_CONFIG_BTN, __ -> {
							Minecraft.getInstance().setScreen(ConfigScreenProvider.createConfigScreen(evt.getScreen()));
						}).bounds(b.getX(), b.getY() + 126, 150, 20).build();
						evt.addListener(b);
						break;
					}
				}
			}*/
		}
	}

	public static void preInit(IEventBus bus) {
		bus.addListener(Client::registerOverlays);
	}

	public static void registerOverlays(RegisterGuiLayersEvent event) {
		if(ModList.get().isLoaded("jade"))JadeOverlay.register(event);
		if(ModList.get().isLoaded("journeymap"))JourneyMapOverlay.register(event);
		if(ModList.get().isLoaded("theoneprobe"))TOPOverlay.register(event);
	}
}
