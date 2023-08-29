package com.tom.vivecraftcompat.mixin.compat.create;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

import com.simibubi.create.foundation.utility.RaycastHelper;

import com.tom.vivecraftcompat.VRHelper;
import com.tom.vivecraftcompat.VRMode;

@Mixin(RaycastHelper.class)
public class CreateRaycastHelperMixin {

	@Inject(at = @At("HEAD"), method = "getTraceOrigin", cancellable = true, remap = false)
	private static void onGetTraceOrigin(Player playerIn, CallbackInfoReturnable<Vec3> cbi) {
		if (playerIn.level.isClientSide && VRMode.isVRStanding()) {
			cbi.setReturnValue(VRHelper.getRayOrigin());
		}
	}

	@Inject(at = @At("HEAD"), method = "getTraceTarget", cancellable = true, remap = false)
	private static void onGetTraceTarget(Player playerIn, double range, Vec3 origin, CallbackInfoReturnable<Vec3> cbi) {
		if (playerIn.level.isClientSide && VRMode.isVRStanding()) {
			Vec3 d = VRHelper.getRayDirection();
			cbi.setReturnValue(origin.add(d.x * range, d.y * range, d.z * range));
		}
	}
}
