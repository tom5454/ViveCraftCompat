package com.tom.vivecraftcompat.mixin.compat.journeymap;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import journeymap.client.event.forge.ForgeHudOverlayEvents;

@Mixin(ForgeHudOverlayEvents.class)
public class JMForgeHudOverlayEventsMixin {

	/**
	 * Disable original overlay rendering code
	 *
	 * @reason Outdated overlay code, incompatible with ViveCraft Compat's overlay system
	 * @author tom5454
	 * */
	@Overwrite(remap = false)
	public void preOverlay(net.minecraftforge.client.event.RenderGameOverlayEvent.Pre event) {}
}
