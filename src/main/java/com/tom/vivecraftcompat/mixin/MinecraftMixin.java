package com.tom.vivecraftcompat.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.Minecraft;
import net.minecraft.client.Timer;

import com.bawnorton.mixinsquared.TargetHandler;
import com.mojang.blaze3d.pipeline.RenderTarget;

import com.tom.vivecraftcompat.access.MC;
import com.tom.vivecraftcompat.overlay.OverlayManager;

@Mixin(value = Minecraft.class, priority = 2000)
public class MinecraftMixin implements MC {

	public @Shadow RenderTarget mainRenderTarget;
	private @Shadow boolean pause;
	private @Shadow float pausePartialTick;
	private @Shadow @Final Timer timer;

	@Override
	public void mc$setMainRenderTarget(RenderTarget mainRenderTarget) {
		this.mainRenderTarget = mainRenderTarget;
	}

	@TargetHandler(mixin = "org.vivecraft.mixin.client_vr.MinecraftVRMixin", name = "vivecraft$renderVRPasses")
	@Inject(at = @At(value = "INVOKE", target = "Lorg/vivecraft/client_vr/gameplay/screenhandlers/RadialHandler;isShowing()Z", remap = false), method = "@MixinSquared:Handler", remap = false)
	public void renderHudLayers(boolean renderLevel, CallbackInfo ci, long nanoTime, CallbackInfo cbi) {
		float f = this.pause ? this.pausePartialTick : this.timer.partialTick;
		OverlayManager.drawLayers(f);
	}
}
