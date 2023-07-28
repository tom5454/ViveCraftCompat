package com.tom.vivecraftcompat.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.client.gui.components.Button;

import com.tom.vivecraftcompat.access.BTN;

@Mixin(Button.class)
public class ButtonMixin implements BTN {
	protected @Shadow @Mutable @Final Button.OnPress onPress;

	@Override
	public void btn$setOnPress(Button.OnPress onPress) {
		this.onPress = onPress;
	}
}
