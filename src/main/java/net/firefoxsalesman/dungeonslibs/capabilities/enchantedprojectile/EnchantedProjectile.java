package net.firefoxsalesman.dungeonslibs.capabilities.enchantedprojectile;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;

import net.firefoxsalesman.dungeonslibs.capabilities.builtinenchantments.BuiltInEnchantments;
import net.firefoxsalesman.dungeonslibs.capabilities.builtinenchantments.BuiltInEnchantmentsHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.EnchantmentInstance;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EnchantedProjectile {
	public static final String ENCHANTMENT_DATA_KEY = "EnchantmentInstance";
	private List<EnchantmentInstance> enchantmentDataList = new ArrayList<>();

	public void setEnchantments(ItemStack itemStack) {
		Map<Holder<Enchantment>, Integer> combined = new HashMap<>();
		EnchantmentHelper.getEnchantmentsForCrafting(itemStack).entrySet()
				.forEach(entry -> combined.merge(entry.getKey(), entry.getIntValue(), Integer::sum));
		BuiltInEnchantments cap = BuiltInEnchantmentsHelper.getBuiltInEnchantmentsCapability(itemStack);
		cap.getAllBuiltInEnchantmentInstances()
				.forEach(instance -> combined.merge(instance.enchantment, instance.level, Integer::sum));
		List<EnchantmentInstance> list = new ArrayList<>();
		combined.forEach((holder, level) -> list.add(new EnchantmentInstance(holder, level)));
		enchantmentDataList = list;
	}

	public int getEnchantmentLevel(Holder<Enchantment> enchantment) {
		return enchantmentDataList.stream()
				.filter(enchantmentData -> enchantment.unwrapKey()
						.map(enchantmentData.enchantment::is).orElse(false))
				.map(enchantmentData -> enchantmentData.level)
				.findFirst()
				.orElse(0);
	}

	public CompoundTag serializeNBT(HolderLookup.Provider provider) {
		CompoundTag nbt = new CompoundTag();
		ListTag enchantmentListnbt = new ListTag();
		enchantmentDataList.forEach(enchantmentData -> {
			CompoundTag enchantmentDataNBT = new CompoundTag();
			enchantmentData.enchantment.unwrapKey().ifPresent(
					key -> enchantmentDataNBT.putString("id", key.location().toString()));
			enchantmentDataNBT.putShort("lvl", (short) enchantmentData.level);
			enchantmentListnbt.add(enchantmentDataNBT);
		});
		nbt.put(ENCHANTMENT_DATA_KEY, enchantmentListnbt);
		return nbt;
	}

	public void deserializeNBT(HolderLookup.Provider provider, CompoundTag tag) {
		List<EnchantmentInstance> list = new ArrayList<>();
		ListTag listTag = tag.getList(ENCHANTMENT_DATA_KEY, 10);
		for (int i = 0; i < listTag.size(); i++) {
			CompoundTag entry = listTag.getCompound(i);
			ResourceLocation id = ResourceLocation.tryParse(entry.getString("id"));
			if (id == null) {
				continue;
			}
			int level = entry.getShort("lvl");
			ResourceKey<Enchantment> key = ResourceKey.create(Registries.ENCHANTMENT, id);
			provider.lookup(Registries.ENCHANTMENT).flatMap(lookup -> lookup.get(key))
					.ifPresent(holder -> list.add(new EnchantmentInstance(holder, level)));
		}
		enchantmentDataList = list;
	}

}
