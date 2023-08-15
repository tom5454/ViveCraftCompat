package com.tom.vivecraftcompat.mixin;

import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.vivecraft.client_vr.provider.VRRenderer;

import com.tom.vivecraftcompat.overlay.OverlayManager;

@Mixin(VRRenderer.class)
public class VRRendererMixin {

	@Inject(at = @At(value = "FIELD", target = "Lorg/vivecraft/client_vr/gameplay/screenhandlers/GuiHandler;guiFramebuffer:Lcom/mojang/blaze3d/pipeline/RenderTarget;", remap = false, opcode = Opcodes.PUTSTATIC), method = "setupRenderConfiguration", remap = false)
	public void setupRenderConfiguration(CallbackInfo cbi) {
		OverlayManager.reinitBuffers();
	}
}
