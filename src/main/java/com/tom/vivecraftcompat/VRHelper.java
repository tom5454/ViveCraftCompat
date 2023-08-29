package com.tom.vivecraftcompat;

import org.vivecraft.client_vr.ClientDataHolderVR;

import net.minecraft.world.phys.Vec3;

public class VRHelper {
	private static final ClientDataHolderVR DATA_HOLDER = ClientDataHolderVR.getInstance();

	public static boolean isVRPlayerInitialized() {
		return DATA_HOLDER.vrPlayer != null && DATA_HOLDER.vrPlayer.vrdata_world_render != null;
	}

	public static boolean isStanding() {
		return !DATA_HOLDER.vrSettings.seated;
	}

	public static Vec3 getRayOrigin() {
		return DATA_HOLDER.vrPlayer.vrdata_world_render.getController(0).getPosition();
	}

	public static Vec3 getRayDirection() {
		return DATA_HOLDER.vrPlayer.vrdata_world_render.getController(0).getDirection();
	}
}
