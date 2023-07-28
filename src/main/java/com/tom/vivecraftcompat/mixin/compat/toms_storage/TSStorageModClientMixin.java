package com.tom.vivecraftcompat.mixin.compat.toms_storage;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.vivecraft.ClientDataHolder;
import org.vivecraft.VRState;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.HitResult;

import com.tom.storagemod.StorageModClient;

@Mixin(StorageModClient.class)
public class TSStorageModClientMixin {
	private static final ClientDataHolder DATA_HOLDER = ClientDataHolder.getInstance();

	@Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;pick(DFZ)Lnet/minecraft/world/phys/HitResult;", remap = true), method = "renderWorldOutline", remap = false)
	private static HitResult pickBlock(Player player, double pHitDistance, float pPartialTicks, boolean pHitFluids) {
		if (VRState.isVR && !ClientDataHolder.getInstance().vrSettings.seated)
			return DATA_HOLDER.vrPlayer.rayTraceBlocksVR(DATA_HOLDER.vrPlayer.vrdata_world_render, 0, pHitDistance, pHitFluids);
		else
			return player.pick(pHitDistance, pPartialTicks, pHitFluids);
	}
}
