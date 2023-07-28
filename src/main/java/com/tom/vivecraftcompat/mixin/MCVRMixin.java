package com.tom.vivecraftcompat.mixin;

import java.util.Map;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.vivecraft.provider.MCVR;
import org.vivecraft.provider.openvr_jna.VRInputAction;

import net.minecraftforge.common.MinecraftForge;

import com.tom.vivecraftcompat.events.VRBindingsEvent;

@Mixin(MCVR.class)
public class MCVRMixin {
	protected @Shadow(remap = false) Map<String, VRInputAction> inputActions;

	@Inject(at = @At("HEAD"), method = "processBindings", remap = false)
	private void processBindings(CallbackInfo cbi) {
		if (!this.inputActions.isEmpty())
			MinecraftForge.EVENT_BUS.post(new VRBindingsEvent());
	}
}
