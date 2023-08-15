package com.tom.vivecraftcompat.overlay;

import java.util.function.Predicate;

import net.minecraft.client.Minecraft;

public enum OverlayEnable {
	ALWAYS(mc -> true),
	INGAME(mc -> mc.screen == null),
	DISABLE(mc -> false),
	;
	private final Predicate<Minecraft> isEnabled;

	private OverlayEnable(Predicate<Minecraft> isEnabled) {
		this.isEnabled = isEnabled;
	}

	public boolean test(Minecraft t) {
		return isEnabled.test(t);
	}

	public static OverlayEnable byName(String name) {
		OverlayEnable[] values = values();
		for (int i = 0; i < values.length; i++) {
			OverlayEnable overlayEn = values[i];
			if(overlayEn.name().equalsIgnoreCase(name))
				return overlayEn;
		}
		return ALWAYS;
	}
}
