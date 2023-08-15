package com.tom.vivecraftcompat.mixin.compat.create;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.simibubi.create.foundation.blockEntity.behaviour.ValueSettingsClient;

import com.tom.vivecraftcompat.VRMode;

@Mixin(ValueSettingsClient.class)
public class CreateValueSettingsClientMixin {
	public @Shadow(remap = false) int interactHeldTicks = -1;

	@Inject(at = @At("HEAD"), method = "tick", remap = false)
	public void onTick(CallbackInfo cbi) {
		if (!VRMode.isVR() || interactHeldTicks == -1)return;
		interactHeldTicks = 10;
	}
}
