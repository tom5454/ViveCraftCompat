package com.tom.vivecraftcompat.mixin.compat.theoneprobe;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import net.minecraftforge.client.event.RenderGuiOverlayEvent.Pre;

import mcjty.theoneprobe.rendering.ClientSetup;

@Mixin(ClientSetup.class)
public class ClientSetupMixin {

	/**
	 * Disable original overlay rendering code
	 *
	 * @reason Outdated overlay code, incompatible with ViveCraft Compat's overlay system
	 * @author tom5454
	 * */
	@Overwrite(remap = false)
	public void renderGameOverlayEvent(Pre event) {}
}
