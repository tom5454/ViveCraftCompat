package com.tom.vivecraftcompat.mixin;

import java.util.Set;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.vivecraft.provider.InputSimulator;

import com.tom.vivecraftcompat.overlay.OverlayManager;

@Mixin(InputSimulator.class)
public class InputSimulatorMixin {

	private @Shadow(remap = false) static Set<Integer> pressedKeys;

	@Inject(at = @At("HEAD"), method = "typeChar(C)V", cancellable = true, remap = false)
	private static void typeChar(char character, CallbackInfo cbi) {
		if(OverlayManager.type(character))cbi.cancel();
	}

	@Inject(at = @At("HEAD"), method = "pressKey(I)V", cancellable = true, remap = false)
	private static void pressKey(int key, CallbackInfo cbi) {
		if(OverlayManager.key(key)) {
			cbi.cancel();
			pressedKeys.add(key);
		}
	}
}
