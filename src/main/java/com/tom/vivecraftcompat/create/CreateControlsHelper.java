package com.tom.vivecraftcompat.create;

import java.util.Vector;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.player.Input;

import com.simibubi.create.content.contraptions.actors.trainControls.ControlsHandler;
import com.simibubi.create.content.redstone.link.controller.LinkedControllerClientHandler;
import com.simibubi.create.content.redstone.link.controller.LinkedControllerClientHandler.Mode;

import com.tom.vivecraftcompat.VRMode;

public class CreateControlsHelper {
	private static float lastFwd, lastLeft;
	private static Vector<KeyMapping> patchedControls;

	public static Vector<KeyMapping> getPatchedControls() {
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

	public static void vrInputEvent(Input in) {
		if(VRMode.isVRStanding()) {
			if(ControlsHandler.getContraption() != null || LinkedControllerClientHandler.MODE != Mode.IDLE) {
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

	public static boolean isMovementPressed(KeyMapping key) {
		Options s = Minecraft.getInstance().options;
		if(s.keyUp == key)return lastFwd > 0.5f;
		if(s.keyDown == key)return lastFwd < -0.5f;
		if(s.keyLeft == key)return lastLeft > 0.5f;
		if(s.keyRight == key)return lastLeft < -0.5f;
		return false;
	}
}
