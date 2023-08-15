package com.tom.vivecraftcompat.overlay;

import java.util.function.Function;

import org.vivecraft.client_vr.ClientDataHolderVR;
import org.vivecraft.client_vr.VRData.VRDevicePose;
import org.vivecraft.common.utils.math.Matrix4f;

import net.minecraft.world.phys.Vec3;

public enum OverlayLock {
	FLOAT,
	HMD(d -> LockedPosition.pose(d.vrPlayer.vrdata_room_pre.hmd, 2f)),
	LEFT_HAND(d -> LockedPosition.pose(d.vrPlayer.vrdata_room_pre.getController(1), 1.2f)),
	RIGHT_HAND(d -> LockedPosition.pose(d.vrPlayer.vrdata_room_pre.getController(0), 1.2f)),
	BODY(d -> new LockedPosition(HMD.getLocked().getPosition())),
	;
	private static final ClientDataHolderVR DATA_HOLDER = ClientDataHolderVR.getInstance();
	private Function<ClientDataHolderVR, LockedPosition> locked;

	private OverlayLock() {}

	private OverlayLock(Function<ClientDataHolderVR, LockedPosition> locked) {
		this.locked = locked;
	}

	public LockedPosition getLocked() {
		return locked == null ? null : locked.apply(DATA_HOLDER);
	}

	public static class LockedPosition {
		private Vec3 pos;
		private Matrix4f matrix;

		public static LockedPosition pose(VRDevicePose d, float f) {
			return new LockedPosition(d.getPosition(), d.getMatrix());
		}

		public LockedPosition(Vec3 pos, Matrix4f matrix) {
			this.pos = pos;
			this.matrix = matrix;
		}

		public LockedPosition(Vec3 pos) {
			this.pos = pos;
			this.matrix = new Matrix4f();
		}

		public Vec3 getPosition() {
			return pos;
		}

		public Matrix4f getMatrix() {
			return matrix;
		}
	}

	public static OverlayLock byName(String lock) {
		OverlayLock[] values = values();
		for (int i = 0; i < values.length; i++) {
			OverlayLock overlayLock = values[i];
			if(overlayLock.name().equalsIgnoreCase(lock))
				return overlayLock;
		}
		return FLOAT;
	}
}
