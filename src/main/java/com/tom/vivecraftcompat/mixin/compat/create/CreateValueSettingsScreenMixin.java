package com.tom.vivecraftcompat.mixin.compat.create;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import com.simibubi.create.foundation.blockEntity.behaviour.ValueSettingsScreen;
import com.simibubi.create.foundation.gui.AbstractSimiScreen;

@Mixin(ValueSettingsScreen.class)
public abstract class CreateValueSettingsScreenMixin extends AbstractSimiScreen {
	@Shadow(remap = false) abstract void saveAndClose(double pMouseX, double pMouseY);

	/**
	 * @reason fix ui closing immediately in vr
	 * @author tom5454
	 * */
	@Override
	@Overwrite
	public boolean keyReleased(int pKeyCode, int pScanCode, int pModifiers) {
		return super.keyReleased(pKeyCode, pScanCode, pModifiers);
	}

	/**
	 * @reason fix ui closing immediately in vr
	 * @author tom5454
	 * */
	@Override
	@Overwrite
	public boolean mouseReleased(double pMouseX, double pMouseY, int pButton) {
		return super.mouseReleased(pMouseX, pMouseY, pButton);
	}

	@Override
	public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
		if (pButton == 0) {
			saveAndClose(pMouseX, pMouseY);
			return true;
		}
		return super.mouseClicked(pMouseX, pMouseY, pButton);
	}

	@Redirect(at = @At(value = "INVOKE", target = "Lcom/simibubi/create/foundation/utility/Lang;translateDirect(Ljava/lang/String;[Ljava/lang/Object;)Lnet/minecraft/network/chat/MutableComponent;"), method = "renderWindow", remap = false)
	public MutableComponent replaceReleaseButtonTooltip(String oldKey, Object... oldArgs) {
		return Component.translatable("vivecraftcompat.gui.value_settings.click_to_confirm");
	}
}
