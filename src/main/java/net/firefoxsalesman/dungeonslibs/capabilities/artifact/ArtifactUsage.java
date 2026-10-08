package net.firefoxsalesman.dungeonslibs.capabilities.artifact;

import net.firefoxsalesman.dungeonslibs.items.artifacts.ArtifactItem;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.util.INBTSerializable;

public class ArtifactUsage implements INBTSerializable<CompoundTag> {

	private ItemStack usingArtifact = null;
	private int usingArtifactRemaining = 0;

	public boolean isUsingArtifact() {
		return usingArtifact != null;
	}

	public boolean isSameUsingArtifact(ItemStack itemStack) {
		return usingArtifact != null && itemStack != null && itemStack.equals(usingArtifact);
	}

	public boolean startUsingArtifact(ItemStack itemStack, LivingEntity entity) {
		if (usingArtifact != null || !(itemStack.getItem() instanceof ArtifactItem))
			return false;
		usingArtifact = itemStack;
		usingArtifactRemaining = itemStack.getUseDuration(entity);
		return true;
	}

	public boolean stopUsingArtifact() {
		usingArtifact = null;
		usingArtifactRemaining = 0;
		return true;
	}

	public ItemStack getUsingArtifact() {
		return usingArtifact;
	}

	public int getUsingArtifactRemaining() {
		return usingArtifactRemaining;
	}

	public void setUsingArtifactRemaining(int usingArtifactRemaining) {
		this.usingArtifactRemaining = usingArtifactRemaining;
	}

	@Override
	public CompoundTag serializeNBT(HolderLookup.Provider provider) {
		CompoundTag tag = new CompoundTag();
		return tag;
	}

	@Override
	public void deserializeNBT(HolderLookup.Provider provider, CompoundTag tag) {
	}
}
