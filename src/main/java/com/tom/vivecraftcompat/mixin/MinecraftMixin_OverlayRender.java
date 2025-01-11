package com.tom.vivecraftcompat.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.Minecraft;
import net.minecraft.client.Timer;

import com.tom.vivecraftcompat.overlay.OverlayManager;

@Mixin(value = Minecraft.class, priority = 500)
public class MinecraftMixin_OverlayRender {
	private @Shadow boolean pause;
	private @Shadow float pausePartialTick;
	private @Shadow @Final Timer timer;

	@Inject(at = @At(value = "INVOKE", target = "Lnet/minecraftforge/event/ForgeEventFactory;onRenderTickEnd(F)V", shift = At.Shift.AFTER), method = "runTick")
	public void renderOverlayPasses(boolean renderLevel, CallbackInfo ci) {
		OverlayManager.drawLayers(this.pause ? this.pausePartialTick : this.timer.partialTick);
	}
}
