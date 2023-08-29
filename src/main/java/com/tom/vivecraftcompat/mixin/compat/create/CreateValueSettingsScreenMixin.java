package com.tom.vivecraftcompat.mixin.compat.create;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TranslatableComponent;

import com.simibubi.create.AllKeys;
import com.simibubi.create.AllPackets;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueSettingsBehaviour.ValueSettings;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueSettingsPacket;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueSettingsScreen;
import com.simibubi.create.foundation.gui.AbstractSimiScreen;
import com.simibubi.create.foundation.utility.Lang;

import com.tom.vivecraftcompat.VRMode;
import com.tom.vivecraftcompat.access.CVSS;

@Mixin(ValueSettingsScreen.class)
public abstract class CreateValueSettingsScreenMixin extends AbstractSimiScreen implements CVSS {
	public @Shadow abstract ValueSettings getClosestCoordinate(int mouseX, int mouseY);
	private @Shadow BlockPos pos;

	@Override
	public void saveAndClose(double pMouseX, double pMouseY) {
		ValueSettings closest = getClosestCoordinate((int) pMouseX, (int) pMouseY);
		// FIXME: value settings may be face-sensitive on future components
		AllPackets.getChannel()
		.sendToServer(new ValueSettingsPacket(pos, closest.row(), closest.value(), null, Direction.UP,
				AllKeys.ctrlDown()));
		onClose();
	}

	/**
	 * @reason Implement 1.19 code
	 * @author tom5454
	 * */
	@Override
	@Overwrite
	public boolean mouseReleased(double pMouseX, double pMouseY, int pButton) {
		if (!VRMode.isVR() && pButton == 1) {
			saveAndClose(pMouseX, pMouseY);
			return true;
		}
		return super.mouseReleased(pMouseX, pMouseY, pButton);
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
		return VRMode.isVR() ? new TranslatableComponent("vivecraftcompat.gui.create.value_settings.click_to_confirm") : Lang.translateDirect(oldKey, oldArgs);
	}
}
