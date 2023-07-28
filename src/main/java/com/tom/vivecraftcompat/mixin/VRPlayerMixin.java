package com.tom.vivecraftcompat.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.vivecraft.gameplay.VRPlayer;

import net.minecraftforge.common.MinecraftForge;

import com.tom.vivecraftcompat.events.VRUpdateControllersEvent;

@Mixin(VRPlayer.class)
public class VRPlayerMixin {

	@Inject(at = @At("RETURN"), method = "postPoll", remap = false)
	public void onPostPoll(CallbackInfo cbi) {
		MinecraftForge.EVENT_BUS.post(new VRUpdateControllersEvent());
	}
}
