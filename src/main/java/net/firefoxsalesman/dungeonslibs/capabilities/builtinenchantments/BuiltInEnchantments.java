package net.firefoxsalesman.dungeonslibs.capabilities.builtinenchantments;

import net.minecraft.core.registries.Registries;

import com.google.common.collect.Lists;

import net.firefoxsalesman.dungeonslibs.items.gearconfig.ArmorGear;
import net.firefoxsalesman.dungeonslibs.items.gearconfig.BowGear;
import net.firefoxsalesman.dungeonslibs.items.gearconfig.CrossbowGear;
import net.firefoxsalesman.dungeonslibs.items.gearconfig.MeleeGear;
import net.firefoxsalesman.dungeonslibs.items.gearconfig.MeleeGearConfigRegistry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.neoforged.neoforge.common.util.INBTSerializable;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceKey;

import java.util.*;
import java.util.stream.Collectors;

public class BuiltInEnchantments implements INBTSerializable<CompoundTag> {
	private final Map<ResourceLocation, List<EnchantmentInstance>> enchantments = new HashMap<>();

	public BuiltInEnchantments() {
	}

	public BuiltInEnchantments(ItemStack itemStack) {
		if (itemStack.getItem() instanceof MeleeGear item) {
			enchantments.put(MeleeGearConfigRegistry.GEAR_CONFIG_BUILTIN_RESOURCELOCATION,
					new ArrayList<>(item.getGearConfig().getBuiltInEnchantments()));
		}
		if (itemStack.getItem() instanceof BowGear item) {
			enchantments.put(MeleeGearConfigRegistry.GEAR_CONFIG_BUILTIN_RESOURCELOCATION,
					new ArrayList<>(item.getGearConfig().getBuiltInEnchantments()));
		}
		if (itemStack.getItem() instanceof CrossbowGear item) {
			enchantments.put(MeleeGearConfigRegistry.GEAR_CONFIG_BUILTIN_RESOURCELOCATION,
					new ArrayList<>(item.getGearConfig().getBuiltInEnchantments()));
		}
		if (itemStack.getItem() instanceof ArmorGear item) {
			List<EnchantmentInstance> builtInEnchantments = item.getGearConfig().getBuiltInEnchantments()
					.stream()
					.filter(enchantmentInstance -> enchantmentInstance.enchantment.value()
							.canEnchant(itemStack))
					.toList();
			enchantments.put(MeleeGearConfigRegistry.GEAR_CONFIG_BUILTIN_RESOURCELOCATION,
					builtInEnchantments);
		}
	}

	public boolean addBuiltInEnchantment(ResourceLocation source, EnchantmentInstance enchantmentInstance) {
		if (enchantments.get(source) == null)
			enchantments.put(source, new ArrayList<>());
		enchantments.get(source).add(enchantmentInstance);
		return true;
	}

	public boolean removeBuiltInEnchantment(ResourceLocation source, Enchantment enchantment) {
		if (!enchantments.containsKey(source)) {
			return false;
		}
		enchantments.put(source,
				enchantments.get(source).stream().filter(
						enchantmentInstance -> enchantmentInstance.enchantment.value() != enchantment)
						.collect(Collectors.toList()));
		return true;
	}

	public boolean setBuiltInEnchantments(ResourceLocation source, List<EnchantmentInstance> enchantmentInstance) {
		enchantments.put(source, new ArrayList<>(enchantmentInstance));
		return true;
	}

	public boolean clearAllBuiltInEnchantments(ResourceLocation source) {
		enchantments.remove(source);
		return true;
	}

	public List<EnchantmentInstance> getBuiltInEnchantments(ResourceLocation source) {
		List<EnchantmentInstance> enchantmentInstance = enchantments.get(source);
		if (enchantmentInstance == null) {
			return Lists.newArrayList();
		}
		return enchantmentInstance;
	}

	public List<EnchantmentInstance> getAllBuiltInEnchantmentInstances() {
		List<EnchantmentInstance> result = new ArrayList<>();
		enchantments.values().forEach(result::addAll);
		return result;
	}

