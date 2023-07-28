package com.tom.vivecraftcompat.mixin.compat.jade;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraftforge.event.TickEvent;

import snownee.jade.util.ClientPlatformProxy;

@Mixin(ClientPlatformProxy.class)
public class JadeClientPlatformProxyMixin {
	private @Shadow(remap = false) static boolean bossbarShown;

	/**
	 * Disable original overlay rendering code
	 *
	 * @reason Outdated overlay code, incompatible with ViveCraft Compat's overlay system
	 * @author tom5454
	 * */
	@Overwrite(remap = false)
	private static void onRenderTick(TickEvent.RenderTickEvent event) {
		bossbarShown = false;
	}
}
