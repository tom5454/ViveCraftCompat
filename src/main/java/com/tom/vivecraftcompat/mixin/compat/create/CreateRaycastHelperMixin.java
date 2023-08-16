package com.tom.vivecraftcompat.mixin.compat.create;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.vivecraft.client_vr.ClientDataHolderVR;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

import com.simibubi.create.foundation.utility.RaycastHelper;

import com.tom.vivecraftcompat.VRMode;

@Mixin(RaycastHelper.class)
public class CreateRaycastHelperMixin {
	private static final ClientDataHolderVR DATA_HOLDER = ClientDataHolderVR.getInstance();

	@Inject(at = @At("HEAD"), method = "getTraceOrigin", cancellable = true, remap = false)
	private static void onGetTraceOrigin(Player playerIn, CallbackInfoReturnable<Vec3> cbi) {
		if (playerIn.level().isClientSide && VRMode.isVR() && !DATA_HOLDER.vrSettings.seated) {
			cbi.setReturnValue(DATA_HOLDER.vrPlayer.vrdata_world_render.getController(0).getPosition());
		}
	}

	@Inject(at = @At("HEAD"), method = "getTraceTarget", cancellable = true, remap = false)
	private static void onGetTraceTarget(Player playerIn, double range, Vec3 origin, CallbackInfoReturnable<Vec3> cbi) {
		if (playerIn.level().isClientSide && VRMode.isVR() && !DATA_HOLDER.vrSettings.seated) {
			Vec3 d = DATA_HOLDER.vrPlayer.vrdata_world_render.getController(0).getDirection();
			cbi.setReturnValue(origin.add(d.x * range, d.y * range, d.z * range));
		}
	}
}
