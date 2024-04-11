package com.tom.vivecraftcompat;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.loading.FMLEnvironment;

@Mod(ViveCraftCompat.MODID)
public class ViveCraftCompat {
	public static final String MODID = "vivecraftcompat";
	public static final Logger LOGGER = LogManager.getLogger();

	public ViveCraftCompat(IEventBus bus) {
		LOGGER.info("Vive Craft Compat loaded");

		bus.addListener(this::doClientStuff);

		if(FMLEnvironment.dist == Dist.CLIENT) {
			Client.preInit(bus);
		}
	}

	private void doClientStuff(final FMLClientSetupEvent event) {
		Client.init();
	}
}
