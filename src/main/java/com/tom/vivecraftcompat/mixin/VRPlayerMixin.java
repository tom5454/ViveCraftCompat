package com.tom.vivecraftcompat.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.vivecraft.client_vr.gameplay.VRPlayer;

import net.neoforged.neoforge.common.NeoForge;

import com.tom.vivecraftcompat.events.VRUpdateControllersEvent;

@Mixin(VRPlayer.class)
public class VRPlayerMixin {

	@Inject(at = @At("RETURN"), method = "postPoll", remap = false)
	public void onPostPoll(CallbackInfo cbi) {
		NeoForge.EVENT_BUS.post(new VRUpdateControllersEvent());
	}
}
