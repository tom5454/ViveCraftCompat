package com.tom.vivecraftcompat.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.Minecraft;
import net.neoforged.neoforge.common.NeoForge;

import com.mojang.blaze3d.pipeline.RenderTarget;

import com.tom.vivecraftcompat.VRMode;
import com.tom.vivecraftcompat.access.MC;
import com.tom.vivecraftcompat.events.VRUpdateControllersEvent;
import com.tom.vivecraftcompat.overlay.OverlayManager;

@Mixin(value = Minecraft.class, priority = 2000)
public class MinecraftMixin implements MC {

	public @Shadow RenderTarget mainRenderTarget;

	@Override
	public void mc$setMainRenderTarget(RenderTarget mainRenderTarget) {
		this.mainRenderTarget = mainRenderTarget;
	}

	@Shadow
	private float realPartialTick;

	@Inject(at = @At(value = "INVOKE", target = "Lnet/neoforged/neoforge/event/EventHooks;onRenderTickEnd(F)V", shift = At.Shift.AFTER), method = "runTick(Z)V")
	public void renderOverlayPasses(final boolean renderLevel, final CallbackInfo ci) {
		OverlayManager.drawLayers(realPartialTick);
	}

	@Inject(at = @At("HEAD"), method = "runTick(Z)V")
	public void updateVRControllers(boolean tick, CallbackInfo callback) {
		if (VRMode.isVR())
			NeoForge.EVENT_BUS.post(new VRUpdateControllersEvent());
	}
}
