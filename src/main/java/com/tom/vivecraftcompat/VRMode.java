package com.tom.vivecraftcompat;

import org.vivecraft.client_vr.VRState;

public class VRMode {
	public static boolean isVR() {
		return VRState.VR_RUNNING && VRHelper.isVRPlayerInitialized();
	}

	public static boolean isVRStanding() {
		return isVR() && VRHelper.isStanding();
	}
}
