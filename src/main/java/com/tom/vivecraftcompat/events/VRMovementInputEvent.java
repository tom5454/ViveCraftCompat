package com.tom.vivecraftcompat.events;

import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

/**
 * Must cancel the event to set the new values.
 * */
@Cancelable
public class VRMovementInputEvent extends Event {
	private final float leftImpulse, forwardImpulse;
	public float newLeftImpulse, newForwardImpulse;

	public VRMovementInputEvent(float leftImpulse, float forwardImpulse) {
		this.leftImpulse = leftImpulse;
		this.forwardImpulse = forwardImpulse;
		this.newLeftImpulse = leftImpulse;
		this.newForwardImpulse = forwardImpulse;
	}

	public float getLeftImpulse() {
		return leftImpulse;
	}

	public float getForwardImpulse() {
		return forwardImpulse;
	}
}
