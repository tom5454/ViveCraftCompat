package com.tom.vivecraftcompat;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;

@Mod("vivecraftcompat")
public class ViveCraftCompat {
	public static final Logger LOGGER = LogManager.getLogger();

	public ViveCraftCompat() {
		LOGGER.info("Vive Craft Compat loaded");

		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::doClientStuff);

		if(FMLEnvironment.dist == Dist.CLIENT) {
			Client.preInit();
		}
	}

	private void doClientStuff(final FMLClientSetupEvent event) {
		Client.init();
	}
}
