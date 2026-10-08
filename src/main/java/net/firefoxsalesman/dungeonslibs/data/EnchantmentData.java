package net.firefoxsalesman.dungeonslibs.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.resources.ResourceLocation;

public record EnchantmentData(ResourceLocation enchantment, int level) {
	public static final Codec<EnchantmentData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			ResourceLocation.CODEC.fieldOf("name").forGetter(EnchantmentData::enchantment),
			Codec.INT.optionalFieldOf("level", 1).forGetter(EnchantmentData::level))
			.apply(instance, EnchantmentData::new));
}
