package net.firefoxsalesman.dungeonslibs.capabilities.artifact;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class ArtifactUsageHelper {

	public static ArtifactUsage getArtifactUsageCapability(Entity entity) {
		return AttacherArtifactUsage.get(entity).orElse(new ArtifactUsage());
	}

	public static boolean startUsingArtifact(Player playerIn, ArtifactUsage cap, ItemStack itemstack) {
		boolean result = cap.startUsingArtifact(itemstack, playerIn);
		return result;
	}
}
