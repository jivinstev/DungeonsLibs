package net.firefoxsalesman.dungeonslibs.data;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

import java.util.ArrayList;
import java.util.List;

public class Codecs {

	// Enchantments are a datapack registry, so gear configs store the id and resolve it at use time.
	public static final Codec<EnchantmentData> ENCHANTMENT_DATA_CODEC = EnchantmentData.CODEC;

	public static List<EnchantmentInstance> resolveEnchantments(List<EnchantmentData> data,
			HolderLookup.Provider registries) {
		List<EnchantmentInstance> result = new ArrayList<>();
		registries.lookup(Registries.ENCHANTMENT).ifPresent(lookup -> {
			for (EnchantmentData entry : data) {
				lookup.get(ResourceKey.create(Registries.ENCHANTMENT, entry.enchantment()))
						.ifPresent(holder -> result.add(new EnchantmentInstance(holder, entry.level())));
			}
		});
		return result;
	}

	public static List<EnchantmentInstance> resolveEnchantments(List<EnchantmentData> data) {
		MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
		if (server == null)
			return new ArrayList<>();
		return resolveEnchantments(data, server.registryAccess());
	}

	public static final Codec<Rarity> ITEM_RARITY_CODEC = Codec.STRING.flatComapMap(Rarity::valueOf,
			d -> DataResult.success(d.name()));

}
