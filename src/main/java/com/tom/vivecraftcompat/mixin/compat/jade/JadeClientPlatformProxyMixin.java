package com.tom.vivecraftcompat.mixin.compat.jade;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import net.minecraftforge.event.TickEvent.RenderTickEvent;

import mcp.mobius.waila.WailaClient;

@Mixin(WailaClient.class)
public class JadeClientPlatformProxyMixin {

	/**
	 * Disable original overlay rendering code
	 *
	 * @reason Outdated overlay code, incompatible with ViveCraft Compat's overlay system
	 * @author tom5454
	 * */
	@Overwrite(remap = false)
	public static void onRenderTick(RenderTickEvent event) {
	}
}
