package com.tom.vivecraftcompat.mixin.compat.journeymap;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import net.neoforged.neoforge.client.event.RenderGuiEvent;

import journeymap.client.event.NeoForgeHudOverlayEvents;

@Mixin(NeoForgeHudOverlayEvents.class)
public class JMNeoForgeHudOverlayEventsMixin {

	/**
	 * Disable original overlay rendering code
	 *
	 * @reason Outdated overlay code, incompatible with ViveCraft Compat's overlay system
	 * @author tom5454
	 * */
	@Overwrite(remap = false)
	public void postGuiOverlay(final RenderGuiEvent.Post event) {}
}
