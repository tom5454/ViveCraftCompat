package com.tom.vivecraftcompat.mixin.compat.create;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;

import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import com.simibubi.create.content.contraptions.actors.contraptionControls.ContraptionControlsMovement.ElevatorFloorSelection;
import com.simibubi.create.content.contraptions.actors.contraptionControls.ContraptionControlsMovingInteraction;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import com.simibubi.create.content.contraptions.elevator.ElevatorContraption;

import com.tom.vivecraftcompat.VRMode;
import com.tom.vivecraftcompat.create.ElevatorControlsVRScreen;

@Mixin(ContraptionControlsMovingInteraction.class)
public class CreateContraptionControlsMovingInteractionMixin {

	@Inject(at = @At("HEAD"), method = "elevatorInteraction", remap = false, cancellable = true)
	private void onElevatorInteraction(BlockPos localPos, AbstractContraptionEntity contraptionEntity,
			ElevatorContraption contraption, MovementContext ctx, CallbackInfoReturnable<Boolean> cbi) {
		if (contraptionEntity.level().isClientSide()) {
			if (VRMode.isVRStanding()) {
				if (!(ctx.temporaryData instanceof ElevatorFloorSelection efs)) {
					cbi.setReturnValue(false);
					return;
				}

				Minecraft.getInstance().setScreen(new ElevatorControlsVRScreen(contraption, efs, ctx));

				cbi.setReturnValue(true);
			}
		}
	}
}
