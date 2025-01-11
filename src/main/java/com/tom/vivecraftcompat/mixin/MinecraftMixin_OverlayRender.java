package com.tom.vivecraftcompat.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.DeltaTracker.Timer;
import net.minecraft.client.Minecraft;

import com.tom.vivecraftcompat.overlay.OverlayManager;

@Mixin(value = Minecraft.class, priority = 500)
public class MinecraftMixin_OverlayRender {
	@Shadow
	@Final
	private Timer timer;

	@Inject(at = @At(value = "INVOKE", target = "Lnet/neoforged/neoforge/client/ClientHooks;fireRenderFramePost(Lnet/minecraft/client/DeltaTracker;)V", shift = At.Shift.AFTER), method = "runTick")
	public void renderOverlayPasses(boolean renderLevel, CallbackInfo ci) {
		OverlayManager.drawLayers(timer);
	}
}
