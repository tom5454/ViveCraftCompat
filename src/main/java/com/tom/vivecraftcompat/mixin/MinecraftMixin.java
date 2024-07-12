package com.tom.vivecraftcompat.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.DeltaTracker.Timer;
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
	@Final
	private Timer timer;

	@Inject(at = @At(value = "INVOKE", target = "Lnet/neoforged/neoforge/client/ClientHooks;fireRenderFramePost(Lnet/minecraft/client/DeltaTracker;)V", shift = At.Shift.AFTER), method = "runTick")
	public void renderOverlayPasses(boolean renderLevel, CallbackInfo ci) {
		OverlayManager.drawLayers(timer);
	}

	@Inject(at = @At("HEAD"), method = "runTick(Z)V")
	public void updateVRControllers(boolean tick, CallbackInfo callback) {
		if (VRMode.isVR())
			NeoForge.EVENT_BUS.post(new VRUpdateControllersEvent());
	}
}
