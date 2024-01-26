package com.tom.vivecraftcompat.mixin.compat.firstperson;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.vivecraft.client.render.VRPlayerModel_WithArms;
import org.vivecraft.client.render.VRPlayerRenderer;

import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;

import dev.tr7zw.firstperson.FirstPersonModelCore;
import dev.tr7zw.firstperson.api.FirstPersonAPI;

@Mixin(VRPlayerRenderer.class)
public class FPMVRPlayerRendererMixin extends PlayerRenderer {

	public FPMVRPlayerRendererMixin(Context pContext, boolean pUseSlimModel) {
		super(pContext, pUseSlimModel);
	}

	@Inject(at = @At("RETURN"), method = "setModelProperties")
	public void onSetModelProperties(AbstractClientPlayer pClientPlayer, CallbackInfo cbi) {
		if (FirstPersonAPI.isRenderingPlayer() && getModel() instanceof VRPlayerModel_WithArms<AbstractClientPlayer> model) {
			boolean v = !FirstPersonModelCore.instance.getLogicHandler().showVanillaHands();
			model.rightHand.visible = v;
			model.leftHand.visible = v;
		}
	}
}
