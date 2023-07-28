package com.tom.vivecraftcompat.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.vivecraft.provider.openvr_jna.control.VivecraftMovementInput;

import net.minecraft.client.player.Input;

import net.minecraftforge.common.MinecraftForge;

import com.tom.vivecraftcompat.events.VRMovementInputEvent;

@Mixin(VivecraftMovementInput.class)
public class VivecraftMovementInputMixin extends Input {

	@Inject(at = @At("TAIL"), method = "tick")
	public void onTick(boolean isSneaking, float sneakSpeed, CallbackInfo cbi) {
		VRMovementInputEvent event = new VRMovementInputEvent(leftImpulse, forwardImpulse);
		MinecraftForge.EVENT_BUS.post(event);

		if(event.isCanceled()) {
			this.leftImpulse = event.newLeftImpulse;
			this.forwardImpulse = event.newForwardImpulse;
		}
	}
}
