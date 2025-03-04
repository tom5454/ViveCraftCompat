package com.tom.vivecraftcompat.mixin.compat.create;

import java.util.Vector;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.vivecraft.client_vr.ClientDataHolderVR;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.player.Input;
import net.neoforged.neoforge.client.event.MovementInputUpdateEvent;
import net.neoforged.neoforge.common.NeoForge;

import com.simibubi.create.content.contraptions.actors.trainControls.ControlsHandler;
import com.simibubi.create.content.redstone.link.controller.LinkedControllerClientHandler;
import com.simibubi.create.content.redstone.link.controller.LinkedControllerClientHandler.Mode;
import com.simibubi.create.foundation.utility.ControlsUtil;

import com.tom.vivecraftcompat.VRMode;

@Mixin(ControlsUtil.class)
public class CreateControlsUtilMixin {
	private static final ClientDataHolderVR DATA_HOLDER = ClientDataHolderVR.getInstance();
	private static Vector<KeyMapping> patchedControls;
	private static float lastFwd, lastLeft;

	private static Vector<KeyMapping> getPatchedControls() {
		if (patchedControls == null) {
			Options gameSettings = Minecraft.getInstance().options;
			patchedControls = new Vector<>(4);
			patchedControls.add(gameSettings.keyUp);
			patchedControls.add(gameSettings.keyDown);
			patchedControls.add(gameSettings.keyLeft);
			patchedControls.add(gameSettings.keyRight);
		}
		return patchedControls;
	}

	@Inject(at = @At("HEAD"), method = "isActuallyPressed", remap = false, cancellable = true)
	private static void vrActuallyPressed(KeyMapping kb, CallbackInfoReturnable<Boolean> cbi) {
		if(VRMode.isVR() && !DATA_HOLDER.vrSettings.seated && getPatchedControls().contains(kb)) {
			Minecraft mc = Minecraft.getInstance();
			if(mc.player != null) {
				cbi.setReturnValue(isMovementPressed(kb));
			}
		}
	}

	static {
		NeoForge.EVENT_BUS.addListener(CreateControlsUtilMixin::vrInputEvent);
	}

	private static void vrInputEvent(MovementInputUpdateEvent event) {
		if(VRMode.isVR() && !DATA_HOLDER.vrSettings.seated) {
			if(ControlsHandler.getContraption() != null || LinkedControllerClientHandler.MODE != Mode.IDLE) {
				Input in = event.getInput();
				lastFwd = in.forwardImpulse;
				lastLeft = in.leftImpulse;
				in.leftImpulse = 0;
				in.forwardImpulse = 0;
			} else {
				lastFwd = 0;
				lastLeft = 0;
			}
		}
	}

	private static boolean isMovementPressed(KeyMapping key) {
		Options s = Minecraft.getInstance().options;
		if(s.keyUp == key)return lastFwd > 0.5f;
		if(s.keyDown == key)return lastFwd < -0.5f;
		if(s.keyLeft == key)return lastLeft > 0.5f;
		if(s.keyRight == key)return lastLeft < -0.5f;
		return false;
	}
}
