package com.tom.vivecraftcompat.overlay;

public interface VRInteractableScreen {
	void processGui();
	void processBindings();
	boolean isUsingController();
	boolean type(char ch);
	boolean key(int key);
}