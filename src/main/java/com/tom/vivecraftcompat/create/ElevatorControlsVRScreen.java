package com.tom.vivecraftcompat.create;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import net.createmod.catnip.data.Couple;
import net.createmod.catnip.data.IntAttached;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

import com.simibubi.create.AllPackets;
import com.simibubi.create.content.contraptions.actors.contraptionControls.ContraptionControlsBlockEntity;
import com.simibubi.create.content.contraptions.actors.contraptionControls.ContraptionControlsMovement;
import com.simibubi.create.content.contraptions.actors.contraptionControls.ContraptionControlsMovement.ElevatorFloorSelection;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import com.simibubi.create.content.contraptions.elevator.ElevatorContraption;
import com.simibubi.create.content.contraptions.elevator.ElevatorTargetFloorPacket;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueSettingsBehaviour.ValueSettings;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueSettingsBoard;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueSettingsFormatter;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueSettingsScreen;

public class ElevatorControlsVRScreen extends ValueSettingsScreen {
	private ElevatorFloorSelection selection;
	private ElevatorContraption elevator;
	private MovementContext ctx;

	public ElevatorControlsVRScreen(ElevatorContraption elevator, ElevatorFloorSelection selection, MovementContext ctx) {
		super(null, makeBoard(elevator), new ValueSettings(elevator.namesList.size() - selection.currentIndex - 1, 0), v -> {}, 0);
		this.selection = selection;
		this.elevator = elevator;
		this.ctx = ctx;
	}

	private static ValueSettingsBoard makeBoard(ElevatorContraption ec) {
		List<Component> rows = ec.namesList.stream().map(e -> (Component) Component.literal(e.getSecond().getFirst())).collect(Collectors.toList());
		Collections.reverse(rows);
		ValueSettingsFormatter format = new ValueSettingsFormatter(s -> {
			int i = Mth.clamp(ec.namesList.size() - s.row() - 1, 0, ec.namesList.size() - 1);
			IntAttached<Couple<String>> entry = ec.namesList.get(i);
			return Component.literal(entry.getSecond().getSecond());
		});
		return new ValueSettingsBoard(Component.translatable("vivecraftcompat.gui.create.elevator.floor_select"), 0, 1, rows, format);
	}

	@Override
	protected void saveAndClose(double pMouseX, double pMouseY) {
		ValueSettings closest = getClosestCoordinate((int) pMouseX, (int) pMouseY);

		selection.currentIndex  = Mth.clamp(elevator.namesList.size() - closest.row() - 1, 0, elevator.namesList.size() - 1);
		ContraptionControlsMovement.tickFloorSelection(selection, elevator);

		if (selection.currentTargetY == elevator.clientYTarget)
			return;

		AllPackets.getChannel().sendToServer(new ElevatorTargetFloorPacket(elevator.entity, selection.currentTargetY));
		if (elevator.getOrCreateClientContraptionLazy().getBlockEntity(ctx.localPos) instanceof ContraptionControlsBlockEntity cbe)
			cbe.pressButton();

		onClose();
	}
}
