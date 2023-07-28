package com.tom.vivecraftcompat.mixin.compat.firstpersonmod;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.vivecraft.render.VRPlayerModel_WithArms;
import org.vivecraft.render.VRPlayerRenderer;

import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;

import dev.tr7zw.firstperson.FirstPersonModelCore;

@Mixin(VRPlayerRenderer.class)
public class FPMVRPlayerRendererMixin extends PlayerRenderer {

	public FPMVRPlayerRendererMixin(Context pContext, boolean pUseSlimModel) {
		super(pContext, pUseSlimModel);
	}

	@Inject(at = @At("RETURN"), method = "setModelProperties")
	public void onSetModelProperties(AbstractClientPlayer pClientPlayer, CallbackInfo cbi) {
		if (FirstPersonModelCore.isRenderingPlayer && getModel() instanceof VRPlayerModel_WithArms<AbstractClientPlayer> model) {
			model.rightHand.visible = false;
			model.leftHand.visible = false;
		}
	}
}
