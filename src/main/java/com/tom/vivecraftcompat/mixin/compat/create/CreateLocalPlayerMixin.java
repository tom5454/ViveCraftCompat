package com.tom.vivecraftcompat.mixin.compat.create;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.player.Input;
import net.minecraft.client.player.LocalPlayer;

import com.tom.vivecraftcompat.create.CreateControlsHelper;

@Mixin(LocalPlayer.class)
public class CreateLocalPlayerMixin {
	public @Shadow Input input;

	@Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/Input;tick(ZF)V", shift = Shift.AFTER), method = "aiStep()V")
	public void onAiStep(CallbackInfo cbi) {
		CreateControlsHelper.vrInputEvent(input);
	}
}
