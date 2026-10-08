package net.firefoxsalesman.dungeonslibs.utils;

import net.firefoxsalesman.dungeonslibs.mixin.ItemAccessor;

import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;

/**
 * Kept out of {@link ItemAccessor}: a mixin interface with a non-accessor method can't target a class. Also kept
 * out of the mixin package: Mixin forbids direct references to any non-mixin class in a defined mixin package.
 */
public final class ItemMaxDamage {
	private ItemMaxDamage() {
	}

	/** Max damage is the MAX_DAMAGE data component in 1.21, so rewrite the item's default components. */
	public static void setMaxDamage(Item item, int maxDamage) {
		((ItemAccessor) item).setComponents(DataComponentMap.builder().addAll(item.components())
				.set(DataComponents.MAX_DAMAGE, maxDamage).build());
	}

	/** Rarity is the RARITY data component in 1.21 (Item.getRarity is gone), so rewrite the default components. */
	public static void setRarity(Item item, Rarity rarity) {
		((ItemAccessor) item).setComponents(DataComponentMap.builder().addAll(item.components())
				.set(DataComponents.RARITY, rarity).build());
	}
}
