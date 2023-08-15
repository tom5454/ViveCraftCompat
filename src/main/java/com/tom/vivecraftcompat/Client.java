package com.tom.vivecraftcompat;

import java.io.File;

import org.vivecraft.client_vr.ClientDataHolderVR;
import org.vivecraft.client_vr.provider.ControllerType;
import org.vivecraft.client_vr.render.RenderPass;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.network.chat.Component;

import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;

import com.tom.cpl.config.ModConfigFile;
import com.tom.vivecraftcompat.access.BTN;
import com.tom.vivecraftcompat.overlay.FloatingGui;
import com.tom.vivecraftcompat.overlay.OverlayConfig;
import com.tom.vivecraftcompat.overlay.OverlayManager;
import com.tom.vivecraftcompat.overlay.OverlayManager.Layer;
import com.tom.vivecraftcompat.overlay.OverlaySettingsGui;

import dev.tr7zw.firstperson.FirstPersonModelCore;

public class Client {
	private static final ClientDataHolderVR DATA_HOLDER = ClientDataHolderVR.getInstance();
	public static ModConfigFile config;
	private static final Component CAM_BTN = Component.translatable("vivecraft.gui.movethirdpersoncam");
	private static final Component OVERLAY_BTN = Component.translatable("vivecraftcompat.gui.overlays");

	public static void init() {
		if(ModList.get().isLoaded("firstpersonmod")) {
			MinecraftForge.EVENT_BUS.addListener(Client::playerRenderPreFPM);
		}
		MinecraftForge.EVENT_BUS.register(OverlayManager.class);
		MinecraftForge.EVENT_BUS.register(Client.class);

		config = new ModConfigFile(new File(FMLPaths.CONFIGDIR.get().toFile(), "vivecraftcompat.json"));
		OverlayConfig.loadOverlays();
	}

	@SubscribeEvent
	public static void initGui(ScreenEvent.Init.Post evt) {
		if (VRMode.isVR() && evt.getScreen() instanceof PauseScreen) {
			for (GuiEventListener l : evt.getListenersList()) {
				if(l instanceof Button b) {
					if(b.getMessage().equals(CAM_BTN) && b instanceof BTN btn) {
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
		}
	}

	public static void playerRenderPreFPM(RenderPlayerEvent.Pre event) {
		if(VRMode.isVR() && FirstPersonModelCore.isRenderingPlayer) {
			if(DATA_HOLDER.currentPass == RenderPass.THIRD || DATA_HOLDER.currentPass == RenderPass.CAMERA) {
				event.setCanceled(true);
				return;
			}
			FirstPersonModelCore.config.vanillaHands = true;

			float yr = event.getEntity().getYHeadRot();
			float s = -0.2f;
			float y = -1.52f;
			if (event.getEntity().isVisuallySwimming()) {
				y += 1f;
				s -= 0.3f;
			} else if(event.getEntity().isShiftKeyDown()) {
				y += 0.1f;
			}

			event.getPoseStack().translate(-Math.sin(Math.toRadians(yr)) * s, y, Math.cos(Math.toRadians(yr)) * s);
		}
	}

	public static void preInit() {
		FMLJavaModLoadingContext.get().getModEventBus().addListener(Client::registerOverlays);
	}

	public static void registerOverlays(RegisterGuiOverlaysEvent event) {
		if(ModList.get().isLoaded("jade"))JadeOverlay.register(event);
		if(ModList.get().isLoaded("journeymap"))JourneyMapOverlay.register(event);
		if(ModList.get().isLoaded("theoneprobe"))TOPOverlay.register(event);
	}
}
