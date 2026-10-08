package net.firefoxsalesman.dungeonslibs.capabilities.timers;

import net.minecraft.core.registries.Registries;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.enchantment.Enchantment;
import net.neoforged.neoforge.common.util.INBTSerializable;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;

public class Timers implements INBTSerializable<CompoundTag> {
	private final Map<ResourceLocation, Integer> enchantmentTimers = new HashMap<>();

	public int getEnchantmentTimer(RegistryAccess registries, Enchantment enchantment) {
		return enchantmentTimers.computeIfAbsent(registries.registryOrThrow(Registries.ENCHANTMENT).getKey(enchantment),
				resourceLocation -> -1);
	}

	public boolean setEnchantmentTimer(RegistryAccess registries, Enchantment enchantment, int value) {
		enchantmentTimers.put(registries.registryOrThrow(Registries.ENCHANTMENT).getKey(enchantment), value);
		return true;
	}

	public boolean setEnchantmentTimer(ResourceLocation enchantment, int value) {
		enchantmentTimers.put(enchantment, value);
		return true;
	}

	public boolean tickTimers() {
		enchantmentTimers.replaceAll((resourceLocation, integer) -> integer > 0 ? integer - 1 : integer);
		return true;
	}

	public Map<ResourceLocation, Integer> getEnchantmentTimers() {
		return enchantmentTimers;
	}

	public static final String ENCHANTS_KEY = "EnchantmentTimers";
	public static final String ENCHANTMENT_KEY = "Enchantment";
	public static final String TIMER_KEY = "Timer";

	@Override
	public CompoundTag serializeNBT(HolderLookup.Provider provider) {
		CompoundTag tag = new CompoundTag();
		ListTag listnbt = new ListTag();
		this.getEnchantmentTimers().forEach((resourceLocation, timer) -> {
			CompoundTag compoundnbt = new CompoundTag();
			compoundnbt.putString(ENCHANTMENT_KEY, resourceLocation.toString());
			compoundnbt.putInt(TIMER_KEY, timer);
			listnbt.add(compoundnbt);
		});
		tag.put(ENCHANTS_KEY, listnbt);
		return tag;
	}

	@Override
	public void deserializeNBT(HolderLookup.Provider provider, CompoundTag tag) {
		ListTag listNBT = tag.getList(ENCHANTS_KEY, 10);
		for (int i = 0; i < listNBT.size(); ++i) {
			CompoundTag compoundnbt = listNBT.getCompound(i);
			ResourceLocation resourcelocation = ResourceLocation
					.tryParse(compoundnbt.getString(ENCHANTMENT_KEY));
			int timer = compoundnbt.getInt(TIMER_KEY);
			this.setEnchantmentTimer(resourcelocation, timer);
		}
	}
}