	public Map<ResourceLocation, List<EnchantmentInstance>> getAllBuiltInEnchantmentInstancesPerSource() {
		return enchantments;
	}

	public boolean hasBuiltInEnchantment(ResourceLocation source) {
		return !getBuiltInEnchantments(source).isEmpty();
	}

	public boolean hasBuiltInEnchantment() {
		return !enchantments.isEmpty();
	}

	public boolean hasBuiltInEnchantment(Enchantment enchantment) {
		return getAllBuiltInEnchantmentInstances().stream()
				.anyMatch(enchantmentInstance -> enchantmentInstance.enchantment.value().equals(enchantment));
	}

	public int getBuiltInItemEnchantmentLevel(Enchantment enchantment) {
		return getAllBuiltInEnchantmentInstances().stream()
				.filter(enchantmentInstance -> enchantmentInstance.enchantment.value().equals(enchantment))
				.map(enchantmentInstance -> enchantmentInstance.level).max(Comparator.naturalOrder())
				.orElse(0);
	}

	public static final String ENCHANTS_KEY = "BuiltInEnchantments";
	public static final String SOURCE_KEY = "source";
	public static final String ENCHANTMENT_DATA_KEY = "data";

	@Override
	public CompoundTag serializeNBT(HolderLookup.Provider provider) {
		CompoundTag tag = new CompoundTag();
		ListTag listnbt = new ListTag();
		getAllBuiltInEnchantmentInstancesPerSource().forEach((resourceLocation, enchantmentInstances) -> {
			CompoundTag compoundnbt = new CompoundTag();
			compoundnbt.putString(SOURCE_KEY, String.valueOf(resourceLocation));
			ListTag enchantmentListnbt = new ListTag();
			enchantmentInstances.forEach(enchantmentInstance -> {
				CompoundTag enchantmentInstanceNBT = new CompoundTag();
				enchantmentInstanceNBT.putString("id", String.valueOf(
						enchantmentInstance.enchantment.unwrapKey().map(ResourceKey::location).orElse(null)));
				enchantmentInstanceNBT.putShort("lvl", (short) enchantmentInstance.level);
				enchantmentListnbt.add(enchantmentInstanceNBT);
			});
			compoundnbt.put(ENCHANTMENT_DATA_KEY, enchantmentListnbt);
			listnbt.add(compoundnbt);
		});
		if (!enchantments.isEmpty()) {
			tag.put(ENCHANTS_KEY, listnbt);
		}
		return tag;
	}

	@Override
	public void deserializeNBT(HolderLookup.Provider provider, CompoundTag tag) {
		if (tag.contains(ENCHANTS_KEY, 10)) {
			ListTag listNBT = tag.getList(ENCHANTS_KEY, 10);
			HolderLookup.RegistryLookup<Enchantment> enchantmentLookup = provider.lookupOrThrow(Registries.ENCHANTMENT);
			for (int i = 0; i < listNBT.size(); ++i) {
				CompoundTag compoundnbt = listNBT.getCompound(i);
				ResourceLocation resourcelocation = ResourceLocation
						.tryParse(compoundnbt.getString(SOURCE_KEY));
				List<EnchantmentInstance> enchantmentInstanceList = new ArrayList<>();
				ListTag enchantmentListnbt = compoundnbt.getList(ENCHANTMENT_DATA_KEY, 10);
				for (int j = 0; j < enchantmentListnbt.size(); ++j) {
					CompoundTag enchantmentNBT = enchantmentListnbt.getCompound(j);
					ResourceLocation enchantmentId = ResourceLocation.tryParse(enchantmentNBT.getString("id"));
					if (enchantmentId == null) {
						continue;
					}
					short level = enchantmentNBT.getShort("lvl");
					enchantmentLookup.get(ResourceKey.create(Registries.ENCHANTMENT, enchantmentId))
							.ifPresent(holder -> enchantmentInstanceList.add(new EnchantmentInstance(holder, level)));
				}
				setBuiltInEnchantments(resourcelocation, enchantmentInstanceList);
			}
		}
	}
}
