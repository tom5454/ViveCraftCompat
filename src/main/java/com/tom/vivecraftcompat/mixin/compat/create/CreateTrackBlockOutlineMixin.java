package com.tom.vivecraftcompat.mixin.compat.create;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import net.minecraft.world.phys.Vec3;

import com.simibubi.create.content.trains.track.TrackBlockOutline;

import com.tom.vivecraftcompat.VRHelper;
import com.tom.vivecraftcompat.VRMode;

@Mixin(TrackBlockOutline.class)
public class CreateTrackBlockOutlineMixin {

	@ModifyVariable(at = @At("STORE"), method = "pickCurves()V", remap = false, ordinal = 0)
	private static Vec3 vrOrigin(Vec3 original) {
		if (VRMode.isVRStanding())
			return VRHelper.getRayOrigin();
		return original;
	}
}
