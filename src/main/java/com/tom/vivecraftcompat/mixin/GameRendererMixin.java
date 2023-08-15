package com.tom.vivecraftcompat.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.vivecraft.common.utils.math.Matrix4f;

import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.world.phys.Vec3;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.vertex.PoseStack;

import com.tom.vivecraftcompat.overlay.OverlayManager;

@Mixin(value = GameRenderer.class, priority = 2000)
public abstract class GameRendererMixin {
	abstract @Shadow(remap = false) void render2D(float partialTicks, RenderTarget framebuffer, Vec3 pos, Matrix4f rot, boolean depthAlways, PoseStack poseStack);
	public abstract @Shadow(remap = false) boolean shouldOccludeGui();

	@Inject(at = @At(value = "INVOKE", target = "Lorg/vivecraft/client_vr/gameplay/screenhandlers/RadialHandler;isShowing()Z", remap = false), method = "renderVRFabulous", remap = false)
	private void renderHudLayers(float partialTicks, LevelRenderer worldrendererin, boolean menuhandright, boolean menuhandleft, PoseStack pMatrix, CallbackInfo cbi) {
		OverlayManager.renderLayers(l -> render2D(partialTicks, l.getFramebuffer(), l.getPos(), l.getRotation(), false, pMatrix));
	}

	@Inject(at = @At(value = "INVOKE", target = "Lorg/vivecraft/client_vr/gameplay/screenhandlers/RadialHandler;isShowing()Z", remap = false, ordinal = 1), method = "renderVrFast", remap = false)
	private void renderHudLayers(float partialTicks, boolean secondpass, boolean menuright, boolean menuleft, PoseStack pMatrix, CallbackInfo cbi) {
		OverlayManager.renderLayers(l -> render2D(partialTicks, l.getFramebuffer(), l.getPos(), l.getRotation(), !shouldOccludeGui(), pMatrix));
	}

	@Inject(at = @At(value = "INVOKE", target = "Lorg/vivecraft/client_vr/gameplay/screenhandlers/RadialHandler;isShowing()Z", remap = false), method = "renderFaceOverlay", remap = false)
	private void renderHudLayers(float partialTicks, PoseStack pMatrix, CallbackInfo cbi) {
		OverlayManager.renderLayers(l -> render2D(partialTicks, l.getFramebuffer(), l.getPos(), l.getRotation(), true, pMatrix));
	}

	@Inject(at = @At("HEAD"), method = "shouldRenderCrosshair", remap = false, cancellable = true)
	private void shouldRenderCrosshair(CallbackInfoReturnable<Boolean> cbi) {
		if (OverlayManager.isUsingController())cbi.setReturnValue(false);
	}
}
