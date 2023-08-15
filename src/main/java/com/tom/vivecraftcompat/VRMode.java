package com.tom.vivecraftcompat;

import org.vivecraft.client_vr.ClientDataHolderVR;
import org.vivecraft.client_vr.VRState;

public class VRMode {
	private static final ClientDataHolderVR DATA_HOLDER = ClientDataHolderVR.getInstance();

	public static boolean isVR() {
		return VRState.vrRunning && DATA_HOLDER.vrPlayer != null && DATA_HOLDER.vrPlayer.vrdata_world_render != null;
	}
}
