package com.tom.vivecraftcompat.mixin.compat.create;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import net.minecraft.world.phys.Vec3;

import com.simibubi.create.foundation.block.BigOutlines;

import com.tom.vivecraftcompat.VRHelper;
import com.tom.vivecraftcompat.VRMode;

@Mixin(BigOutlines.class)
public class CreateBigOutlinesMixin {

	@ModifyVariable(at = @At("STORE"), method = "pick()V", remap = false, ordinal = 0)
	private static Vec3 vrOrigin(Vec3 original) {
		if (VRMode.isVRStanding())
			return VRHelper.getRayOrigin();
		return original;
	}
}
