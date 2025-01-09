package com.tom.vivecraftcompat;

import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.gui.overlay.ForgeGui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import mcjty.theoneprobe.api.ProbeMode;
import mcjty.theoneprobe.config.Config;
import mcjty.theoneprobe.items.ModItems;
import mcjty.theoneprobe.keys.KeyBindings;
import mcjty.theoneprobe.rendering.OverlayRenderer;

public class TOPOverlay {
	public static void register(RegisterGuiOverlaysEvent event) {
		event.registerAboveAll("theoneprobe", TOPOverlay::render);
	}

	public static void render(ForgeGui gui, PoseStack poseStack, float partialTick, int screenWidth, int screenHeight) {
		var stack = RenderSystem.getModelViewStack();
		stack.pushPose();
		poseStack.pushPose();
		poseStack.setIdentity();
		if (Config.holdKeyToMakeVisible.get()) {
			if (!KeyBindings.toggleVisible.isDown()) {
				return;
			}
		} else if (!(Boolean) Config.isVisible.get()) {
			return;
		}

		if (hasItemInEitherHand(ModItems.CREATIVE_PROBE)) {
			OverlayRenderer.renderHUD(ProbeMode.DEBUG, poseStack, partialTick);
		} else {
			switch (Config.needsProbe.get()) {
			case 0 :
			case 3 :
				OverlayRenderer.renderHUD(getModeForPlayer(), poseStack,
						partialTick);
				break;
			case 1 :
			case 2 :
				if (ModItems.hasAProbeSomewhere(Minecraft.getInstance().player)) {
					OverlayRenderer.renderHUD(getModeForPlayer(), poseStack,
							partialTick);
				}
			}
		}
		stack.popPose();
		poseStack.popPose();
		RenderSystem.applyModelViewMatrix();
	}

	private static ProbeMode getModeForPlayer() {
		Player player = Minecraft.getInstance().player;
		if (Config.extendedInMain.get() && hasItemInMainHand(ModItems.PROBE)) {
			return ProbeMode.EXTENDED;
		} else {
			return player.isShiftKeyDown() ? ProbeMode.EXTENDED : ProbeMode.NORMAL;
		}
	}

	private static boolean hasItemInEitherHand(Item item) {
		ItemStack mainHeldItem = Minecraft.getInstance().player.getItemInHand(InteractionHand.MAIN_HAND);
		ItemStack offHeldItem = Minecraft.getInstance().player.getItemInHand(InteractionHand.OFF_HAND);
		return mainHeldItem.getItem() == item || offHeldItem.getItem() == item;
	}

	private static boolean hasItemInMainHand(Item item) {
		ItemStack mainHeldItem = Minecraft.getInstance().player.getItemInHand(InteractionHand.MAIN_HAND);
		return mainHeldItem.getItem() == item;
	}
}
