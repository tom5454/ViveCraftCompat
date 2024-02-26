package com.tom.vivecraftcompat.mixin.compat.firstperson;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;

import com.mojang.blaze3d.vertex.PoseStack;

import dev.tr7zw.firstperson.FirstPersonModelCore;
import dev.tr7zw.firstperson.api.FirstPersonAPI;

@Mixin(value = ItemInHandRenderer.class, priority = 2000)
public class FPMItemInHandMixin {

	@Inject(at = @At("HEAD"), method = "vivecraft$vrRenderArmWithItem", remap = false, cancellable = true)
	private void onVrRenderArmWithItem(AbstractClientPlayer pPlayer, float pPartialTicks, float pPitch, InteractionHand pHand, float pSwingProgress, ItemStack pStack, float pEquippedProgress, PoseStack pMatrixStack, MultiBufferSource pBuffer, int pCombinedLight, CallbackInfo cbi) {
		if (FirstPersonAPI.isEnabled() && !FirstPersonModelCore.instance.getLogicHandler().showVanillaHands() && !FirstPersonModelCore.instance.getConfig().doubleHands) {
			cbi.cancel();
		}
	}
}
