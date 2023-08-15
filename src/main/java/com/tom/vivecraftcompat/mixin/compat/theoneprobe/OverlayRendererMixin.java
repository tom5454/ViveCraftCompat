package com.tom.vivecraftcompat.mixin.compat.theoneprobe;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.vivecraft.client_vr.ClientDataHolderVR;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

import com.tom.vivecraftcompat.VRMode;

import mcjty.theoneprobe.rendering.OverlayRenderer;

@Mixin(OverlayRenderer.class)
public class OverlayRendererMixin {
	private static final ClientDataHolderVR DATA_HOLDER = ClientDataHolderVR.getInstance();

	@ModifyVariable(at = @At("STORE"), method = "renderHUD", remap = false, ordinal = 0)
	private static Vec3 vrEyePos(Vec3 original) {
		if (VRMode.isVR() && !DATA_HOLDER.vrSettings.seated) {
			return DATA_HOLDER.vrPlayer.vrdata_world_render.getController(0).getPosition();
		}
		return original;
	}

	@Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;getViewVector(F)Lnet/minecraft/world/phys/Vec3;"), method = "renderHUD", remap = false)
	private static Vec3 vrEyeLook(Player entity, float partialTicks) {
		if (VRMode.isVR() && !DATA_HOLDER.vrSettings.seated) {
			return DATA_HOLDER.vrPlayer.vrdata_world_render.getController(0).getDirection();
		}
		return entity.getViewVector(partialTicks);
	}
}
