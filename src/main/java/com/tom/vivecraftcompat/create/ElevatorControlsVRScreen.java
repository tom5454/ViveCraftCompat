package com.tom.vivecraftcompat.create;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
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
import com.simibubi.create.foundation.utility.Couple;
import com.simibubi.create.foundation.utility.IntAttached;

import com.tom.vivecraftcompat.access.CVSS;

public class ElevatorControlsVRScreen extends ValueSettingsScreen implements CVSS {
	private ElevatorFloorSelection selection;
	private ElevatorContraption elevator;
	private MovementContext ctx;

	public ElevatorControlsVRScreen(ElevatorContraption elevator, ElevatorFloorSelection selection, MovementContext ctx) {
		super(null, makeBoard(elevator), new ValueSettings(elevator.namesList.size() - selection.currentIndex - 1, 0), v -> {});
		this.selection = selection;
		this.elevator = elevator;
		this.ctx = ctx;
	}

	private static ValueSettingsBoard makeBoard(ElevatorContraption ec) {
		List<Component> rows = ec.namesList.stream().map(e -> (Component) new TextComponent(e.getSecond().getFirst())).collect(Collectors.toList());
		Collections.reverse(rows);
		ValueSettingsFormatter format = new ValueSettingsFormatter(s -> {
			int i = Mth.clamp(ec.namesList.size() - s.row() - 1, 0, ec.namesList.size() - 1);
			IntAttached<Couple<String>> entry = ec.namesList.get(i);
			return new TextComponent(entry.getSecond().getSecond());
		});
		return new ValueSettingsBoard(new TranslatableComponent("vivecraftcompat.gui.create.elevator.floor_select"), 0, 1, rows, format);
	}

	@Override
	public void saveAndClose(double pMouseX, double pMouseY) {
		ValueSettings closest = getClosestCoordinate((int) pMouseX, (int) pMouseY);

		selection.currentIndex  = Mth.clamp(elevator.namesList.size() - closest.row() - 1, 0, elevator.namesList.size() - 1);
		ContraptionControlsMovement.tickFloorSelection(selection, elevator);

		if (selection.currentTargetY == elevator.clientYTarget)
			return;

		AllPackets.getChannel().sendToServer(new ElevatorTargetFloorPacket(elevator.entity, selection.currentTargetY));
		if (elevator.presentBlockEntities.get(ctx.localPos) instanceof ContraptionControlsBlockEntity cbe)
			cbe.pressButton();

		onClose();
	}
}
