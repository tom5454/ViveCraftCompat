package com.tom.vivecraftcompat.mixin.compat.create;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import com.simibubi.create.foundation.blockEntity.behaviour.ValueSettingsScreen;
import com.simibubi.create.foundation.gui.AbstractSimiScreen;
import com.simibubi.create.foundation.utility.Lang;

import com.tom.vivecraftcompat.VRMode;

@Mixin(ValueSettingsScreen.class)
public abstract class CreateValueSettingsScreenMixin extends AbstractSimiScreen {
	@Shadow(remap = false) abstract void saveAndClose(double pMouseX, double pMouseY);

	@Inject(at = @At("HEAD"), method = "keyReleased(III)Z", cancellable = true)
	public void onKeyReleased(int pKeyCode, int pScanCode, int pModifiers, CallbackInfoReturnable<Boolean> cbi) {
		if (VRMode.isVR())cbi.setReturnValue(super.keyPressed(pKeyCode, pScanCode, pModifiers));
	}

	@Inject(at = @At("HEAD"), method = "mouseReleased(DDI)Z", cancellable = true)
	public void onMouseReleased(double pMouseX, double pMouseY, int pButton, CallbackInfoReturnable<Boolean> cbi) {
		if (VRMode.isVR())cbi.setReturnValue(super.mouseReleased(pMouseX, pMouseY, pButton));
	}

	@Override
	public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
		if (VRMode.isVR() && pButton == 0) {
			saveAndClose(pMouseX, pMouseY);
			return true;
		}
		return super.mouseClicked(pMouseX, pMouseY, pButton);
	}

	@Redirect(at = @At(value = "INVOKE", target = "Lcom/simibubi/create/foundation/utility/Lang;translateDirect(Ljava/lang/String;[Ljava/lang/Object;)Lnet/minecraft/network/chat/MutableComponent;"), method = "renderWindow", remap = false)
	public MutableComponent replaceReleaseButtonTooltip(String oldKey, Object... oldArgs) {
		return VRMode.isVR() ? Component.translatable("vivecraftcompat.gui.create.value_settings.click_to_confirm") : Lang.translateDirect(oldKey, oldArgs);
	}
}
