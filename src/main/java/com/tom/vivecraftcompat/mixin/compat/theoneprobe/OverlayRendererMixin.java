package com.tom.vivecraftcompat.mixin.compat.theoneprobe;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

import com.tom.vivecraftcompat.VRHelper;
import com.tom.vivecraftcompat.VRMode;

import mcjty.theoneprobe.rendering.OverlayRenderer;

@Mixin(OverlayRenderer.class)
public class OverlayRendererMixin {

	@ModifyVariable(at = @At("STORE"), method = "renderHUD", remap = false, ordinal = 0)
	private static Vec3 vrEyePos(Vec3 original) {
		if (VRMode.isVRStanding()) {
			return VRHelper.getRayOrigin();
		}
		return original;
	}

	@Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;getViewVector(F)Lnet/minecraft/world/phys/Vec3;"), method = "renderHUD", remap = false)
	private static Vec3 vrEyeLook(Player entity, float partialTicks) {
		if (VRMode.isVRStanding()) {
			return VRHelper.getRayDirection();
		}
		return entity.getViewVector(partialTicks);
	}
}
