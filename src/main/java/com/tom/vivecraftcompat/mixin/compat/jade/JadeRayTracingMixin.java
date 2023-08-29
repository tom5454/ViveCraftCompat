package com.tom.vivecraftcompat.mixin.compat.jade;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

import com.tom.vivecraftcompat.VRHelper;
import com.tom.vivecraftcompat.VRMode;

import snownee.jade.overlay.RayTracing;

@Mixin(RayTracing.class)
public class JadeRayTracingMixin {

	@ModifyVariable(at = @At("STORE"), method = "rayTrace", remap = false, ordinal = 0)
	private Vec3 vrEyePos(Vec3 original) {
		if (VRMode.isVRStanding()) {
			return VRHelper.getRayOrigin();
		}
		return original;
	}

	@Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;getViewVector(F)Lnet/minecraft/world/phys/Vec3;", remap = true), method = "rayTrace", remap = false)
	private Vec3 vrEyeLook(Entity entity, float partialTicks) {
		if (VRMode.isVRStanding()) {
			return VRHelper.getRayDirection();
		}
		return entity.getViewVector(partialTicks);
	}
}
