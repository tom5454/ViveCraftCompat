package com.tom.vivecraftcompat.mixin.compat.journeymap;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.tom.vivecraftcompat.JourneyMapOverlay;
import com.tom.vivecraftcompat.overlay.OverlayManager;

import journeymap.client.ui.minimap.Effect;

@Mixin(Effect.class)
public class JMEffectMixin {

	@Inject(at = @At("HEAD"), cancellable = true, method = "canPotionShift", remap = false)
	public void canPotionShift(CallbackInfoReturnable<Boolean> cbi) {
		if(OverlayManager.isOverlayDetached(JourneyMapOverlay.ID))cbi.setReturnValue(false);
	}
}
