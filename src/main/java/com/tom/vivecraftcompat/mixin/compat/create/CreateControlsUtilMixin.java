package com.tom.vivecraftcompat.mixin.compat.create;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;

import com.simibubi.create.foundation.utility.ControlsUtil;

import com.tom.vivecraftcompat.VRMode;
import com.tom.vivecraftcompat.create.CreateControlsHelper;

@Mixin(ControlsUtil.class)
public class CreateControlsUtilMixin {

	@Inject(at = @At("HEAD"), method = "isActuallyPressed", remap = false, cancellable = true)
	private static void vrActuallyPressed(KeyMapping kb, CallbackInfoReturnable<Boolean> cbi) {
		if(VRMode.isVRStanding() && CreateControlsHelper.getPatchedControls().contains(kb)) {
			Minecraft mc = Minecraft.getInstance();
			if(mc.player != null) {
				cbi.setReturnValue(CreateControlsHelper.isMovementPressed(kb));
			}
		}
	}
}
